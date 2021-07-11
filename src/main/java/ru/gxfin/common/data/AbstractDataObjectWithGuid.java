package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class AbstractDataObjectWithGuid extends AbstractDataObject implements DataObjectWithGuid {
    @JsonProperty
    private UUID globalId;

    @Override
    public void cleanOnReturnToPool() {
        this.globalId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
