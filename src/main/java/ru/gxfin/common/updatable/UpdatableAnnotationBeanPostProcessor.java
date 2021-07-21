package ru.gxfin.common.updatable;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import ru.gxfin.common.annotations.Updatable;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Обработчик бинов, который позволяет регистрировать бины с обновляемыми свойствами.
 *
 * @author Adolin Negash 17.05.2021
 */
public class UpdatableAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, Ordered {

    private final Map<String, Pair<Object, Updatable>> beansMap = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private UpdatableRegistry registry;

    /**
     * Обрабатывает бины до того, как они будут обернуты в proxy-сервера.
     *
     * @param bean     бин.
     * @param beanName имя бина.
     * @return исходный бин.
     * @throws BeansException ошибка при обработке.
     */
    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {

        Updatable annotation = beanFactory.findAnnotationOnBean(beanName, Updatable.class);
        if (annotation != null) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            final String scope = beanDefinition.getScope();
            if (isNoneBlank(scope) && !SCOPE_SINGLETON.equals(scope)) {
                throw new IllegalStateException(
                        String.format("Cannot use scope [%s] with annotation UpdatableBean in bean [%s]", scope, beanName));
            }
            beansMap.put(beanName, Pair.of(bean, annotation));
        }
        return bean;
    }

    /**
     * Обрабатывает бины после того, как они обернуты в прокси.
     *
     * @param proxyBean бин.
     * @param beanName  имя бина.
     * @return исходный бин.
     * @throws BeansException ошибка при обработке.
     */
    @Override
    public Object postProcessAfterInitialization(@NotNull Object proxyBean, @NotNull String beanName) throws BeansException {

        Pair<Object, Updatable> pair = beansMap.get(beanName);
        if (pair != null) {
            registry.registerBean(beanName, pair.getLeft(), proxyBean, pair.getRight());
            beansMap.remove(beanName);
        }
        return proxyBean;
    }

    /**
     * Порядок выполнения BeanPostProcessor-а.
     */
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * Сеттер фабрики бинов.
     *
     * @param beanFactory фабрика бинов.
     */
    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
