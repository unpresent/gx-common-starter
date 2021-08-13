package data;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.gxfin.common.data.AbstractDataObject;
import ru.gxfin.common.data.ObjectCreateException;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "code", generator = ObjectIdGenerators.PropertyGenerator.class, resolver = TestDictionaryRepository.IdResolver.class)
public class TestDictionaryObject extends AbstractDataObject {
    private String code;

    private String name;

    //    @SuppressWarnings("unused")
    //    @JsonCreator
    //    public static TestDictionaryObject createObject(
    //            @JsonProperty(value = "code") String code
    //    ) throws ObjectCreateException {
    //        return TestDictionaryRepository.ObjectFactory.getOrCreateObject(code);
    //    }
}
