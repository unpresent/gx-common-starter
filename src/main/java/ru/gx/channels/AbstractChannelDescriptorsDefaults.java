package ru.gx.channels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public abstract class AbstractChannelDescriptorsDefaults {
    @NotNull
    private ChannelMessageMode messageMode = ChannelMessageMode.Object;

    @NotNull
    private SerializeMode serializeMode = SerializeMode.JsonString;
}
