package com.ywcheong.short4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(CsrfConfigurer::disable)  // 시스템 설계상 세션을 사용하지 않으므로 비활성화해도 괜찮음
                .authorizeHttpRequests(registry -> registry.requestMatchers(new String[]{"/error"})
                        .permitAll()
                        .anyRequest()
                        .permitAll());
        return httpSecurity.build();
    }
}
