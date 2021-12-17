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
public abstract class AbstractResponse<B extends AbstractMessageBody>
        extends AbstractMessage<ResponseHeader, B>
        implements Response<B> {

    @JsonCreator
    public AbstractResponse(
            @JsonProperty("header") @NotNull final ResponseHeader header,
            @JsonProperty("body") @NotNull final B body,
            @JsonProperty("correlation") @Nullable final MessageCorrelation correlation
    ) {
        super(header, body, correlation);
    }

    protected AbstractResponse(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @NotNull final String queryId,
            @NotNull final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        super(new ResponseHeader(id, type, sourceSystem, createdDateTime, version, queryId), body, correlation);
    }
}