package ru.gxfin.common.utils;

import java.math.BigDecimal;

/**
 * Вспомогательные утилиты для работы с BigDecimal
 */
@SuppressWarnings("unused")
public abstract class BigDecimalUtils {
    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    public static BigDecimal isNull(BigDecimal value, BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param mainValue возвращаемое занчение, если не совпадает с ifValue.
     * @param ifValue значение, с которым сверяется mainValue.
     * @return null, если #mainValue.equals(ifValue). Иначе mainValue.
     */
    public static BigDecimal nullIf(BigDecimal mainValue, BigDecimal ifValue) {
        if (mainValue == null) {
            return null;
        }
        return mainValue.equals(ifValue) ? null : mainValue;
    }
}
