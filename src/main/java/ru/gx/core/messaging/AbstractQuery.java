package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQuery<B extends AbstractMessageBody>
        extends AbstractMessage<QueryHeader, B>
        implements Query<QueryHeader, B> {

    @JsonCreator
    public AbstractQuery(
            @JsonProperty("header") @NotNull final QueryHeader header,
            @JsonProperty("body") @Nullable final B body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    public AbstractQuery(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @Nullable final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        super(new QueryHeader(id, type, sourceSystem, createdDateTime, version), body, correlation);
    }
}