package com.example.paper.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!prod")
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        /*
        * http.authorizeHttpRequests(request -> request.anyRequest().permitAll());
        * http.authorizeHttpRequests(request -> request.anyRequest().denyAll());
        * */
        http
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // only for http and not product
                .cors(cors -> cors.disable())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/").hasRole("USER"))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
