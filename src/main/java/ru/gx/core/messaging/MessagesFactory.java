package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings("unused")
public interface MessagesFactory {

    /**
     * @param messageType Тип сообщения.
     * @param version     Версия сообщения.
     * @return Возможно ли создание сообщения по параметрам.
     */
    boolean isSupportedCreateByParams(@NotNull final String messageType, final int version);

    /**
     * Создание экземпляра сообщения.
     *
     * @param creatingParams Параметры для создания сообщения.
     * @param messageType    Тип сообщения.
     * @param version        Версия сообщения.
     * @return Экземпляр сообщения.
     */
    @NotNull <M extends Message<? extends MessageBody>>
    M createByParams(@NotNull final Map<MessageCreatingParams, Object> creatingParams, @NotNull final String messageType, final int version) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * @param messageType Тип сообщения.
     * @param version     Версия сообщения.
     * @return Возможно ли создание сообщения только с указанием одного DTO, все остальное автоматом.
     */
    <M extends Message<? extends MessageBody>>
    boolean isSupportedCreateByDataObject(@NotNull final String messageType, final int version);

    /**
     * Создание экземпляра сообщения только по одному параметру. Все остальные автоматом.
     *
     * @param parentId    Идентификатор родительского сообщения.
     * @param messageType Тип сообщения.
     * @param version     Версия сообщения.
     * @param dataObject  DTO, который должен быть упакован в данное сообщение.
     * @param correlation Неиспользуемые данные, которые надо проигнорировать.
     * @return Экземпляр сообщения.
     */
    @NotNull <M extends Message<? extends MessageBody>>
    M createByDataObject(
            @Nullable final String parentId,
            @NotNull final String messageType,
            final int version,
            @Nullable final DataObject dataObject,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * @return Возможно ли создание сообщения только с указанием одного пакета объектов, все остальное автоматом.
     */
    <M extends Message<? extends MessageBody>>
    boolean isSupportedCreateByDataPackage(@NotNull final String messageType, final int version);

    /**
     * Создание экземпляра сообщения только по одному параметру. Все остальные автоматом.
     *
     * @param parentId Идентификатор родительского сообщения.
     * @param messageType Тип сообщения.
     * @param version Версия сообщения.
     * @param dataPackage Пакет DTO, который должен быть упакован в данное сообщение.
     * @param correlation Неиспользуемые данные, которые надо проигнорировать.
     * @return Экземпляр сообщения.
     */
    @NotNull <M extends Message<? extends MessageBody>, O extends DataObject>
    M createByDataPackage(
            @Nullable final String parentId,
            @NotNull final String messageType,
            final int version,
            @Nullable final DataPackage<O> dataPackage,
            @Nullable final MessageCorrelation correlation
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
