package ru.gagarkin.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gagarkin.common.updatable.DefaultUpdatableBeanRegistry;
import ru.gagarkin.common.updatable.UpdatableAnnotationBeanPostProcessor;
import ru.gagarkin.common.updatable.UpdatableBeanMemberInfoExtractor;
import ru.gagarkin.common.updatable.UpdatableBeanRegistry;

/**
 * Конфигурация инфраструктурных бинов.
 *
 */
@Configuration
public class CommonAutoConfiguration {

    /**
     * Реестр обновляемых свойств.
     *
     * @return {@link DefaultUpdatableBeanRegistry}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableBeanRegistry updatableBeanRegistry() {
        return new DefaultUpdatableBeanRegistry();
    }

    /**
     * Обработчик бинов, который добавляет в бины функционал обновляемых полей.
     *
     * @return {@link UpdatableAnnotationBeanPostProcessor}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableAnnotationBeanPostProcessor updatableAnnotationBeanPostProcessor() {
        return new UpdatableAnnotationBeanPostProcessor();
    }

    /**
     * Обработчик, извлекающий из класса обновляемые поля и сеттеры.
     *
     * @return {@link UpdatableBeanMemberInfoExtractor}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableBeanMemberInfoExtractor updatableBeanMemberInfoExtractor() {
        return new UpdatableBeanMemberInfoExtractor();
    }
}
