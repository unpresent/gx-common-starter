package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.gx.core.data.DataObject;

import java.math.BigDecimal;

@Accessors(chain = true)
@ToString
public class TestDto implements DataObject {
    @Getter
    private final String code;

    @Getter
    private final String name;

    @Getter
    private final BigDecimal price;

    @Getter
    private final Long number;

    @JsonCreator
    public TestDto(
            @JsonProperty("code") final String code,
            @JsonProperty("name") final String name,
            @JsonProperty("price") final BigDecimal price,
            @JsonProperty("number") final Long number
    ) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.number = number;
    }
}
