package ru.gx.config;

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
        private int wait_on_stop_ms = 3000;
        private int wait_on_restart_ms = 30000;
        private int min_time_per_iteration_ms = 1000;
        private int timeout_runner_life_ms = 20000;
    }

    @Getter
    @Setter
    public static class WorkerEx extends SimpleWorker {
        private int name;
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
        private int wait_on_stop_ms = 3000;
        private int wait_on_restart_ms = 30000;
        private int min_time_per_iteration_ms = 1000;
        private int timeout_runner_life_ms = 20000;
    }

    @Getter
    @Setter
    public static class StandardQueue {
        private boolean enabled = true;
        private int printStatisticsEveryMs = 1000;
        private int maxQueueSize = 1000;
        private int prioritiesCount = 8;
    }
}
