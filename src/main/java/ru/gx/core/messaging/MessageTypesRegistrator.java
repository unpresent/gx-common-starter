package ru.gx.core.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public abstract class MessageTypesRegistrator {
    private static final HashMap<String, MessageTypeRegistration> typesMaps = new HashMap<>();
    private static final HashMap<Class<? extends Message>, MessageTypeRegistration> classesMap = new HashMap<>();

    public static void registerType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            @NotNull final Class<? extends Message> messageClass
    ) {
        synchronized (MessageTypesRegistrator.class) {
            if (typesMaps.containsKey(type)) {
                final var r = typesMaps.get(type);
                throw new MessagingConfigurationException("Type " + type + " already registered (for messageClass: " + r.messageClass().getName() + ")!");
            }
            if (classesMap.containsKey(messageClass)) {
                final var r = classesMap.get(messageClass);
                throw new MessagingConfigurationException("MessageClass " + messageClass.getName() + " already registered (for type: " + r.type() + ")!");
            }
            final var reg = new MessageTypeRegistration(kind, type, messageClass);
            typesMaps.put(type, reg);
            classesMap.put(messageClass, reg);
        }
    }

    public static void checkType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            @NotNull final Class<? extends Message> messageClass
    ) {
        final var reg = typesMaps.get(type);
        if (reg == null) {
            throw new MessagingConfigurationException("Type " + type + " does not registered!");
        }
        if (reg.messageClass() != messageClass) {
            throw new MessagingConfigurationException("Type " + type + " is registered with MessageClass " + reg.messageClass().getName() + ", which is not compatible with class " + messageClass.getName() + "!");
        }
        if (reg.kind() != kind) {
            throw new MessagingConfigurationException("Type " + type + " is registered with Kind " + reg.kind() + ", which is not compatible with type " + kind + "!");
        }
    }

    private record MessageTypeRegistration(
            @Getter MessageKind kind,
            @Getter String type,
            @Getter Class<? extends Message> messageClass
    ) {
    }
}
