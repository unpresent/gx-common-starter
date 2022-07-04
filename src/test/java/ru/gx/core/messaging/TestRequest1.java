package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.UnsupportedDataTypeException;

@ToString(callSuper = true)
public class TestRequest1 extends AbstractMessage<TestRequest1.Body> {
    public static final String MESSAGE_TYPE = "TEST:TEST";
    public static final int VERSION = 1;

    static {
        // Здесь регистрируем тип. В конструкторе канала, который будет связан с данным типом сообщений
        MessageTypesRegistrator.registerType(MessageKind.Request, MESSAGE_TYPE, VERSION, TestRequest1.class, Body.class);
    }

    @JsonCreator
    public TestRequest1(
            @JsonProperty("header") @NotNull final MessageHeader header,
            @JsonProperty("body") @NotNull final TestRequest1.Body body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    @ToString
    public static class Body extends AbstractMessageBodyDataObject<TestDto> {
        @JsonCreator
        public Body(
                @JsonProperty("data") @NotNull final TestDto testDto
        ) throws UnsupportedDataTypeException {
            super(testDto);
        }
    }
}

