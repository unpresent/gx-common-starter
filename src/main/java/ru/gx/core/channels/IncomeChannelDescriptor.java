package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.events.DataEvent;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@SuppressWarnings("unused")
public interface IncomeChannelDescriptor<O extends DataObject, P extends DataPackage<O>> extends ChannelDescriptor {

    /**
     * @return Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     */
    @Nullable
    LoadingFiltrator getLoadingFiltrator();

    /**
     * Установка фильтровальщика.
     *
     * @param loadingFiltrator Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     * @return this.
     */
    @NotNull
    IncomeChannelDescriptor<O, P> setLoadingFiltrator(@Nullable final LoadingFiltrator loadingFiltrator);

    /**
     * @return Класс объектов основных данных, которые будут передаваться в канале.
     */
    @Nullable
    Class<O> getDataObjectClass();

    /**
     * Установка класса объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataObjectClass Класс объектов основных данных.
     * @return this.
     */
    @NotNull
    IncomeChannelDescriptor<O, P> setDataObjectClass(@Nullable final Class<O> dataObjectClass);

    /**
     * @return Класс пакета объектов основных данных, которые будут передаваться в канале.
     */
    @Nullable
    Class<P> getDataPackageClass();

    /**
     * Установка класс пакета объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataPackageClass Класс пакета объектов основных данных.
     * @return this.
     */
    @NotNull
    IncomeChannelDescriptor<O, P> setDataPackageClass(@Nullable final Class<P> dataPackageClass);

    /**
     * Получение класса объектов-событий, которые требуется бросить, чтобы обработать полученные данные.
     */
    @Nullable
    Class<DataEvent> getDataLoadedEventClass();

    /**
     * Получение класса объектов-событий, которые требуется бросить, чтобы обработать полученные данные.
     */
    @NotNull
    IncomeChannelDescriptor<O, P> setDataLoadedEventClass(@Nullable final Class<DataEvent> dataEventClass);

    /**
     * Создание нового экземпляра объекта события для данного канала.
     * @param source Источник порождения события.
     * @return Объект-событие.
     */
    @NotNull
    DataEvent createEvent(@NotNull final Object source);

    /**
     * Способ обработки события о получении данных
     */
    @Nullable
    IncomeDataProcessType getProcessType();

    /**
     * Установка способа обработки события о получении данных
     */
    @NotNull
    IncomeChannelDescriptor<O, P> setProcessType(@Nullable final IncomeDataProcessType processType);
}
