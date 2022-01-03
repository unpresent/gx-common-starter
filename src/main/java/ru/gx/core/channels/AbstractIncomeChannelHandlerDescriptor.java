package ru.gx.core.channels;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractIncomeChannelHandlerDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>>
        extends AbstractChannelHandlerDescriptor<M>
        implements IncomeChannelHandlerDescriptor<M> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    /**
     * Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     */
    @Getter
    @Nullable
    private LoadingFiltrator loadingFiltrator;

    /**
     * Способ обработки события о получении данных
     */
    @Getter
    @Nullable
    private IncomeDataProcessType processType;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractIncomeChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull ChannelApiDescriptor<M> api,
            @Nullable final IncomeChannelDescriptorsDefaults defaults
    ) {
        super(owner, api, ChannelDirection.In, defaults);
        if (defaults != null) {
            this
                    .setProcessType(defaults.getProcessType())
                    .setLoadingFiltrator(defaults.getLoadingFiltrator());
        }
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
    public AbstractIncomeChannelHandlerDescriptor<M> setLoadingFiltrator(final LoadingFiltrator loadingFiltrator) {
        checkMutable("loadingFiltrator");
        this.loadingFiltrator = loadingFiltrator;
        return this;
    }

    /**
     * Установка способа обработки события о получении данных
     */
    @Override
    @NotNull
    public AbstractIncomeChannelHandlerDescriptor<M> setProcessType(@Nullable final IncomeDataProcessType processType) {
        checkMutable("processType");
        this.processType = processType;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
