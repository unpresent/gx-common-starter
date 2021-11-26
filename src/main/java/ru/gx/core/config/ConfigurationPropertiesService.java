package ru.gx.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "service")
@Getter
@Setter
public class ConfigurationPropertiesService {
    private String name;

    @NestedConfigurationProperty
    private StandardSettingsController standardSettingsController = new StandardSettingsController();

    @NestedConfigurationProperty
    private SimpleWorker simpleWorker = new SimpleWorker();

    @NestedConfigurationProperty
    private Events events = new Events();

    @Getter
    @Setter
    public static class StandardSettingsController {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class SimpleWorker {
        private boolean enabled = false;
        private String name = "simple-worker";
        private int waitOnStopMs = 3000;
        private int waitOnRestartMs = 30000;
        private int minTimePerIterationMs = 1000;
        private int timeoutRunnerLifeMs = 20000;
        private int printStatisticsEveryMs = 1000;
    }

    @Getter
    @Setter
    public static class Events {
        @NestedConfigurationProperty
        private StandardExecutor standardExecutor = new StandardExecutor();

        @NestedConfigurationProperty
        private StandardQueue standardQueue = new StandardQueue();
    }

    @Getter
    @Setter
    public static class StandardExecutor {
        private boolean enabled = true;
        private String name = "std-events-executor";
        private int waitOnStopMs = 3000;
        private int waitOnRestartMs = 30000;
        private int minTimePerIterationMs = 1000;
        private int timeoutRunnerLifeMs = 20000;
        private int printStatisticsEveryMs = 1000;
    }

    @Getter
    @Setter
    public static class StandardQueue {
        private boolean enabled = true;
        private String name = "std-events-queue";
        private int printStatisticsEveryMs = 1000;
        private int maxQueueSize = 1000;
        private int prioritiesCount = 8;
    }
}
