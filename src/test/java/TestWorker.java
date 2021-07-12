import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gxfin.common.worker.Worker;
import worker.TheWorker;

@Slf4j
@Testable
public class TestWorker {
    @Test
    public void testWorker() {
        final Worker worker = new TheWorker("TestWorker");
    }
}
