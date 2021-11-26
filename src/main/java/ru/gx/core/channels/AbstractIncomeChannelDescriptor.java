package ru.gx.core.channels;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.events.DataEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractIncomeChannelDescriptor<O extends DataObject, P extends DataPackage<O>>
        extends AbstractChannelDescriptor
        implements IncomeChannelDescriptor<O, P> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    /**
     * Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     */
    @Getter
    @Nullable
    private LoadingFiltrator loadingFiltrator;

    /**
     * Класс объектов основных данных, которые будут передаваться в канале.
     */
    @Getter
    @Nullable
    private Class<O> dataObjectClass;

    /**
     * Класс пакета объектов основных данных, которые будут передаваться в канале.
     */
    @Getter
    @Nullable
    private Class<P> dataPackageClass;

    /**
     * Класс объектов-событий, которые требуется бросить, чтобы обработать полученные данные.
     */
    @Getter
    @Nullable
    private Class<DataEvent> dataLoadedEventClass;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Constructor<DataEvent> dataEventConstructor;

    /**
     * Способ обработки события о получении данных
     */
    @Getter
    @Nullable
    private IncomeDataProcessType processType;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractIncomeChannelDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final String name,
            @Nullable final AbstractChannelDescriptorsDefaults defaults
    ) {
        super(owner, name, ChannelDirection.In, defaults);
    }

    @SneakyThrows(NoSuchMethodException.class)
    @Override
    public @NotNull AbstractChannelDescriptor init() throws InvalidParameterException {
        if (this.dataLoadedEventClass == null) {
            throw new ChannelConfigurationException("DataLoadedEventClass does not defined!");
        }
        this.dataEventConstructor = this.dataLoadedEventClass.getConstructor(Object.class);
        super.init();
        return this;
    }

    @Override
    public @NotNull AbstractChannelDescriptor unInit() {
        this.dataEventConstructor = null;
        super.unInit();
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">

    /**
     * Установка фильтровальщика.
     *
     * @param loadingFiltrator Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractIncomeChannelDescriptor<O, P> setLoadingFiltrator(final LoadingFiltrator loadingFiltrator) {
        checkChangeable("loadingFiltrator");
        this.loadingFiltrator = loadingFiltrator;
        return this;
    }

    /**
     * Установка класса объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataObjectClass Класс объектов основных данных.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractIncomeChannelDescriptor<O, P> setDataObjectClass(@Nullable final Class<O> dataObjectClass) {
        checkChangeable("dataObjectClass");
        this.dataObjectClass = dataObjectClass;
        return this;
    }

    /**
     * Установка класс пакета объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataPackageClass Класс пакета объектов основных данных.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractIncomeChannelDescriptor<O, P> setDataPackageClass(@Nullable final Class<P> dataPackageClass) {
        checkChangeable("dataPackageClass");
        this.dataPackageClass = dataPackageClass;
        return this;
    }

    /**
     * Получение класса объектов-событий, которые требуется бросить, чтобы обработать полученные данные.
     */
    @Override
    @NotNull
    public IncomeChannelDescriptor<O, P> setDataLoadedEventClass(@Nullable final Class<DataEvent> dataLoadedEventClass) {
        checkChangeable("dataLoadedEventClass");
        this.dataLoadedEventClass = dataLoadedEventClass;
        return this;
    }

    /**
     * Создание нового экземпляра объекта события для данного канала.
     * @param source Источник порождения события.
     * @return Объект-событие.
     */
    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    @Override
    public @NotNull DataEvent createEvent(@NotNull Object source) {
        if (this.dataEventConstructor == null) {
            throw new ChannelConfigurationException("DataEventConstructor does not defined!");
        }
        return this.dataEventConstructor.newInstance(source);
    }

    /**
     * Установка способа обработки события о получении данных
     */
    @Override
    @NotNull
    public IncomeChannelDescriptor<O, P> setProcessType(@Nullable final IncomeDataProcessType processType) {
        checkChangeable("processType");
        this.processType = processType;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
