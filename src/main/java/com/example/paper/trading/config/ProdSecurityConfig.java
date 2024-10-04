package com.example.paper.trading.config;

import com.example.paper.trading.exceptionHandling.MyAccessDeniedHandler;
import com.example.paper.trading.exceptionHandling.MyBasicAuthenticationEntryPoint;
import com.example.paper.trading.filter.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    @Autowired
    private JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    @Autowired
    private JWTTokenValidatorFilter jwtTokenValidatorFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        /*
        * SESSION (Stateful)
        *
        * http
        *       .securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
        *       .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
        *
        * */

        // JWT (Stateless)

        http
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                .csrf(csrfConfigurer -> csrfConfigurer
                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/register", "/api-login")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                // .requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) // only https can access here
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register", "/api-login").permitAll()
                        .requestMatchers("/header-login").authenticated()
                        .requestMatchers("/role-user").hasRole("USER")
                        .requestMatchers("/role-admin").hasRole("ADMIN"))
                .formLogin(Customizer.withDefaults())
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

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        ProdUserPwdAuthenticationProvider prodUserPwdAuthenticationProvider =
                new ProdUserPwdAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(prodUserPwdAuthenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }
}
