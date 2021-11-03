package ru.gx.data;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "code")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "code", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestDictionaryRepository.IdResolver.class)
public class TestDictionaryObject extends AbstractDataObject {
    @NotNull
    private final String code;

    private final String name;

    @JsonCreator
    public TestDictionaryObject(
            @JsonProperty("code") @NotNull final String code,
            @JsonProperty("name") final String name) {
        this.code = code;
        this.name = name;
    }
}
