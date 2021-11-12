package ru.gx.data;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.tomcat.jni.Local;
import org.jetbrains.annotations.NotNull;
import ru.gx.json.JsonPropertyEx;

import java.time.LocalTime;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "id")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestObjectsRepository.IdResolver.class)
public class TestDataObject extends AbstractDataObject {
    private final int id;

    @NotNull
    private final String code;

    private final String name;

    @JsonIdentityReference(alwaysAsId = true)
    private final TestDictionaryObject dictionaryObject;

    @JsonFormat(pattern = "H:mm:ss")
    private final LocalTime timeVal;

    @JsonCreator
    public TestDataObject(
            @JsonProperty("id") final int id,
            @JsonProperty("code") @NotNull final String code,
            @JsonProperty("name") final String name,
            @JsonProperty("dictionaryObject") final TestDictionaryObject dictionaryObject,
            @JsonProperty("timeVal") final LocalTime timeVal
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dictionaryObject = dictionaryObject;
        this.timeVal = timeVal;
    }
}
