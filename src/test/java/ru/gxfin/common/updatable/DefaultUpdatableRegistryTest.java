package ru.gxfin.common.updatable;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import ru.gxfin.common.AbstractMockTest;

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
