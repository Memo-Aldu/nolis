package com.nolis.authenticationserver.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.nolis.authenticationserver.filter.JwtAuthorizationFilter;
import com.nolis.authenticationserver.filter.JwtUsernameAndPasswordAuthenticationFilter;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final JwtUtils jwtUtils;
    private final EndpointConfig endpointConfig;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtUsernameAndPasswordAuthenticationFilter jwtUsernameAndPasswordAuthenticationFilter =
                new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtUtils);
        jwtUsernameAndPasswordAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeRequests()
                    .antMatchers(String.valueOf(endpointConfig.openEndpoints)).permitAll()
                    .antMatchers(GET, String.valueOf(endpointConfig.adminEndpoints))
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                .anyRequest().authenticated()
                .and().addFilter(jwtUsernameAndPasswordAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtConfig, jwtUtils)
                        , JwtUsernameAndPasswordAuthenticationFilter.class);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
