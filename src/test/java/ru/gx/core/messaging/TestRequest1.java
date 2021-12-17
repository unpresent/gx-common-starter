package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelHandleDescriptor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
public class TestRequest1 extends AbstractRequest<TestRequest1.TestRequest1Body> {
    public static final String MESSAGE_TYPE = "TEST:TEST";
    public static final int VERSION = 1;

    static {
        // Здесь регистрируем тип. В конструкторе канала, который будет связан с данным типом сообщений
        MessageTypesRegistrator.registerType(MessageKind.Request, MESSAGE_TYPE, TestRequest1.class);
    }

    @JsonCreator
    public TestRequest1(
            @JsonProperty("header") @NotNull final RequestHeader header,
            @JsonProperty("body") @NotNull final TestRequest1Body body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    public TestRequest1(
            @NotNull String id,
            @NotNull String sourceSystem,
            @NotNull LocalDateTime createdDateTime,
            @NotNull TestDto testDto,
            @Nullable MessageCorrelation correlation
    ) {
        super(
                new RequestHeader(id, MESSAGE_TYPE, sourceSystem, createdDateTime, VERSION),
                new TestRequest1Body(testDto),
                correlation
        );
    }

    @ToString
    public static class TestRequest1Body extends AbstractMessageBody {
        @Getter
        private final TestDto testDto;

        @JsonCreator
        public TestRequest1Body(
                @JsonProperty("testDto") @NotNull final TestDto testDto
        ) {
            this.testDto = testDto;
        }
    }
}

