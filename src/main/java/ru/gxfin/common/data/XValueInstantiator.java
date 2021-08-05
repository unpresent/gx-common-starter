package ru.gxfin.common.data;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Deprecated
public class XValueInstantiator extends ValueInstantiator {
    private final ValueInstantiator original;

    public XValueInstantiator(ValueInstantiator original) {
        super();
        this.original = original;
    }

    @Override
    public ValueInstantiator createContextual(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        return this.original.createContextual(ctxt, beanDesc);
    }

    @Override
    public Class<?> getValueClass() {
        return this.original.getValueClass();
    }

    @Override
    public String getValueTypeDesc() {
        return this.original.getValueTypeDesc();
    }

    @Override
    public boolean canInstantiate() {
        return this.original.canInstantiate();
    }

    @Override
    public boolean canCreateFromString() {
        return this.original.canCreateFromString();
    }

    @Override
    public boolean canCreateFromInt() {
        return this.original.canCreateFromInt();
    }

    @Override
    public boolean canCreateFromLong() {
        return this.original.canCreateFromLong();
    }

    @Override
    public boolean canCreateFromBigInteger() {
        return this.original.canCreateFromBigInteger();
    }

    @Override
    public boolean canCreateFromDouble() {
        return this.original.canCreateFromDouble();
    }

    @Override
    public boolean canCreateFromBigDecimal() {
        return this.original.canCreateFromBigDecimal();
    }

    @Override
    public boolean canCreateFromBoolean() {
        return this.original.canCreateFromBoolean();
    }

    @Override
    public boolean canCreateUsingDefault() {
        return this.original.canCreateUsingDefault();
    }

    @Override
    public boolean canCreateUsingDelegate() {
        return this.original.canCreateUsingDelegate();
    }

    @Override
    public boolean canCreateUsingArrayDelegate() {
        return this.original.canCreateUsingArrayDelegate();
    }

    @Override
    public boolean canCreateFromObjectWith() {
        return this.original.canCreateFromObjectWith();
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return this.original.getFromObjectArguments(config);
    }

    @Override
    public JavaType getDelegateType(DeserializationConfig config) {
        return this.original.getDelegateType(config);
    }

    @Override
    public JavaType getArrayDelegateType(DeserializationConfig config) {
        return this.original.getArrayDelegateType(config);
    }

    @Override
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        return this.original.createUsingDefault(ctxt);
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
        return this.original.createFromObjectWith(ctxt, args);
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
        return this.original.createFromObjectWith(ctxt, props, buffer);
    }

    @Override
    public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        return this.original.createUsingDelegate(ctxt, delegate);
    }

    @Override
    public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        return this.original.createUsingArrayDelegate(ctxt, delegate);
    }

    @Override
    public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
        return this.original.createFromString(ctxt, value);
    }

    @Override
    public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
        return this.original.createFromInt(ctxt, value);
    }

    @Override
    public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
        return this.original.createFromLong(ctxt, value);
    }

    @Override
    public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
        return this.original.createFromBigInteger(ctxt, value);
    }

    @Override
    public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
        return this.original.createFromDouble(ctxt, value);
    }

    @Override
    public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
        return this.original.createFromBigDecimal(ctxt, value);
    }

    @Override
    public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
        return this.original.createFromBoolean(ctxt, value);
    }

    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return this.original.getDefaultCreator();
    }

    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return this.original.getDelegateCreator();
    }

    @Override
    public AnnotatedWithParams getArrayDelegateCreator() {
        return this.original.getArrayDelegateCreator();
    }

    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return this.original.getWithArgsCreator();
    }
}
