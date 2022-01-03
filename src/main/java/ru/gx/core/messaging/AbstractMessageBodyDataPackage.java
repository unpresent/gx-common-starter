package ru.gx.core.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

public abstract class AbstractMessageBodyDataPackage<P extends DataPackage<? extends DataObject>> implements MessageBody {
    @Getter
    @NotNull
    private final P dataPackage;

    protected AbstractMessageBodyDataPackage(@NotNull final P dataPackage) {
        this.dataPackage = dataPackage;
    }
}
