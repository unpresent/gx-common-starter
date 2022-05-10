package ru.gx.core.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
@UtilityClass
public class GuidUtils {
    @NotNull
    private static final UUID emptyGuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @NotNull
    public static UUID Empty() {
        return emptyGuid;
    }
}
