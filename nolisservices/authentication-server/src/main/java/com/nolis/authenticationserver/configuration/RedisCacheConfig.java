package com.nolis.authenticationserver.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

@EnableCaching
@Component
@Getter @Setter @Slf4j
public class RedisCacheConfig {
    private final AppRedisConfiguration appRedisConfiguration;
    private final com.nolis.commonconfig.redis.RedisCacheConfig commonRedisCacheConfig;

    public RedisCacheConfig(AppRedisConfiguration appRedisConfiguration) {
        this.appRedisConfiguration = appRedisConfiguration;
        this.commonRedisCacheConfig = new com.nolis.commonconfig.redis.RedisCacheConfig(
                this.appRedisConfiguration.getHost(),
                this.appRedisConfiguration.getPort(),
                this.appRedisConfiguration.getCache()
        );
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory myRedisConnectionFactory() {
        log.info("Info -> Lettuce Connection Factory: "
                + this.appRedisConfiguration.host + ":" + this.appRedisConfiguration.port);
        return commonRedisCacheConfig.myRedisConnectionFactory();
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("Info -> Redis Cache Manager");
        return commonRedisCacheConfig.cacheManager(lettuceConnectionFactory);
    }

}
