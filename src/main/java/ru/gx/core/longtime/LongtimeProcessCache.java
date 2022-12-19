package ru.gx.core.longtime;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LongtimeProcessCache {

    private final Cache<UUID, LongtimeProcess> cache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)

            .evictionListener((RemovalListener<UUID, LongtimeProcess>) (key, value, removalCause)
                    -> log.info("Evicted: {}, reason: {}", value, removalCause))
            .build();

    public void put(LongtimeProcess longtimeProcess) {
        cache.put(longtimeProcess.getId(), longtimeProcess);
    }

    public LongtimeProcess get(UUID id) {
        return cache.getIfPresent(id);
    }

}
