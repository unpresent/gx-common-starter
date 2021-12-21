package ru.gx.core.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;

public abstract class AbstractMessageBodyDataObject<O extends DataObject> extends AbstractMessageBody {
    @Getter
    @NotNull
    private final O dataObject;

    protected AbstractMessageBodyDataObject(@NotNull final O dataObject) {
        this.dataObject = dataObject;
    }
}
