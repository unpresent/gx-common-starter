package ru.gx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.worker.SimpleWorker;

@Configuration
public class CommonAutoConfiguration {
    @Value("${service.name}")
    private String serviceName;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    public SimpleWorker simpleWorker() {
        return new SimpleWorker(this.serviceName);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.simple-worker.enabled", havingValue = "true")
    public SimpleSettingsController simpleSettingsController() {
        return new SimpleSettingsController(this.serviceName);
    }
}
