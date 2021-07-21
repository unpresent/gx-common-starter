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
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestObjectRepository.IdResolver.class)
public class TestDataObject extends AbstractDataObjectWithKey {
    private int id;

    private String code;

    private String name;

    @JsonIdentityReference(alwaysAsId = true)
    private TestDictionaryObject dictionaryObject;

    protected TestDataObject() {
        super();
    }

    @Override
    @JsonIgnore
    public Object getKey() {
        return this.id;
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public static TestDataObject createObject(
            @JsonProperty(value = "id") int id
    ) throws ObjectsPoolException {
        return TestObjectRepository.ObjectFactory.getOrCreateObject(id);
    }
}
