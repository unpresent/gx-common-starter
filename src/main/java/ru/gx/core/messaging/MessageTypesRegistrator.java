package ru.gx.core.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings("rawtypes")
public abstract class MessageTypesRegistrator {
    /**
     * Map(type, Map(version, MessageTypeRegistration))
     * Для каждого типа определяется список пар: (версия, регистрация).
     */
    private static final Map<String, Map<Integer, MessageTypeRegistration>> types = new HashMap<>();

    /**
     * Map(MessageClass, List(MessageTypeRegistration))
     * Для каждого класса определяется список регистраций.
     */
    private static final Map<Class<? extends Message>, Set<MessageTypeRegistration>> classes = new HashMap<>();

    public static void registerType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            final int version,
            @NotNull final Class<? extends Message> messageClass,
            @NotNull final Class<? extends MessageBody> messageBody
    ) {
        synchronized (MessageTypesRegistrator.class) {
            final var verRegs = types.computeIfAbsent(type, k -> new HashMap<>());
            final var r = verRegs.get(version);
            if (r != null) {
                throw new MessagingConfigurationException("(Type=" + type + ", version=" + version + ") already registered (for messageClass: " + r.getMessageClass().getName() + ")!");
            }
            final var reg = new MessageTypeRegistration(kind, type, version, messageClass, messageBody);
            verRegs.put(version, reg);

            final var regSet = classes.computeIfAbsent(messageClass, k -> new HashSet<>());
            regSet.add(reg);
        }
    }

    public static void checkType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            final int version,
            @NotNull final Class<? extends Message> messageClass
    ) {
        internalCheckType(kind, type, version, messageClass);
    }

    public static void checkType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            final int version,
            @NotNull final Class<? extends Message> messageClass,
            @NotNull final Class<? extends MessageBody> messageBodyClass
    ) {
        final var reg = internalCheckType(kind, type, version, messageClass);
        if (reg.getMessageBodyClass() != messageBodyClass) {
            throw new MessagingConfigurationException("Type " + type + " is registered with MessageBody " + reg.getMessageBodyClass().getName() + ", which is not compatible with class " + messageBodyClass.getName() + "!");
        }
    }

    private static MessageTypeRegistration internalCheckType(
            @NotNull final MessageKind kind,
            @NotNull final String type,
            final int version,
            @NotNull final Class<? extends Message> messageClass
    ) {
        final var reg = get(type, version);
        if (reg.getMessageClass() != messageClass) {
            throw new MessagingConfigurationException("(Type=" + type + ", version=" + version + ") is registered with MessageClass " + reg.getMessageClass().getName() + ", which is not compatible with messageClass " + messageClass.getName() + "!");
        }
        if (reg.getKind() != kind) {
            throw new MessagingConfigurationException("(Type=" + type + ", version=" + version + ") is registered with Kind " + reg.getKind() + ", which is not compatible with kind " + kind + "!");
        }
        return reg;
    }

    public static boolean contains(@NotNull final String type, final int version) {
        final var verRegs = types.get(type);
        if (verRegs != null) {
            return verRegs.containsKey(version);
        }
        return false;
    }

    @NotNull
    public static MessageTypeRegistration get(@NotNull final String type, final int version) {
        final var verRegs =  types.get(type);
        if (verRegs == null) {
            throw new MessagingConfigurationException("Type " + type + " does not registered!");
        }
        final var result = verRegs.get(version);
        if (result == null) {
            throw new MessagingConfigurationException("Type " + type + " does not registered with version " + version + "!");
        }
        return result;
    }

    public static class MessageTypeRegistration {
        @Getter
        @NotNull
        private final MessageKind kind;

        @Getter
        @NotNull
        private final String type;

        @Getter
        private final int version;

        @Getter
        @NotNull
        private final Class<? extends Message> messageClass;

        @Getter
        @NotNull
        private final Class<? extends MessageBody> messageBodyClass;

        @Getter
        @Nullable
        private final Constructor<? extends MessageBody> constructorMessageBodyByDataObject;

        @Getter
        @Nullable
        private final Constructor<? extends MessageBody> constructorMessageBodyByDataPackage;

        @Getter
        @Nullable
        private final Constructor<? extends Message> constructorMessageByParams;

        @Getter
        @Nullable
        private final Constructor<? extends Message> constructorMessageByHeaderBody;

        public boolean isBodyConstructorByDataObject() {
            return this.constructorMessageBodyByDataObject != null;
        }

        public boolean isBodyConstructorByDataPackage() {
            return this.constructorMessageBodyByDataPackage != null;
        }

        public boolean isConstructorByParams() {
            return this.constructorMessageByParams != null;
        }

        public boolean isConstructorByHeaderBody() {
            return this.constructorMessageByHeaderBody != null;
        }

        @SuppressWarnings("unchecked")
        private MessageTypeRegistration(
                @NotNull final MessageKind kind,
                @NotNull final String type,
                final int version,
                @NotNull final Class<? extends Message> messageClass,
                @NotNull final Class<? extends MessageBody> messageBodyClass
        ) {
            this.kind = kind;
            this.type = type;
            this.version = version;
            this.messageClass = messageClass;
            this.messageBodyClass = messageBodyClass;

            final var bodyConstructors = messageBodyClass.getConstructors();

            this.constructorMessageBodyByDataObject = (Constructor<? extends MessageBody>)
                    Arrays.stream(bodyConstructors)
                            .filter(c -> {
                                final var params = c.getParameterTypes();
                                return
                                        params.length == 1
                                                && DataObject.class.isAssignableFrom(params[0]);
                            })
                            .findFirst()
                            .orElse(null);

            this.constructorMessageBodyByDataPackage = (Constructor<? extends MessageBody>)
                    Arrays.stream(bodyConstructors)
                            .filter(c -> {
                                final var params = c.getParameterTypes();
                                return
                                        params.length == 1
                                                && DataPackage.class.isAssignableFrom(params[0]);
                            })
                            .findFirst()
                            .orElse(null);

            this.constructorMessageByParams = (Constructor<? extends Message>)
                    Arrays.stream(messageClass.getConstructors())
                            .filter(c -> {
                                final var params = c.getParameterTypes();
                                return
                                        params.length == 1
                                                && Map.class.isAssignableFrom(params[0]);
                            })
                            .findFirst()
                            .orElse(null);

            this.constructorMessageByHeaderBody = (Constructor<? extends Message>)
                    Arrays.stream(messageClass.getConstructors())
                            .filter(c -> {
                                final var params = c.getParameterTypes();
                                return
                                        params.length == 3
                                                && MessageHeader.class.isAssignableFrom(params[0])
                                                && MessageBody.class.isAssignableFrom(params[1])
                                                && MessageCorrelation.class.isAssignableFrom(params[2]);
                            })
                            .findFirst()
                            .orElse(null);
        }

        public String toString() {
            return "MessageTypeRegistration(kind=" + this.kind
                    + ", type=" + this.type
                    + ", messageClass=" + this.messageClass
                    + ", messageBodyClass=" + this.messageBodyClass
                    + ", isBodyConstructorByDataObject()=" + this.isBodyConstructorByDataObject()
                    + ", isBodyConstructorByDataPackage()=" + this.isBodyConstructorByDataPackage()
                    + ", isConstructorByParams()=" + this.isConstructorByParams()
                    + ", isConstructorByHeaderBody()=" + this.isConstructorByHeaderBody()
                    + ")";
        }
    }
}
