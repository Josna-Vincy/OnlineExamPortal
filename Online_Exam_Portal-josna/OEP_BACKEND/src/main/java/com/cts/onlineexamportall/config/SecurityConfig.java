package com.cts.onlineexamportall.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;   
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.cts.onlineexamportall.filter.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    // @Autowired
    // private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        logger.info("Configuring GrantedAuthorityDefaults");
        return new GrantedAuthorityDefaults(""); // Remove "ROLE_" prefix
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers( "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/users","/api/auth/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/admin/**").permitAll()
                        .requestMatchers("/api/auth/update", "/api/auth/updatePassword","/api/auth/getRole").hasAnyRole("ADMIN", "STUDENT", "EXAMINER")
                        .requestMatchers(HttpMethod.OPTIONS, "/student/**").permitAll()
                        .requestMatchers("/student/**").hasAnyRole("STUDENT","EXAMINER","ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN","EXAMINER")
                        .requestMatchers(HttpMethod.OPTIONS,"/examiner/**").permitAll()
                        .requestMatchers("/examiner/**").hasAnyRole("EXAMINER","ADMIN")
                        // .requestMatchers("/examiner/allExams").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     logger.info("Configuring authentication provider");
    //     DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //     provider.setPasswordEncoder(passwordEncoder());
    //     provider.setUserDetailsService(userDetailsService);
    //     return provider;
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Configuring authentication manager");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configuring password encoder");
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        logger.info("Configuring LocalValidatorFactoryBean");
        return new LocalValidatorFactoryBean();
    }
}