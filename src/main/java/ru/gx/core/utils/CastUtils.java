package ru.gx.core.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.UnsupportedDataTypeException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
@UtilityClass
public class CastUtils {
    // Map<ResultType, Map<BaseForValueType, Function>>
    private static final Map<Class<?>, Map<Class<?>, Function<?, ?>>> castFunctions = new HashMap<>();

    static {
        // registerCastFunction(valueType, resultType, castFunction)

        registerCastFunction(String.class, Long.class, (Long::parseLong));
        registerCastFunction(String.class, Integer.class, (Integer::parseInt));
        registerCastFunction(String.class, Short.class, (Short::parseShort));
        registerCastFunction(String.class, Byte.class, (Byte::parseByte));
        registerCastFunction(String.class, Double.class, (Double::parseDouble));
        registerCastFunction(String.class, BigDecimal.class, BigDecimal::new);
        registerCastFunction(String.class, LocalDate.class, LocalDate::parse);
        registerCastFunction(String.class, LocalTime.class, LocalTime::parse);
        registerCastFunction(String.class, LocalDateTime.class, LocalDateTime::parse);
        registerCastFunction(String.class, OffsetDateTime.class, OffsetDateTime::parse);
        registerCastFunction(String.class, UUID.class, UUID::fromString);

        registerCastFunction(Long.class, String.class, (Object::toString));
        registerCastFunction(Long.class, Integer.class, (Long::intValue));
        registerCastFunction(Long.class, Short.class, (Long::shortValue));
        registerCastFunction(Long.class, Byte.class, (Long::byteValue));
        registerCastFunction(Long.class, BigDecimal.class, BigDecimal::new);

        registerCastFunction(Integer.class, String.class, (Object::toString));
        registerCastFunction(Integer.class, Long.class, aInteger -> (long)aInteger);
        registerCastFunction(Integer.class, Short.class, (Integer::shortValue));
        registerCastFunction(Integer.class, Byte.class, (Integer::byteValue));
        registerCastFunction(Integer.class, BigDecimal.class, BigDecimal::new);

        registerCastFunction(Short.class, String.class, (Object::toString));
        registerCastFunction(Short.class, Long.class, aShort -> (long)aShort);
        registerCastFunction(Short.class, Integer.class, aShort -> (int)aShort);
        registerCastFunction(Short.class, Byte.class, (Short::byteValue));
        registerCastFunction(Short.class, BigDecimal.class, BigDecimal::new);

        registerCastFunction(Byte.class, String.class, (Object::toString));
        registerCastFunction(Byte.class, Long.class, aByte -> (long)aByte);
        registerCastFunction(Byte.class, Integer.class, aByte -> (int)aByte);
        registerCastFunction(Byte.class, Short.class, aByte -> (short)aByte);
        registerCastFunction(Byte.class, BigDecimal.class, BigDecimal::new);

        registerCastFunction(BigDecimal.class, String.class, (Object::toString));
        registerCastFunction(BigDecimal.class, Long.class, BigDecimal::longValue);
        registerCastFunction(BigDecimal.class, Integer.class, BigDecimal::intValue);
        registerCastFunction(BigDecimal.class, Short.class, BigDecimal::shortValue);
        registerCastFunction(BigDecimal.class, Byte.class, BigDecimal::byteValue);
    }

    /**
     * Регистрируем функцию приведения
     *
     * @param valueType  тип аргумента
     * @param resultType тип возвращаемого значения
     * @param function   функция приведения
     * @param <R>        тип возвращаемого значения
     * @param <V>        тип аргумента
     */
    private <R, V> void registerCastFunction(
            @NotNull final Class<V> valueType,
            @NotNull final Class<R> resultType,
            @NotNull final Function<V, R> function
    ) {
        final var casts = castFunctions
                .computeIfAbsent(resultType, k -> new HashMap<>());
        casts.put(valueType, function);
    }

    /**
     * Извлекаем функцию приведения
     *
     * @param valueType  тип аргумента
     * @param resultType тип возвращаемого значения
     * @return функция приведения
     */
    @NotNull
    private Function<?, ?> getCastFunction(
            @NotNull final Class<?> valueType,
            @NotNull final Class<?> resultType
    ) throws UnsupportedDataTypeException {
        final var casts = castFunctions.get(resultType);
        if (casts == null) {
            // resultType должен быть определен в карте преобразователей точно
            throw new UnsupportedDataTypeException("Unsupported value type " + valueType.getName() + " for cast");
        }
        var result = casts.get(resultType);
        if (result != null) {
            // valueType строго найден в карте преобразователей
            return result;
        }

        for (final var item : casts.entrySet()) {
            // Пробегаемся по всем возможным valueType для найденного resultType
            if (item.getKey().isAssignableFrom(valueType)) {
                // Нам подойдет преобразователь, где тип параметра базовый для нашего valueType
                return item.getValue();
            }
        }

        // Не нашли подходящий преобразователь
        throw new UnsupportedDataTypeException("Unsupported result type " + valueType.getName() + " for cast");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T cast(
            @Nullable final Object value,
            @NotNull final Class<T> resultTye
    ) throws UnsupportedDataTypeException {
        if (value == null) {
            return null;
        }
        final var func = (Function<Object, T>)getCastFunction(value.getClass(), resultTye);
        return func.apply(value);
    }
}
