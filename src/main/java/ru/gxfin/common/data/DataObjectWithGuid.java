package ru.gxfin.common.data;

import java.util.UUID;

public interface DataObjectWithGuid extends DataObject {
    /**
     * @return межсистемный идентификатор объекта
     */
    @SuppressWarnings("unused")
    UUID getGuid();
}
