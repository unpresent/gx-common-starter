package data;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.gxfin.common.data.AbstractDataObjectWithKey;
import ru.gxfin.common.data.ObjectsPoolException;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "code", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestDictionaryRepository.IdResolver.class)
public class TestDictionaryObject extends AbstractDataObjectWithKey {
    private String code;

    private String name;

    @Override
    @JsonIgnore
    public Object getKey() {
        return getCode();
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public static TestDictionaryObject createObject(
            @JsonProperty(value = "code") String code
    ) throws ObjectsPoolException {
        return TestDictionaryRepository.ObjectFactory.getOrCreateObject(code);
    }
}
