package com.example.paper.trading.config;

import com.example.paper.trading.exceptionHandling.MyAccessDeniedHandler;
import com.example.paper.trading.exceptionHandling.MyBasicAuthenticationEntryPoint;
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

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) // only https can access here
                .cors(cors -> cors.disable())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/role-user").hasRole("USER")
                        .requestMatchers("/role-admin").hasRole("ADMIN"))
                .formLogin(Customizer.withDefaults())  // LoginUrlAuthenticationEntryPoint
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
