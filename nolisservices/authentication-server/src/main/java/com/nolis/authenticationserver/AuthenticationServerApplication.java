package com.nolis.authenticationserver;

import com.nolis.authenticationserver.security.JwtConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthenticationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServerApplication.class, args);
    }


/*    @Bean
    CommandLineRunner run(BCryptPasswordEncoder passwordEncoder) throws Exception {
       return args -> {
               System.out.println(passwordEncoder.encode("AdminNatalie"));
           };
    }*/
}
