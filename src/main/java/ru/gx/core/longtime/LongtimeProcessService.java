package ru.gx.core.longtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Сервис регистрации длительно выполняемых задач.
 */
@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@Slf4j
public class LongtimeProcessService {

    private final LongtimeProcessCache cache;

    /**
     * Создание описателя длительно выполняемой задачи. Создание происходит в потоке обработки REST-запроса.
     * @param username Имя пользователя, под которым создается задача. Для использования в будущем.
     * @return Описатель длительно выполняемой задачи.
     */
    @NotNull
    public LongtimeProcess createLongtimeProcess(@Nullable final String username) {
        final var uuid = UUID.randomUUID();
        final var longtimeProcess = new LongtimeProcess(uuid);
        longtimeProcess.setCreated(ZonedDateTime.now());
        longtimeProcess.setUsername(username);
        cache.put(longtimeProcess);
        log.info("CREATED {}", longtimeProcess);
        return longtimeProcess;
    }

    /**
     * Регистрация факта запуска обработки длительной задачи.
     * @param longtimeProcessId Идентификатор длительной задачи.
     * @return Описатель длительной задачи.
     */
    @Nullable
    public LongtimeProcess startLongtimeProcess(@NotNull final UUID longtimeProcessId) {
        final var longtimeProcess = getLongtimeProcess(longtimeProcessId);
        if (longtimeProcess == null) {
            return null;
        }
        longtimeProcess.setStarted(ZonedDateTime.now());
        longtimeProcess.setStatus(LongtimeProcessStatus.IN_PROCESS);
        cache.put(longtimeProcess);
        log.info("STARTED {}", longtimeProcess);
        return longtimeProcess;
    }

    /**
     * Получить описатель длительно выполняемой задачи
     * @param longtimeProcessId Идентификатор длительной задачи.
     * @return Описатель длительной задачи.
     */
    @Nullable
    public LongtimeProcess getLongtimeProcess(@NotNull final UUID longtimeProcessId) {
        return cache.get(longtimeProcessId);
    }

    public void finishLongtimeProcess(@NotNull final UUID longtimeProcessId) {
        final var longtimeProcess = cache.get(longtimeProcessId);
        if (longtimeProcess == null) {
            return;
        }
        longtimeProcess.setFinished(ZonedDateTime.now());
        longtimeProcess.setStatus(LongtimeProcessStatus.FINISHED);
        cache.put(longtimeProcess);
        log.info("FINISHED {}", longtimeProcess);
    }

    public void setErrorLongtimeProcess(@NotNull final UUID longtimeProcessId, @NotNull final String error) {
        final var longtimeProcess = getLongtimeProcess(longtimeProcessId);
        if (longtimeProcess == null) {
            return;
        }
        longtimeProcess.setFinished(ZonedDateTime.now());
        longtimeProcess.setStatus(LongtimeProcessStatus.ERROR);
        longtimeProcess.setResult(error);
        cache.put(longtimeProcess);
        log.info("ERROR {}", longtimeProcess);
    }
}
