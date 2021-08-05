package data;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.gxfin.common.data.AbstractDataObject;
import ru.gxfin.common.data.ObjectCreateException;
import ru.gxfin.common.utils.StringUtils;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestObjectsRepository.IdResolver.class)
public class TestDataObject extends AbstractDataObject {
    private int id;

    private String code;

    private String name;

    @JsonIdentityReference(alwaysAsId = true)
    private TestDictionaryObject dictionaryObject;

    protected TestDataObject() {
        super();
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public static TestDataObject createObject(
            @JsonProperty(value = "id") int id,
            @JsonProperty(value = "code") String code
    ) throws ObjectCreateException {
        if (StringUtils.isNull(code, "X").charAt(0) != 'X') {
            return TestObjectsRepository.ObjectFactory.getOrCreateObject(id);
        } else {
            return null;
        }
    }
}
