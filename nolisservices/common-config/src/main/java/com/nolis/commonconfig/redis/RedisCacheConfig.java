package com.nolis.commonconfig.redis;

import com.nolis.commondata.dto.CacheDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@Slf4j
public class RedisCacheConfig {
    private final String host;
    private final Integer port;
    private final List<CacheDTO> caches;

    public ReactiveRedisConnectionFactory myRedisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(60000))
                .clientName(this.host)
                .build();
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(
                this.host, this.port), clientConfig);
    }

    public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        this.caches.forEach(item -> {
            log.info("Creating Redis Cache with name {} and with ttl {} {}",
                    item.name(), item.ttl(), item.unit());
            cacheConfigurations.put(item.name(), RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.of(Long.parseLong(item.ttl()), ChronoUnit.valueOf(item.unit())))
                    .disableCachingNullValues()
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer
                            (new GenericJackson2JsonRedisSerializer())));
        });
        return RedisCacheManager.builder(lettuceConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
