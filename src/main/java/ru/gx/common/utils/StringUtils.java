package ru.gx.common.utils;

@SuppressWarnings("unused")
public class StringUtils {
    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    public static String isNull(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param mainValue возвращаемое занчение, если не совпадает с ifValue.
     * @param ifValue значение, с которым сверяется mainValue.
     * @return null, если #mainValue.equals(ifValue). Иначе mainValue.
     */
    public static String nullIf(String mainValue, String ifValue) {
        if (mainValue == null) {
            return null;
        }
        return mainValue.equals(ifValue) ? null : mainValue;
    }
}
