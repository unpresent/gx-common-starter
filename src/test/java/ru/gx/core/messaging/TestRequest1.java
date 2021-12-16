package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@ToString
public class TestRequest1 extends AbstractRequest<TestRequest1.TestRequest1Body> {
    public TestRequest1(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @Nullable final TestRequest1Body body,
            @Nullable final MessageCorrelation correlation
    ) {
        super(id, type, sourceSystem, createdDateTime, version, body, correlation);
    }

    @JsonCreator
    public TestRequest1(
            @JsonProperty("header") @NotNull final RequestHeader header,
            @JsonProperty("body") @Nullable final TestRequest1Body body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    @Accessors(chain = true)
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
