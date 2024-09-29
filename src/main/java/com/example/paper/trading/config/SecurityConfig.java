package com.example.paper.trading.config;

import com.example.paper.trading.exceptionHandling.MyAccessDeniedHandler;
import com.example.paper.trading.exceptionHandling.MyBasicAuthenticationEntryPoint;
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
        * http
        *       .authorizeHttpRequests(request -> request.anyRequest().permitAll());
        *       .authorizeHttpRequests(request -> request.anyRequest().denyAll());
        *       // invalid session user will be redirect to "/login" url
        *       // , the valid session number is set to one
        *       // , prevent the first session from delete
        *       .sessionManagement(smc -> smc.invalidSessionUrl("/login").maximumSessions(1).maxSessionsPreventsLogin(true).expiredUrl())
        * */

        http

                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // only for http and not product
                .cors(cors -> cors.disable())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/role-user").hasRole("USER")
                        .requestMatchers("/role-admin").hasRole("ADMIN"))
                .formLogin(Customizer.withDefaults()) // LoginUrlAuthenticationEntryPoint
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new MyBasicAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new MyAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
