package ru.gx.channels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class IncomeChannelDescriptorsDefaults extends AbstractChannelDescriptorsDefaults {
    /**
     * Фильтровальщик, который определяет, требуется ли обрабатывать данные.
     */
    @Nullable
    private LoadingFiltrator loadingFiltrator;
}
