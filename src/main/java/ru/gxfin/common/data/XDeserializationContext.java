package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;

@Deprecated
public class XDeserializationContext extends DefaultDeserializationContext {
    private static final long serialVersionUID = 3L;

    public XDeserializationContext(DeserializerFactory df) {
        super(df, null);
    }

    private XDeserializationContext(XDeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues values) {
        super(src, config, p, values);
    }

    private XDeserializationContext(XDeserializationContext src) {
        super(src);
    }

    private XDeserializationContext(XDeserializationContext src, DeserializerFactory factory) {
        super(src, factory);
    }

    private XDeserializationContext(XDeserializationContext src, DeserializationConfig config) {
        super(src, config);
    }

    public DefaultDeserializationContext copy() {
        ClassUtil.verifyMustOverride(DefaultDeserializationContext.Impl.class, this, "copy");
        return new XDeserializationContext(this);
    }

    public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser p, InjectableValues values) {
        return new XDeserializationContext(this, config, p, values);
    }

    public DefaultDeserializationContext createDummyInstance(DeserializationConfig config) {
        return new XDeserializationContext(this, config);
    }

    public DefaultDeserializationContext with(DeserializerFactory factory) {
        return new XDeserializationContext(this, factory);
    }
}
