package ru.gx.core.utils;

import ru.gx.core.periodic.PeriodicData;

import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("unused")
public class PeriodicDataUtils {
    public static Object getPrevObject(PeriodicData<?> data, LocalDate date) {
        var maxDate = data.keySet().stream().filter(d -> d.isBefore(date) || d.isEqual(date)).max(LocalDate::compareTo).orElse(null);
        return maxDate == null ? null : data.get(maxDate);
    }

    public static <T> void putIfNotEquals(PeriodicData<T> data, LocalDate date, T object) {
        var currentObject = getPrevObject(data, date);

        if (!Objects.equals(currentObject, object)) {
            data.put(date, object);
        }
    }

    public static Object getNextObject(PeriodicData<?> data, LocalDate date) {
        var minDate = data.keySet().stream().filter(d -> d.isAfter(date)).min(LocalDate::compareTo).orElse(null);
        return minDate == null ? null : data.get(minDate);
    }
}
