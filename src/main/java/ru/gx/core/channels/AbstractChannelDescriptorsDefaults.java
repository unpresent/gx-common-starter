package ru.gx.core.channels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public abstract class AbstractChannelDescriptorsDefaults {
    @Getter
    @Setter
    private OnErrorBehavior onErrorBehavior = OnErrorBehavior.StopProcessOnError;
}