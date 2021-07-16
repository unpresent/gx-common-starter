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
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestMemorySimpleRepository.IdResolver.class)
public class TestDataObject extends AbstractDataObjectWithKey {
    @JsonProperty
    private int id;

    @JsonProperty
    private String code;

    protected TestDataObject() {
        super();
    }

    @Override
    public Object getKey() {
        return this.id;
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public static TestDataObject createObject(
            @JsonProperty(value = "id") int id
    ) throws ObjectsPoolException {
        return (TestDataObject) TestMemorySimpleRepository.ObjectsFactory.getOrCreateObject(id);
    }
}
