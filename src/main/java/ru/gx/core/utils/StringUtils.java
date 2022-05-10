package ru.gx.core.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@UtilityClass
public class StringUtils {

    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    @Nullable
    public static String isNullObject(@Nullable final Object value, @Nullable final String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    @NotNull
    public static String isNullObjectStrong(@Nullable final Object value, @NotNull final String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    @NotNull
    public static String isNull(@Nullable final String value, @NotNull final String defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param mainValue возвращаемое значение, если не совпадает с ifValue.
     * @param ifValue значение, с которым сверяется mainValue.
     * @return null, если #mainValue.equals(ifValue). Иначе mainValue.
     */
    @Nullable
    public static String nullIf(@Nullable final String mainValue, @Nullable final String ifValue) {
        if (mainValue == null) {
            return null;
        }
        return mainValue.equals(ifValue) ? null : mainValue;
    }
}
