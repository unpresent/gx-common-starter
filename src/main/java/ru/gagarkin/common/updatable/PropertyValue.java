package ru.gagarkin.common.updatable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Значение свойства.
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyValue {

    /**
     * Название свойства.
     */
    private String name;

    /**
     * Значение свойства.
     */
    private String value;
}
