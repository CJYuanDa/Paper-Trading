package com.example.paper.trading.config;

import com.example.paper.trading.exceptionHandling.MyAccessDeniedHandler;
import com.example.paper.trading.exceptionHandling.MyBasicAuthenticationEntryPoint;
import com.example.paper.trading.filter.CsrfCookieFilter;
import com.example.paper.trading.filter.JWTTokenGeneratorFilter;
import com.example.paper.trading.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@Profile("!prod")
public class SecurityConfig {

    @Autowired
    private JWTTokenGeneratorFilter jwtTokenGeneratorFilter;
    @Autowired
    private JWTTokenValidatorFilter jwtTokenValidatorFilter;

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

        /*
        * SESSION (Stateful):
        *
        * http
        *       // To make sure the SecurityContext (which holds information about the current user’s authentication)
        *       // is saved automatically after each request, without the need for explicit intervention.
        *       .securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
        *       // Because we're using our login page, therefore we need to tell spring to generate JSESSIONID for us
        *       .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
        * */

        // to receive inside the RequestHeader from the UI
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        // JWT (Stateless)
        http
                // ensures that no server-side session is created or managed, allowing your Spring Security
                // setup to rely entirely on JWT for authentication and authorization.
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
                        // to accept all kinds of headers from client side
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        // It instructs the server to expose the Authorization header to the client in a CORS response.
                        // By default, when a browser makes a cross-origin request,
                        // only a few specific headers are exposed to the client (such as Content-Type, Cache-Control, etc.).
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L); // seconds
                        return config;
                    }
                }))
                .csrf(csrfConfigurer -> csrfConfigurer
                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/register", "/api-login")
                        // JS and Angular can access the CSRF token manually, since HttpOnly is set to false.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Spring Security generates the CSRF token during request processing and stores it in a cookie
                // using the CookieCsrfTokenRepository.
                // After the CSRF token is stored, the request goes through CsrfCookieFilter for further processing.
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // only for http and not product
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register", "/api-login").permitAll()
                        .requestMatchers("/header-login").authenticated()
                        .requestMatchers("/role-user").hasRole("USER")
                        .requestMatchers("/role-admin").hasRole("ADMIN"))
                .formLogin(Customizer.withDefaults()) // LoginUrlAuthenticationEntryPoint
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

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        UserPwdAuthenticationProvider userPwdAuthenticationProvider = new UserPwdAuthenticationProvider(userDetailsService);
        ProviderManager providerManager = new ProviderManager(userPwdAuthenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }
}
