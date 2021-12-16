package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Getter
@ToString
public class TestDeserialize {
    private final Header header;
    private final Body body;
    private final MessageCorrelation correlation;

    @JsonCreator
    public TestDeserialize(
            @JsonProperty("header.id") @NotNull final String id,
            @JsonProperty("header.kind") @NotNull final MessageKind kind,
            @JsonProperty("header.type") @NotNull final String type,
            @JsonProperty("header.systemSource") @Nullable final String sourceSystem,
            @JsonProperty("header.createdDateTime") @NotNull final LocalDateTime createdDateTime
    ) {
        super();
        this.header = new Header(id, kind, type, sourceSystem, createdDateTime);
        this.body = new Body();
        this.correlation = new MessageCorrelation();
    }

    @Getter
    @Setter
    @ToString
    public static class Header {
        private final String id;

        private final String sourceSystem;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private final LocalDateTime createdDateTime;

        private final String type;

        private final MessageKind kind;

        @JsonCreator
        public Header(
                @JsonProperty("id") @NotNull final String id,
                @JsonProperty("kind") @NotNull final MessageKind kind,
                @JsonProperty("type") @NotNull final String type,
                @JsonProperty("systemSource") @Nullable final String sourceSystem,
                @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTime
        ) {
            this.id = id;
            this.kind = kind;
            this.type = type;
            this.sourceSystem = sourceSystem;
            this.createdDateTime = createdDateTime;
        }
    }

    @Getter
    @Setter
    @ToString
    public class Body {
        private TestDto testDto;
        public Body() {
        }
    }
}
