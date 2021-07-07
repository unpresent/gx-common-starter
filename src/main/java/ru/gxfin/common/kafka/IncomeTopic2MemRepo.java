package ru.gxfin.common.kafka;

import lombok.Getter;
import ru.gxfin.common.data.AbstractMemRepo;

public class IncomeTopic2MemRepo {
    @Getter
    private final String topic;

    @Getter
    private final int priority;

    @Getter
    private final AbstractMemRepo memRepo;

    @Getter
    private final TopicMessageMode messageMode;

    public IncomeTopic2MemRepo(String topic, int priority, AbstractMemRepo memRepo, TopicMessageMode messageMode) {
        this.topic = topic;
        this.priority = priority;
        this.memRepo = memRepo;
        this.messageMode = messageMode;
    }
}
