package ru.gxfin.common.kafka;

import ru.gxfin.common.data.AbstractMemRepo;

public interface IncomeTopicsConfiguration {
    IncomeTopic2MemRepo get(String topic);

    IncomeTopicsConfiguration register(int priority, String topic, AbstractMemRepo memRepo, TopicMessageMode mode);

    IncomeTopicsConfiguration register(String topic, int priority, AbstractMemRepo memRepo, TopicMessageMode mode);

    IncomeTopicsConfiguration register(IncomeTopic2MemRepo item);

    IncomeTopicsConfiguration unregister(String topic);

    int prioritiesCount();

    Iterable<IncomeTopic2MemRepo> getByPriority(int priority);
}
