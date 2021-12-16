package ru.gx.core.messaging;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString
public class TestCorrelation {
    @Getter
    private final String code = "KKK";

    @Getter
    private final String value = "VVV";
}
