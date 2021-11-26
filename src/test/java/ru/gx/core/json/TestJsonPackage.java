package ru.gx.core.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestJsonPackage {
    @JsonProperty("allCount")
    public long allCount;

    @JsonProperty("objects")
    public String objects;
}
