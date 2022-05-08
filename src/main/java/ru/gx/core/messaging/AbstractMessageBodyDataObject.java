package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.ParameterizedType;

@ToString
public abstract class AbstractMessageBodyDataObject<O extends DataObject>
        implements MessageBody {
    @Getter
    @NotNull
    private final O dataObject;

    /**
     * Класс данных в dataObject
     */
    @JsonIgnore
    @Getter
    private final Class<O> dataClass;

    @SuppressWarnings("unchecked")
    protected AbstractMessageBodyDataObject(@NotNull final O dataObject) throws UnsupportedDataTypeException {
        this.dataObject = dataObject;
        final var thisClass = this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.dataClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new UnsupportedDataTypeException(thisClass.getName() + " does not support dataClass!");
        }
    }
}
