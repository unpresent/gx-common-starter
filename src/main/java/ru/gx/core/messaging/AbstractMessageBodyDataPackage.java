package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.ParameterizedType;

@ToString
public abstract class AbstractMessageBodyDataPackage<P extends DataPackage<? extends DataObject>>
        implements MessageSimpleBody {
    /**
     * Объект данных, содержащийся в теле сообщения
     */
    @Getter
    @NotNull
    private final P data;

    /**
     * Класс данных в dataPackage
     */
    @JsonIgnore
    @Getter
    private final Class<P> dataClass;

    @SuppressWarnings("unchecked")
    protected AbstractMessageBodyDataPackage(@NotNull final P dataPackage) throws UnsupportedDataTypeException {
        this.data = dataPackage;
        final var thisClass = this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.dataClass = (Class<P>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new UnsupportedDataTypeException(thisClass.getName() + " does not support dataClass!");
        }
    }
}
