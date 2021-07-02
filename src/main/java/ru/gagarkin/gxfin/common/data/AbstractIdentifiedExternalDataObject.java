package ru.gagarkin.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class AbstractIdentifiedExternalDataObject<ID> extends AbstractDataObject implements IdentifiedDataObject<ID> {
    @Id
    @JsonProperty(required = false)
    @Column(name = "Id", nullable = false)
    private ID id;
}
