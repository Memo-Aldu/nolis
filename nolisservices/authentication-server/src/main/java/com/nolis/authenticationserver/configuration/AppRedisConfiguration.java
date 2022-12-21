package com.nolis.authenticationserver.configuration;

import com.nolis.commondata.dto.CacheDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.service.redis")
@Getter @Setter
public class AppRedisConfiguration {
        public List<CacheDTO> cache;
        public Integer port;
        public String host;
        // not pulling from config
}
