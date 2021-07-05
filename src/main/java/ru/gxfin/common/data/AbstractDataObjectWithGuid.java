package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AbstractDataObjectWithGuid extends AbstractDataObject implements DataObjectWithGuid {
    @JsonProperty
    private UUID globalId;
}
