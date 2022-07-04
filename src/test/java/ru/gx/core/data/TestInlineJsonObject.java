package ru.gx.core.data;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestInlineJsonObject extends AbstractDataObject {

    @JsonRawValue
    private final String data;

    @JsonCreator
    public TestInlineJsonObject(
            @JsonProperty("data") final String data
    ) {
        this.data = data;
    }
}
