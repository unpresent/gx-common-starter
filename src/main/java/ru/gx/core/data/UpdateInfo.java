package ru.gx.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateInfo {
    @NotNull
    private final List<String> fields;

    @JsonCreator
    public UpdateInfo(@JsonProperty("fields") @NotNull final List<String> fields) {
        this.fields = fields;
    }
}
