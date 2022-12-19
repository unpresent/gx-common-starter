package ru.gx.core.channels;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class ChannelExecuteStatisticsManager {
    @NotNull
    private final Iterable<ChannelsConfiguration> configurations;

    public ChannelExecuteStatisticsManager(
            @NotNull final Iterable<ChannelsConfiguration> configurations,
            final long printStatisticsEveryMs
    ) {
        super();
        this.configurations = configurations;
        final var printTimer = new Timer(true);
        printTimer.scheduleAtFixedRate(new PrintTimerTask(), printStatisticsEveryMs, printStatisticsEveryMs);
    }

    /**
     * Task, который выводит в лог статистику
     */
    protected class PrintTimerTask extends TimerTask {
        @SneakyThrows
        @Override
        public void run() {
            log.info("channels-execute-statistics:");
            ChannelExecuteStatisticsManager.this.configurations
                    .forEach(config -> config
                            .getAll()
                            .forEach(channel -> log.info(
                                    channel.getExecuteStatistics()
                                            .getPrintableInfo()
                            )));
        }
    }

}
