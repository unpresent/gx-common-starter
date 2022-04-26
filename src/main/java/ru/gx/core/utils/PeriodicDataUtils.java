package ru.gx.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.periodic.PeriodicData;

import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("unused")
public class PeriodicDataUtils {
    @Nullable
    public static Object getPrevObject(@Nullable final PeriodicData<?> data, @NotNull final LocalDate date) {
        if (data == null) {
            return null;
        }

        var maxDate = data.keySet().stream()
                .filter(d -> d.isBefore(date) || d.isEqual(date))
                .max(LocalDate::compareTo)
                .orElse(null);
        return maxDate == null ? null : data.get(maxDate);
    }

    public static <T> void putIfNotEquals(
            @NotNull final PeriodicData<T> data,
            @NotNull final LocalDate date,
            @Nullable final T object
    ) {
        var currentObject = getPrevObject(data, date);

        if (!Objects.equals(currentObject, object)) {
            data.put(date, object);
        }
    }

    @Nullable
    public static Object getNextObject(@Nullable final PeriodicData<?> data, @NotNull final LocalDate date) {
        if (data == null) {
            return null;
        }

        var minDate = data.keySet().stream().filter(d -> d.isAfter(date)).min(LocalDate::compareTo).orElse(null);
        return minDate == null ? null : data.get(minDate);
    }

    @NotNull
    public static <T> PeriodicData<T> createPeriodicDataObject(
            @Nullable final T object,
            @NotNull final LocalDate date
    ) {
        return
                new PeriodicData<>() {{
                    put(LocalDate.now(), object);
                }};
    }
}
