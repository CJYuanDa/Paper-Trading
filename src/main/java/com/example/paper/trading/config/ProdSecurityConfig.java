package com.example.paper.trading.config;

import com.example.paper.trading.exceptionHandling.MyAccessDeniedHandler;
import com.example.paper.trading.exceptionHandling.MyBasicAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        // allow all http methods
                        config.setAllowedMethods(Collections.singletonList("*"));
                        // enable accepting the user credentials or any other applicable cookies
                        // from the UI origin to backend server
                        config.setAllowCredentials(true);
                        // to accept all kinds of headers
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); // seconds
                        return config;
                    }
                }))
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                // .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) // only https can access here
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/role-user").hasRole("USER")
                        .requestMatchers("/role-admin").hasRole("ADMIN"))
                .formLogin(Customizer.withDefaults())  // LoginUrlAuthenticationEntryPoint
                // only for stateful API (Session-Based)
                .logout(loc -> loc
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new MyBasicAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new MyAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // To prevent user using easy (leaked, breach) password
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
