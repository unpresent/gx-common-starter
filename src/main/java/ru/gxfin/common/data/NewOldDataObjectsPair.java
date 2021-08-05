package ru.gxfin.common.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
public class NewOldDataObjectsPair<T extends DataObject> {
    @Getter
    private final T newObject;

    @Getter
    private final T oldObject;

    public NewOldDataObjectsPair(T newObject, T oldObject) {
        this.newObject = newObject;
        this.oldObject = oldObject;
    }
}
