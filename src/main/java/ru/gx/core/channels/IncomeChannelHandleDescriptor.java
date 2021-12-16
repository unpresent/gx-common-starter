package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IncomeChannelHandleDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>>
        extends ChannelHandleDescriptor<M> {

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
    IncomeChannelHandleDescriptor<M> setLoadingFiltrator(@Nullable final LoadingFiltrator loadingFiltrator);

    /**
     * Способ обработки события о получении данных
     */
    @Nullable
    IncomeDataProcessType getProcessType();

    /**
     * Установка способа обработки события о получении данных
     */
    @NotNull
    IncomeChannelHandleDescriptor<M> setProcessType(@Nullable final IncomeDataProcessType processType);
}
