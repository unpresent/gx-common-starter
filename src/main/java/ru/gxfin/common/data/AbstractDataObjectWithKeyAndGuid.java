package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Базовый тип для DTO (объектов передачи данных), у которых локальный идентификатор системе и межсистменый guid.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class AbstractDataObjectWithKeyAndGuid extends AbstractDataObjectWithKey implements DataObjectWithGuid {
    @JsonProperty
    private UUID guid;

    protected AbstractDataObjectWithKeyAndGuid() {
        super();
    }

    @Override
    public Object getKey() {
        return getGuid();
    }

    @Override
    public void cleanOnReleaseToPool() {
        this.guid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
