package ru.gxfin.common.kafka;

import lombok.extern.slf4j.Slf4j;
import ru.gxfin.common.data.AbstractMemRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractIncomeTopicsConfiguration implements IncomeTopicsConfiguration {
    private final List<List<IncomeTopic2MemRepo>> priorities = new ArrayList<>();
    private final Map<String, IncomeTopic2MemRepo> topics = new HashMap<>();

    protected AbstractIncomeTopicsConfiguration() {
        super();
    }

    @Override
    public IncomeTopic2MemRepo get(String topic) {
        return this.topics.get(topic);
    }

    @Override
    public AbstractIncomeTopicsConfiguration register(IncomeTopic2MemRepo item) {
        if (this.topics.containsKey(item.getTopic())) {
            throw new IncomeTopicsConfigurationException("Topic " + item.getTopic() + " already registered!");
        }

        final var priority = item.getPriority();
        while (priorities.size() <= priority) {
            priorities.add(new ArrayList<>());
        }

        final var itemsList = priorities.get(priority);
        itemsList.add(item);

        topics.put(item.getTopic(), item);

        return this;
    }

    @Override
    public IncomeTopicsConfiguration register(int priority, String topic, AbstractMemRepo memRepo, TopicMessageMode mode) {
        return register(new IncomeTopic2MemRepo(topic, priority, memRepo, mode));
    }

    @Override
    public IncomeTopicsConfiguration register(String topic, int priority, AbstractMemRepo memRepo, TopicMessageMode mode) {
        return register(new IncomeTopic2MemRepo(topic, priority, memRepo, mode));
    }

    @Override
    public AbstractIncomeTopicsConfiguration unregister(String topic) {
        final var item = this.topics.get(topic);
        if (item == null) {
            throw new IncomeTopicsConfigurationException("Topic " + topic + " not registered!");
        }

        this.topics.remove(topic);
        for (var pList : this.priorities) {
            if (pList.remove(item)) {
                break;
            }
        }

        return this;
    }

    @Override
    public int prioritiesCount() {
        return this.priorities.size();
    }

    @Override
    public Iterable<IncomeTopic2MemRepo> getByPriority(int priority) {
        return this.priorities.get(0);
    }
}
