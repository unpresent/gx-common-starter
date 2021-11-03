package ru.gx.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "service")
@Getter
@Setter
public class ConfigurationPropertiesService {
    private String name;

    @NestedConfigurationProperty
    private SimpleSettingsController simpleSettingsController = new SimpleSettingsController();

    @NestedConfigurationProperty
    private SimpleWorker simpleWorker = new SimpleWorker();

    //    @NestedConfigurationProperty
    //    private WorkerEx worker1;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker2;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker3;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker4;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker5;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker6;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker7;
    //
    //    @NestedConfigurationProperty
    //    private WorkerEx worker8;

    @Getter
    @Setter
    public static class SimpleSettingsController {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class SimpleWorker {
        private boolean enabled = true;
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

}
