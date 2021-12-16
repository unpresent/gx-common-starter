package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class TestDes2 {
    private String headerId;

    @Getter
    @Setter
    private Header header;

    @JsonCreator
    public TestDes2(
            @JsonProperty("header.id") String headerId
    ) {
        super();
        this.header = new Header();
        this.headerId = headerId;
    }

    public class Header {
        private String getId() {
            return TestDes2.this.headerId;
        }

        public Header() {}
    }
}
