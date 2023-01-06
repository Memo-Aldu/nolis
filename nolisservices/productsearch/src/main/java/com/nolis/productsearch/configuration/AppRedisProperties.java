package com.nolis.productsearch.configuration;

import com.nolis.commondata.dto.CacheDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.service.redis")
@Getter @Setter
public class AppRedisProperties {
        public List<CacheDTO> cache;
        public Integer port;
        public String host;
}
