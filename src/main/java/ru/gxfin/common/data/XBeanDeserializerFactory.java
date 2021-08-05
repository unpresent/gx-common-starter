package ru.gxfin.common.data;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

@SuppressWarnings({"SpellCheckingInspection", "UnnecessaryLocalVariable"})
@Deprecated
public class XBeanDeserializerFactory extends BeanDeserializerFactory {
    public static final BeanDeserializerFactory instance = new XBeanDeserializerFactory(new DeserializerFactoryConfig());

    public XBeanDeserializerFactory(DeserializerFactoryConfig config) {
        super(config);
    }

    @Override
    public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription valueBeanDesc, Class<?> builderClass) throws JsonMappingException {
        final var result = super.createBuilderBasedDeserializer(ctxt, valueType, valueBeanDesc, builderClass);
        return result;
    }

    @Override
    public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        final var result = super.createBeanDeserializer(ctxt, type, beanDesc);
        return result;
    }

    @Override
    public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        final var result = super.buildBeanDeserializer(ctxt, type, beanDesc);
        return result;
    }

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        final var instantiator = super.findValueInstantiator(ctxt, beanDesc);
        final var result = new XValueInstantiator(instantiator);
        return result;
    }
}
