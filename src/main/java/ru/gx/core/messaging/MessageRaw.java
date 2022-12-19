package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Accessors(chain = true)
public class MessageRaw extends AbstractMessage<MessageRaw.Body> {

    /**
     * Конструктор сообщения.
     *
     * @param header      Заголовок сообщения.
     * @param body        Тело сообщения.
     * @param correlation Необрабатываемые данные.
     */
    @JsonCreator
    public MessageRaw(
            @JsonProperty("header") @NotNull final MessageHeader header,
            @JsonProperty("body") @NotNull final Body body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    @Override
    protected void checkMessageType() {
        // No check!
    }

    // -----------------------------------------------------------------------------------------------------------------
    @ToString
    public static class Body implements MessageSimpleBody {
        /**
         * Объект данных, содержащийся в теле сообщения
         */
        @JsonDeserialize(using = InlineDeserializer.class)
        @NotNull
        private final Object data;

        @JsonRawValue
        @NotNull
        public Object getData() {
            return this.data;
        }

        /*
        @NotNull
        public Body setData(@NotNull final Object data) {
            this.data = (String)data;
            return this;
        }
        */

        /**
         * Класс данных в dataObject
         */
        @JsonIgnore
        public final Class<?> getDataClass() {
            return String.class;
        }

        @JsonCreator
        public Body(
                @JsonProperty("data") @NotNull Object data
        ) {
            if (data instanceof String) {
                this.data = data;
            } else {
                throw new ClassCastException("supported only String type for parameter data!");
            }
        }
    }

    private static class InlineDeserializer extends StdDeserializer<Object> {
        @SuppressWarnings("unused")
        public InlineDeserializer() {
            this(null);
        }

        public InlineDeserializer(Class<Object> t) {
            super(t);
        }

        @Override
        public Object deserialize(
                JsonParser parser,
                DeserializationContext context
        ) throws IOException {
            final var node = parser.getCodec().readTree(parser);
            return node.toString();
        }
    }
}
