package ru.gx.core.channels;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractIncomeChannelHandlerDescriptor
        extends AbstractChannelHandlerDescriptor
        implements IncomeChannelHandlerDescriptor {
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
            @NotNull final ChannelApiDescriptor<? extends Message<? extends MessageBody>> api,
            @Nullable final IncomeChannelDescriptorsDefaults defaults
    ) {
        super(owner, api, ChannelDirection.In, defaults);
        internalInitDefaults(defaults);
    }

    protected AbstractIncomeChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final String channelName,
            @Nullable final IncomeChannelDescriptorsDefaults defaults
    ) {
        super(owner, channelName, ChannelDirection.In, defaults);
        internalInitDefaults(defaults);
    }

    protected void internalInitDefaults(@Nullable final IncomeChannelDescriptorsDefaults defaults) {
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
    public AbstractIncomeChannelHandlerDescriptor setLoadingFiltrator(final LoadingFiltrator loadingFiltrator) {
        checkMutable("loadingFiltrator");
        this.loadingFiltrator = loadingFiltrator;
        return this;
    }

    /**
     * Установка способа обработки события о получении данных
     */
    @Override
    @NotNull
    public AbstractIncomeChannelHandlerDescriptor setProcessType(@Nullable final IncomeDataProcessType processType) {
        checkMutable("processType");
        this.processType = processType;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
