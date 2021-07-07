package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class AbstractDataPackageWithSharding<T extends AbstractDataObject> extends AbstractDataPackage<T> {
    @Getter
    @Setter
    @JsonProperty
    private UUID shardId;
}
