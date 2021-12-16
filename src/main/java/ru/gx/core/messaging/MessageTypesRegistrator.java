package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public abstract class MessageTypesRegistrator {
    private static final HashMap<String, Class<? extends Message>> types = new HashMap<>();

    public static void registerType(@NotNull final String type, @NotNull final Class<? extends Message> messageClass) {
        if (types.containsKey(type)) {
            throw new MessagingConfigurationException("Type " + type + " already registered!");
        }
        types.put(type, messageClass);
    }

    public static void checkType(@NotNull final String type, @NotNull final Class<? extends Message> messageClass) {
        if (!types.containsKey(type)) {
            throw new MessagingConfigurationException("Type " + type + " does not registered!");
        }
        final var messageClassByType = types.get(type);
        if (messageClassByType != messageClass) {
            throw new MessagingConfigurationException("Type " + type + " is registered with class " + messageClassByType.getName() + ", which is not compatible with class " + messageClass.getName() + "!");
        }
    }
}
