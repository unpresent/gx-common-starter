package ru.gx.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class IntegerUtils {
    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    @NotNull
    public static Integer isNull(@Nullable final Integer value, @NotNull final Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param mainValue возвращаемое занчение, если не совпадает с ifValue.
     * @param ifValue значение, с которым сверяется mainValue.
     * @return null, если #mainValue.equals(ifValue). Иначе mainValue.
     */
    @Nullable
    public static Integer nullIf(@Nullable final Integer mainValue, @Nullable final Integer ifValue) {
        if (mainValue == null) {
            return null;
        }
        return mainValue.equals(ifValue) ? null : mainValue;
    }

    /**
     * @param fromString Исходная строка, которую парсим.
     * @return Число из строки, если строка не null. Null, если строка null.
     * @throws NumberFormatException Ошибка при парсинге строки.
     */
    @Nullable
    public static Integer parseNullableInt(@Nullable final String fromString) throws NumberFormatException  {
        if (fromString == null) {
            return null;
        }
        return Integer.parseInt(fromString);
    }
}