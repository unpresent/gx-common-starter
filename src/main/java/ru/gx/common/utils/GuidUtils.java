package ru.gx.common.utils;

import java.util.UUID;

@SuppressWarnings("unused")
public class GuidUtils {
    private static final UUID emptyGuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static UUID Empty() {
        return emptyGuid;
    }
}
