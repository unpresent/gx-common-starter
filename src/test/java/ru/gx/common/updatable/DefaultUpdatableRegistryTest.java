package ru.gx.common.updatable;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import ru.gx.common.AbstractMockTest;

/**
 * Класс для тестирования {@link DefaultUpdatableRegistry}
 *
 * @author Adolin Negash 20.05.2021
 */
class DefaultUpdatableRegistryTest extends AbstractMockTest {

    @InjectMocks
    private DefaultUpdatableRegistry subj;

    @Mock
    private Environment environment;

    @Mock
    private UpdatableMemberInfoExtractor infoExtractor;

    @Test
    void shouldRegisterBean() {
        // subj.registerBean();
    }
}
