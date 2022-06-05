package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IncomeChannelHandlerDescriptor
        extends ChannelHandlerDescriptor {

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
    IncomeChannelHandlerDescriptor setLoadingFiltrator(@Nullable final LoadingFiltrator loadingFiltrator);

    /**
     * Способ обработки события о получении данных
     */
    @Nullable
    IncomeDataProcessType getProcessType();

    /**
     * Установка способа обработки события о получении данных
     */
    @NotNull
    IncomeChannelHandlerDescriptor setProcessType(@Nullable final IncomeDataProcessType processType);
}
