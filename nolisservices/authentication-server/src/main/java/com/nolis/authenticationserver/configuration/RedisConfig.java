package com.nolis.authenticationserver.configuration;

import com.nolis.commondata.dto.CacheDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableCaching
@Configuration
@ConfigurationProperties(prefix = "application.service.redis")
@AllArgsConstructor
@Getter @Setter
public class RedisConfig {
    public final List<CacheDTO> cache;

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
            cache.forEach((item) -> {
                System.out.println("Creating Redis Cache with name " +item.name()+ " with ttl " + item.ttl() + " " + item.unit());
                configurationMap.put(item.name(), RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(Long.parseLong(item.ttl()), ChronoUnit.valueOf(item.unit())))
                        .disableCachingNullValues()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()))
                );

            });
            builder.withInitialCacheConfigurations(configurationMap);
        };
    }


}
