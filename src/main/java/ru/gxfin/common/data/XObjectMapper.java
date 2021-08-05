package ru.gxfin.common.data;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;

@SuppressWarnings("unused")
@Deprecated
public class XObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

    public XObjectMapper() {
        super(null, null, new XDeserializationContext(XBeanDeserializerFactory.instance));
    }

    @Override
    protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws JsonMappingException {
        final var result = super._findRootDeserializer(ctxt, valueType);
        if (valueType.getTypeName().equals("data.TestDictionaryObject") && result instanceof BeanDeserializer) {
            final var beanDeserializer = (BeanDeserializer)result;
        }
        return result;
    }
}
