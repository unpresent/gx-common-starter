package ru.gx.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gx.common.updatable.UpdatableMemberInfoExtractor;
import ru.gx.common.updatable.UpdatableRegistry;
import ru.gx.common.updatable.DefaultUpdatableRegistry;
import ru.gx.common.updatable.UpdatableAnnotationBeanPostProcessor;

/**
 * Конфигурация инфраструктурных бинов.
 *
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class CommonAutoConfiguration {

    /**
     * Реестр обновляемых свойств.
     *
     * @return {@link DefaultUpdatableRegistry}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableRegistry updatableBeanRegistry() {
        return new DefaultUpdatableRegistry();
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
     * @return {@link UpdatableMemberInfoExtractor}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableMemberInfoExtractor updatableBeanMemberInfoExtractor() {
        return new UpdatableMemberInfoExtractor();
    }
}
