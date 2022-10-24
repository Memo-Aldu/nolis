package com.nolis.authenticationserver.security;

import com.nolis.authenticationserver.apihelper.ResponseHandler;
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
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EndpointConfig endpointConfig;
    private final JwtConfig jwtConfig;
    private final JwtUtils jwtUtils;
    private final ResponseHandler responseHandler;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtUsernameAndPasswordAuthenticationFilter jwtUsernameAndPasswordAuthenticationFilter =
                new JwtUsernameAndPasswordAuthenticationFilter(
                        authenticationManager(), jwtConfig, jwtUtils, responseHandler);
        jwtUsernameAndPasswordAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeRequests()
                    .antMatchers(endpointConfig.openEndpoints).permitAll()
                    .antMatchers(endpointConfig.adminEndpoints)
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                .anyRequest().authenticated()
                .and().addFilter(jwtUsernameAndPasswordAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtConfig, jwtUtils, endpointConfig, responseHandler),
                        JwtUsernameAndPasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }
}
