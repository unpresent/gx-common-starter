package ru.gagarkin.gxfin.common.data;

public interface IdentifiedDataObject<ID> extends DataObject {
    ID getId();
}
