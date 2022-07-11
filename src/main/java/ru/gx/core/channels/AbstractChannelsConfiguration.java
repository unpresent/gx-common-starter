package ru.gx.core.channels;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.*;

public abstract class AbstractChannelsConfiguration implements ChannelsConfiguration {
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    /**
     * Список описателей сгруппированные по приоритетам.
     */
    @NotNull
    private final List<List<ChannelHandlerDescriptor>> priorities = new ArrayList<>();

    /**
     * Список описателей с группировкой по топикам.
     */
    @NotNull
    private final Map<String, ChannelHandlerDescriptor> channels = new HashMap<>();

    @Getter
    @NotNull
    private final ChannelDirection direction;

    @Getter
    @NotNull
    private final AbstractChannelDescriptorsDefaults descriptorsDefaults;

    @Getter
    @NotNull
    private final String configurationName;

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractChannelsConfiguration(@NotNull ChannelDirection direction, @NotNull final String configurationName) {
        this.direction = direction;
        this.configurationName = configurationName;
        this.descriptorsDefaults = createChannelDescriptorsDefaults();
    }

    protected abstract AbstractChannelDescriptorsDefaults createChannelDescriptorsDefaults();
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ChannelsConfiguration">

    /**
     * Проверка регистрации описателя канала в конфигурации.
     *
     * @param channelName Имя канала.
     * @return true - описатель канала зарегистрирован.
     */
    @Override
    public boolean contains(@NotNull final String channelName) {
        return this.channels.containsKey(channelName);
    }

    /**
     * Получение описателя канала по имени канала.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала.
     * @throws ChannelConfigurationException В случае отсутствия описателя канала с заданным именем бросается ошибка.
     */
    @Override
    @NotNull
    public ChannelHandlerDescriptor get(@NotNull final String channelName) throws ChannelConfigurationException {
        final var result = this.channels.get(channelName);
        if (result == null) {
            throw new ChannelConfigurationException("Can't find description for channel " + channelName);
        }
        return result;
    }

    /**
     * Получение описателя канала по имени.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала. Если не найден, то возвращается null.
     */
    @Override
    @Nullable
    public ChannelHandlerDescriptor tryGet(@NotNull final String channelName) {
        return this.channels.get(channelName);
    }

    /**
     * Создание описателя обработчика канала.
     *
     * @param channelApi      Описатель API канала.
     * @param descriptorClass Класс описателя.
     * @return this.
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    @Override
    @NotNull
    public <M extends Message<? extends MessageBody>, D extends ChannelHandlerDescriptor>
    D newDescriptor(@NotNull final ChannelApiDescriptor<M> channelApi, @NotNull final Class<D> descriptorClass) throws ChannelConfigurationException {
        if (contains(channelApi.getName())) {
            throw new ChannelConfigurationException("Topic '" + channelApi.getName() + "' already registered!");
        }
        D result = null;
        if (allowCreateDescriptor(descriptorClass)) {

            final var constructor3 = Arrays.stream(descriptorClass.getConstructors())
                    .filter(c -> {
                        final var paramsTypes = c.getParameterTypes();
                        return paramsTypes.length == 3
                                && ChannelsConfiguration.class.isAssignableFrom(paramsTypes[0])
                                && ChannelApiDescriptor.class.isAssignableFrom(paramsTypes[1])
                                && AbstractChannelDescriptorsDefaults.class.isAssignableFrom(paramsTypes[2]);
                    })
                    .findFirst();
            if (constructor3.isPresent()) {
                result = (D) constructor3.get().newInstance(this, channelApi, this.getDescriptorsDefaults());
            } else {
                final var constructor4 = Arrays.stream(descriptorClass.getConstructors())
                        .filter(c -> {
                            final var paramsTypes = c.getParameterTypes();
                            return paramsTypes.length == 4
                                    && ChannelsConfiguration.class.isAssignableFrom(paramsTypes[0])
                                    && ChannelApiDescriptor.class.isAssignableFrom(paramsTypes[1])
                                    && ChannelDirection.class.isAssignableFrom(paramsTypes[2])
                                    && AbstractChannelDescriptorsDefaults.class.isAssignableFrom(paramsTypes[3]);
                        })
                        .findFirst();
                if (constructor4.isPresent()) {
                    result = (D) constructor4.get().newInstance(this, channelApi, this.getDirection(), this.getDescriptorsDefaults());
                }
            }
        }
        if (result == null) {
            throw new InvalidParameterException("Can't create instance of " + descriptorClass.getName());
        }
        return result;
    }

    /**
     * Создание описателя обработчика канала.
     *
     * @param channelName     Имя канала.
     * @param descriptorClass Класс описателя.
     * @return this.
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    @Override
    @NotNull
    public <D extends ChannelHandlerDescriptor>
    D newDescriptor(@NotNull final String channelName, @NotNull final Class<D> descriptorClass) throws ChannelConfigurationException {
        if (contains(channelName)) {
            throw new ChannelConfigurationException("Channel '" + channelName + "' already registered!");
        }
        D result = null;
        if (allowCreateDescriptor(descriptorClass)) {

            final var constructor3 = Arrays.stream(descriptorClass.getConstructors())
                    .filter(c -> {
                        final var paramsTypes = c.getParameterTypes();
                        return paramsTypes.length == 3
                                && ChannelsConfiguration.class.isAssignableFrom(paramsTypes[0])
                                && String.class.isAssignableFrom(paramsTypes[1])
                                && AbstractChannelDescriptorsDefaults.class.isAssignableFrom(paramsTypes[2]);
                    })
                    .findFirst();
            if (constructor3.isPresent()) {
                result = (D) constructor3.get().newInstance(this, channelName, this.getDescriptorsDefaults());
            } else {
                final var constructor4 = Arrays.stream(descriptorClass.getConstructors())
                        .filter(c -> {
                            final var paramsTypes = c.getParameterTypes();
                            return paramsTypes.length == 4
                                    && ChannelsConfiguration.class.isAssignableFrom(paramsTypes[0])
                                    && String.class.isAssignableFrom(paramsTypes[1])
                                    && ChannelDirection.class.isAssignableFrom(paramsTypes[2])
                                    && AbstractChannelDescriptorsDefaults.class.isAssignableFrom(paramsTypes[3]);
                        })
                        .findFirst();
                if (constructor4.isPresent()) {
                    result = (D) constructor4.get().newInstance(this, channelName, this.getDirection(), this.getDescriptorsDefaults());
                }
            }
        }
        if (result == null) {
            throw new InvalidParameterException("Can't create instance of " + descriptorClass.getName());
        }
        return result;
    }

    /**
     * Проверка на допустимость создания экземпляра описателя канала указанного класса.
     *
     * @param descriptorClass Класс создаваемого описателя.
     * @return True - создание описателя допустимо.
     */
    abstract protected <D extends ChannelHandlerDescriptor>
    boolean allowCreateDescriptor(
            @NotNull final Class<D> descriptorClass
    );

    /**
     * Регистрация описателя одной очереди.
     *
     * @param descriptor Описатель топика, который надо зарегистрировать в списках описателей.
     */
    @Override
    public void internalRegisterDescriptor(@NotNull final ChannelHandlerDescriptor descriptor) {
        final var channelName = descriptor.getChannelName();
        if (contains(channelName)) {
            throw new ChannelConfigurationException("Channel '" + channelName + "' already registered!");
        }
        if (!descriptor.isInitialized()) {
            throw new ChannelConfigurationException("Descriptor of channel '" + channelName + "' doesn't initialized!");
        }

        final var priority = descriptor.getPriority();
        while (priorities.size() <= priority) {
            priorities.add(new ArrayList<>());
        }
        final var itemsList = priorities.get(priority);
        itemsList.add(descriptor);

        channels.put(channelName, descriptor);
    }

    /**
     * Дерегистрация обработчика очереди.
     *
     * @param descriptor Описатель топика очереди.
     */
    @Override
    public void internalUnregisterDescriptor(@NotNull final ChannelHandlerDescriptor descriptor) {
        final var channelName = descriptor.getChannelName();
        if (!this.channels.containsValue(descriptor) || !this.channels.containsKey(channelName)) {
            throw new ChannelConfigurationException("Channel " + channelName + " not registered!");
        }
        if (!descriptor.equals(this.get(channelName))) {
            throw new ChannelConfigurationException("Descriptor by name " + channelName + " not equal descriptor by parameter!");
        }

        this.channels.remove(channelName);
        for (var pList : this.priorities) {
            if (pList.remove(descriptor)) {
                descriptor.unInit();
                break;
            }
        }
    }

    /**
     * @return Количество приоритетов.
     */
    @Override
    public int prioritiesCount() {
        return this.priorities.size();
    }

    /**
     * Получение списка описателей обработчиков очередей по приоритету.
     *
     * @param priority Приоритет.
     * @return Список описателей обработчиков.
     */
    @Override
    @Nullable
    public Iterable<ChannelHandlerDescriptor> getByPriority(int priority) {
        return this.priorities.get(priority);
    }

    /**
     * @return Список всех описателей обработчиков очередей.
     */
    @Override
    @NotNull
    public Iterable<ChannelHandlerDescriptor> getAll() {
        return this.channels.values();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
