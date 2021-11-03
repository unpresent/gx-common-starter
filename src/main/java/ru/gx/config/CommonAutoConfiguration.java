package ru.gx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.settings.SimpleWorkerSettingsContainer;
import ru.gx.worker.SimpleOnIterationExecuteEvent;
import ru.gx.worker.SimpleOnStartingExecuteEvent;
import ru.gx.worker.SimpleOnStoppingExecuteEvent;
import ru.gx.worker.SimpleWorker;

@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesService.class)
public class CommonAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-settings-controller.enabled", havingValue = "true")
    public SimpleSettingsController simpleSettingsController() {
        return new SimpleSettingsController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    public SimpleWorker simpleWorker() {
        return new SimpleWorker();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    @Autowired
    public SimpleOnIterationExecuteEvent simpleIterationExecuteEvent(SimpleWorker source) {
        return new SimpleOnIterationExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    @Autowired
    public SimpleOnStartingExecuteEvent simpleStartingExecuteEvent(SimpleWorker source) {
        return new SimpleOnStartingExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    @Autowired
    public SimpleOnStoppingExecuteEvent simpleStoppingExecuteEvent(SimpleWorker source) {
        return new SimpleOnStoppingExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    public SimpleWorkerSettingsContainer simpleWorkerSettingsContainer() {
        return new SimpleWorkerSettingsContainer();
    }

}
