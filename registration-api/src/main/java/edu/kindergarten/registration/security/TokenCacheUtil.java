package edu.kindergarten.registration.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.kindergarten.registration.rest.UserInfo;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
@Startup
public class TokenCacheUtil {
    private Cache<UUID, UserInfo> refreshTokens;
    private Cache<String, Long> invalidatedJwt;
    @PostConstruct
    private void initialize() {
        refreshTokens = Caffeine.newBuilder()
                .maximumSize(1000)
                //.refreshAfterWrite(10, TimeUnit.MINUTES)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        invalidatedJwt = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfter(new Expiry<String, Long>() {
                    @Override
                    public long expireAfterCreate(
                            String key, Long value, long currentTime) {
                        long cacheExp = TimeUnit.SECONDS.toNanos(value - System.currentTimeMillis());
                        System.out.println(cacheExp);
                        return cacheExp;
                        //return jwtUtil.getExpDate(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusNanos(currentTime).toEpochSecond(ZoneOffset.UTC);
                    }
                    @Override
                    public long expireAfterUpdate(
                            String key, Long value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                    @Override
                    public long expireAfterRead(
                            String key, Long value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                }).build();
        /*invalidatedJwt = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES).build();*/
    }

    public Cache<UUID, UserInfo> getRefreshTokens() {
        return refreshTokens;
    }

    public Cache<String, Long> getInvalidatedJwt() {
        return invalidatedJwt;
    }
}
