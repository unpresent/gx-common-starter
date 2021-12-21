package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

public abstract class AbstractMessageBodyDataPackage<P extends DataPackage<? extends DataObject>> extends AbstractMessageBody {
    @Getter
    @NotNull
    private final P dataPackage;

    protected AbstractMessageBodyDataPackage(@NotNull final P dataPackage) {
        this.dataPackage = dataPackage;
    }
}
