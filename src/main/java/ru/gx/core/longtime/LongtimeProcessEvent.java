package ru.gx.core.longtime;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Событие о необходимости обработать действие по указанному длительному процессу
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class LongtimeProcessEvent {

    /**
     * Идентификатор длительного процесса
     */
    @NotNull
    private final UUID longtimeProcessId;
}
