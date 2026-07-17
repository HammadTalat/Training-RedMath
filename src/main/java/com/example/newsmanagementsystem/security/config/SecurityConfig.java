package com.example.newsmanagementsystem.security.config;

import com.example.newsmanagementsystem.security.service.DatabaseUserDetailsService;
import com.example.newsmanagementsystem.user.entity.AppUser;
import com.example.newsmanagementsystem.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DatabaseUserDetailsService db, UserRepository repo) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/csrf").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/news/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/news/**")
                        .hasAnyRole("REPORTER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(config -> config.successHandler((request, response, authentication) -> {
                    AppUser user = db.generateToken(authentication.getName());
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"accessToken\":\""
                                    + user.getAccessToken()
                                    + "\"}"
                    );
                }))
                .oauth2ResourceServer(config -> config.opaqueToken(config2 -> config2.introspector(token -> {

                    Optional<AppUser> user = repo.findByAccessToken(token);
                    if(user.isPresent()){
                        AppUser u = user.get();
                        System.out.println(u.getRole().toString());
                        return new DefaultOAuth2AuthenticatedPrincipal(u.getUsername(), Map.of("Username" ,u.getUsername(),"User role" ,u.getRole()),
                        AuthorityUtils.createAuthorityList("ROLE_" + u.getRole().toString()));
                    }
                        return null;
                }) ))
                .csrf(  config->config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
