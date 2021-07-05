package ru.gxfin.common.data;

import java.util.UUID;

public interface DataObjectWithGuid {
    /**
     * @return межсистемный идентификатор объекта
     */
    UUID getGlobalId();
}
