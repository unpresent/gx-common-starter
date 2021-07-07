package ru.gxfin.common.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gxfin.common.updatable.DefaultUpdatableRegistry;
import ru.gxfin.common.updatable.UpdatableAnnotationBeanPostProcessor;
import ru.gxfin.common.updatable.UpdatableMemberInfoExtractor;
import ru.gxfin.common.updatable.UpdatableRegistry;

/**
 * Конфигурация инфраструктурных бинов.
 *
 */
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
