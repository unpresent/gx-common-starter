package ru.gx.core.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

public abstract class AbstractMessagesFactory implements MessagesFactory {
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    private final Set<MessageCreatingParams> onlyParamsCreateByDataObject;

    @Getter(PROTECTED)
    private final Set<MessageCreatingParams> onlyParamsCreateByDataPackage;

    @Getter(PROTECTED)
    private final String serviceName;

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractMessagesFactory(String serviceName) {
        this.serviceName = serviceName;

        this.onlyParamsCreateByDataObject = new HashSet<>();
        this.onlyParamsCreateByDataObject.add(MessageCreatingParams.BodyDataObject);
        this.onlyParamsCreateByDataObject.add(MessageCreatingParams.ParentId);

        this.onlyParamsCreateByDataPackage = new HashSet<>();
        this.onlyParamsCreateByDataPackage.add(MessageCreatingParams.BodyDataPackage);
        this.onlyParamsCreateByDataPackage.add(MessageCreatingParams.ParentId);
    }

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements MessageFactory">
    @Override
    public boolean isSupportedCreateByParams(@NotNull final String messageType, final int version) {
        final var reg = MessageTypesRegistrator.get(messageType, version);
        return reg.isConstructorByParams();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M extends Message<? extends MessageBody>>
    @NotNull M createByParams(
            @NotNull final Map<MessageCreatingParams, Object> creatingParams,
            @NotNull final String messageType,
            final int version
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var reg = MessageTypesRegistrator.get(messageType, version);

        if (reg.isBodyConstructorByDataObject()
                && reg.isConstructorByHeaderBody()
                && checkParamsIsOnly(creatingParams, this.onlyParamsCreateByDataObject)) {
            return (M) internalCreateMessageByDataObject(
                    reg,
                    (String) creatingParams.get(MessageCreatingParams.ParentId),
                    (DataObject) creatingParams.get(MessageCreatingParams.BodyDataObject),
                    (MessageCorrelation) creatingParams.get(MessageCreatingParams.Correlation)
            );
        }

        if (reg.isBodyConstructorByDataPackage()
                && reg.isConstructorByHeaderBody()
                && checkParamsIsOnly(creatingParams, this.onlyParamsCreateByDataPackage)) {
            return (M) internalCreateMessageByDataPackage(
                    reg,
                    (String) creatingParams.get(MessageCreatingParams.ParentId),
                    (DataPackage<? extends DataObject>) creatingParams.get(MessageCreatingParams.BodyDataPackage),
                    (MessageCorrelation) creatingParams.get(MessageCreatingParams.Correlation)
            );
        }

        if (reg.isBodyConstructorByDataObject()
                && reg.isConstructorByHeaderBody()) {
            final var header = internalCreateHeaderByParams(reg, creatingParams);
            final var body = internalCreateBodyByDataObject(reg, (DataObject) creatingParams.get(MessageCreatingParams.BodyDataObject));
            return internalCreateMessageByHeaderBody(reg, header, body, (MessageCorrelation) creatingParams.get(MessageCreatingParams.Correlation));
        }

        if (reg.isBodyConstructorByDataPackage()
                && reg.isConstructorByHeaderBody()) {
            final var header = internalCreateHeaderByParams(reg, creatingParams);
            final var body = internalCreateBodyByDataPackage(reg, (DataPackage<? extends DataObject>) creatingParams.get(MessageCreatingParams.BodyDataPackage));
            return internalCreateMessageByHeaderBody(reg, header, body, (MessageCorrelation) creatingParams.get(MessageCreatingParams.Correlation));
        }

        return internalCreateMessageByParams(reg, creatingParams);
    }

    @Override
    public boolean isSupportedCreateByDataObject(@NotNull final String messageType, final int version) {
        final var reg = MessageTypesRegistrator.get(messageType, version);
        return reg.isBodyConstructorByDataObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <M extends Message<? extends MessageBody>>
    M createByDataObject(
            @Nullable final String parentId,
            @NotNull final String messageType,
            final int version,
            @Nullable final DataObject dataObject,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var reg = MessageTypesRegistrator.get(messageType, version);
        return (M) internalCreateMessageByDataObject(reg, parentId, dataObject, correlation);
    }

    @Override
    public boolean isSupportedCreateByDataPackage(@NotNull final String messageType, final int version) {
        final var reg = MessageTypesRegistrator.get(messageType, version);
        return reg.isBodyConstructorByDataPackage();
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <M extends Message<? extends MessageBody>, O extends DataObject>
    M createByDataPackage(
            @Nullable final String parentId,
            @NotNull final String messageType,
            final int version,
            @Nullable final DataPackage<O> dataPackage,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var reg = MessageTypesRegistrator.get(messageType, version);
        return (M) internalCreateMessageByDataPackage(reg, parentId, dataPackage, correlation);
    }

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="internal methods">

    /**
     * Создание сообщения по тройке: Заголовку, Телу, Correlation-данным.
     *
     * @param reg         Информация о регистрации типа.
     * @param header      Заголовок сообщения.
     * @param body        Тело  сообщения.
     * @param correlation Correlation-данные.
     * @param <M>         Тип сообщения.
     * @return Экземпляр сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected <M extends Message<? extends MessageBody>>
    M internalCreateMessageByHeaderBody(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @NotNull final MessageHeader header,
            @NotNull final MessageBody body,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var c = reg.getConstructorMessageByHeaderBody();
        if (c == null) {
            throw new MessagingConfigurationException("Message class (" + reg.getMessageClass().getName() + ") of message " + reg.getType() + " does not have constructor by (Header, Body, Correlation)!");
        }
        return (M) c.newInstance(header, body, correlation);
    }


    /**
     * Создание сообщения по тройке: Заголовку, Телу, Correlation-данным.
     *
     * @param reg            Информация о регистрации типа.
     * @param creatingParams Параметры для создания сообщения.
     * @param <M>            Тип сообщения.
     * @return Экземпляр сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected <M extends Message<? extends MessageBody>>
    M internalCreateMessageByParams(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @NotNull final Map<MessageCreatingParams, Object> creatingParams
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var c = reg.getConstructorMessageByParams();
        if (c == null) {
            throw new MessagingConfigurationException("Message class (" + reg.getMessageClass().getName() + ") of message " + reg.getType() + " does not have constructor by (Map<MessageCreatingParams, Object>)!");
        }
        return (M) c.newInstance(creatingParams);
    }


    /**
     * Создание экземпляра сообщения dataObject и связям (parentId, askMessageId)
     *
     * @param reg          Регистрация типа сообщения.
     * @param dataObject   DTO, который надо упаковать в тело сообщения.
     * @param correlation  Correlation-данные.
     * @param parentId     Идентификатор вышестоящего сообщения.
     * @return Экземпляр сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @NotNull
    protected Message<? extends MessageBody>
    internalCreateMessageByDataObject(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @Nullable final String parentId,
            @Nullable final DataObject dataObject,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var body = internalCreateBodyByDataObject(reg, dataObject);
        final var header = internalAutoCreateHeader(reg, parentId);
        return internalCreateMessageByHeaderBody(reg, header, body, correlation);
    }

    /**
     * Создание экземпляра сообщения dataObject и связям (parentId, askMessageId)
     *
     * @param reg          Регистрация типа сообщения.
     * @param dataPackage  Пакет DTOs, который надо упаковать в тело сообщения.
     * @param correlation  Correlation-данные.
     * @param parentId     Идентификатор вышестоящего сообщения.
     * @return Экземпляр сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @NotNull
    protected Message<? extends MessageBody>
    internalCreateMessageByDataPackage(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @Nullable final String parentId,
            @Nullable final DataPackage<? extends DataObject> dataPackage,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var body = internalCreateBodyByDataPackage(reg, dataPackage);
        final var header = internalAutoCreateHeader(reg, parentId);
        return internalCreateMessageByHeaderBody(reg, header, body, correlation);
    }

    /**
     * Создание экземпляра тела сообщения с упаковкой в него dataObject.
     *
     * @param reg        Регистрация типа сообщения.
     * @param dataObject DTO, который надо упаковать в тело сообщения.
     * @return Экземпляр тела сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @NotNull
    protected MessageBody internalCreateBodyByDataObject(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @Nullable final DataObject dataObject
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var constrBody = reg.getConstructorMessageBodyByDataObject();
        if (constrBody == null) {
            throw new MessagingConfigurationException("Body (" + reg.getMessageBodyClass().getName() + ") of message " + reg.getType() + " does not have constructor by DataObject!");
        }
        return constrBody.newInstance(dataObject);
    }

    /**
     * Создание экземпляра тела сообщения с упаковкой в него dataPackage.
     *
     * @param reg         Регистрация типа сообщения.
     * @param dataPackage Пакет DTOs, который надо упаковать в тело сообщения.
     * @return Экземпляр тела сообщения.
     * @throws InvocationTargetException Ошибки при вызове конструктора сообщения.
     * @throws InstantiationException    Ошибки при создании экземпляра сообщения.
     * @throws IllegalAccessException    Ошибки при отсутствии доступа к конструктору сообщения.
     */
    @NotNull
    protected MessageBody internalCreateBodyByDataPackage(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @Nullable final DataPackage<? extends DataObject> dataPackage
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var constrBody = reg.getConstructorMessageBodyByDataPackage();
        if (constrBody == null) {
            throw new MessagingConfigurationException("Body (" + reg.getMessageBodyClass().getName() + ") of message " + reg.getType() + " does not have constructor by DataPackage!");
        }
        return constrBody.newInstance(dataPackage);
    }

    /**
     * Создание заголовка сообщения.
     *
     * @param reg      Регистрация типа сообщения.
     * @param parentId Идентификатор вышестоящего сообщения.
     * @return Экземпляр заголовка сообщения.
     */
    protected MessageHeader internalAutoCreateHeader(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @Nullable final String parentId
    ) {
        return internalCreateHeader(
                reg,
                UUID.randomUUID().toString(),
                parentId,
                this.serviceName,
                OffsetDateTime.now()
        );
    }


    /**
     * Создание заголовка сообщения.
     *
     * @param reg Регистрация типа сообщения.
     * @param creatingParams Параметры для создания заголовка сообщения.
     * @return Экземпляр заголовка сообщения.
     */
    protected MessageHeader internalCreateHeaderByParams(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @NotNull final Map<MessageCreatingParams, Object> creatingParams
    ) {
        var created = (OffsetDateTime) creatingParams.get(MessageCreatingParams.CreatedDateTimeUtc);
        if (created == null) {
            created = OffsetDateTime.now(); // ZoneOffset.UTC
        }

        return internalCreateHeader(
                reg,
                StringUtils.isNull((String) creatingParams.get(MessageCreatingParams.Id), UUID.randomUUID().toString()),
                (String) creatingParams.get(MessageCreatingParams.ParentId),
                StringUtils.isNull((String) creatingParams.get(MessageCreatingParams.SystemSource), this.serviceName),
                created
        );
    }

    /**
     * Создание заголовка сообщения по параметрам.
     *
     * @param reg          Регистрация типа сообщения.
     * @param parentId     Идентификатор вышестоящего сообщения.
     * @return Экземпляр заголовка сообщения.
     */
    protected MessageHeader internalCreateHeader(
            @NotNull final MessageTypesRegistrator.MessageTypeRegistration reg,
            @NotNull final String id,
            @Nullable final String parentId,
            @NotNull final String sourceSystem,
            @NotNull final OffsetDateTime createdDateTime
    ) {
        if (parentId == null && (reg.getKind() == MessageKind.Response || reg.getKind() == MessageKind.QueryResult)) {
            throw new NullPointerException("Parameter parentId should be not null!");
        }
        return new MessageHeader(
                id,
                parentId,
                reg.getKind(),
                reg.getType(),
                reg.getVersion(),
                sourceSystem,
                createdDateTime
        );
    }

    protected boolean checkParamsIsOnly(@NotNull final Map<MessageCreatingParams, Object> creatingParams, @NotNull final Set<MessageCreatingParams> onlyParams) {
        for (var p : creatingParams.keySet()) {
            if (!onlyParams.contains(p)) {
                return false;
            }
        }
        return true;
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
