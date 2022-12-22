package com.nolis.authenticationserver.configuration;

import com.nolis.commonconfig.redis.AppRedisCacheConfig;
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
    private final AppRedisProperties appRedisProperties;
    private final AppRedisCacheConfig appRedisCacheConfig;

    public RedisCacheConfig(AppRedisProperties appRedisProperties) {
        this.appRedisProperties = appRedisProperties;
        this.appRedisCacheConfig = new AppRedisCacheConfig(
                this.appRedisProperties.getHost(),
                this.appRedisProperties.getPort(),
                this.appRedisProperties.getCache()
        );
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory myRedisConnectionFactory() {
        log.info("Info -> Lettuce Connection Factory: "
                + this.appRedisProperties.getHost() + ":" + this.appRedisProperties.getPort());
        return appRedisCacheConfig.myRedisConnectionFactory();
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("Info -> Redis Cache Manager");
        return appRedisCacheConfig.cacheManager(lettuceConnectionFactory);
    }

}
