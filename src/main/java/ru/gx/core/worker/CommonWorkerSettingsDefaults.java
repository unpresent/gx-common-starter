package ru.gx.core.worker;

public interface CommonWorkerSettingsDefaults {
    int WAIT_ON_STOP_MS_DEFAULT = 3000;
    int WAIT_ON_RESTART_MS_DEFAULT = 30000;
    int MIN_TIME_PER_ITERATION_MS_DEFAULT = 1000;
    int TIMEOUT_RUNNER_LIFE_MS_DEFAULT = 20000;
    int PRINT_STATISTICS_EVERY_MS_DEFAULT = 1000;
}
