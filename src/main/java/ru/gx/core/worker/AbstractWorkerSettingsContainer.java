package ru.gx.core.worker;

public abstract class AbstractWorkerSettingsContainer implements WorkerSettingsContainer {
    public static final String WAIT_ON_STOP_MS = "wait-on-stop-ms";
    public static final String WAIT_ON_RESTART_MS = "wait-on-restart-ms";
    public static final String MIN_TIME_PER_ITERATION_MS = "min-time-per-iteration-ms";
    public static final String TIMEOUT_RUNNER_LIFE_MS = "timeout-runner-life-ms";
    public static final String PRINT_STATISTICS_EVERY_MS = "print-statistics-every-ms";

    @Override
    public abstract int getWaitOnStopMs();

    @Override
    public abstract int getWaitOnRestartMs();

    @Override
    public abstract int getMinTimePerIterationMs();

    @Override
    public abstract int getTimeoutRunnerLifeMs();

    @Override
    public abstract int getPrintStatisticsEveryMs();
}
