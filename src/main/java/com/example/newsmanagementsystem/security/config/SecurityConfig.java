package com.example.newsmanagementsystem.security.config;

import com.example.newsmanagementsystem.security.service.DatabaseUserDetailsService;
import com.example.newsmanagementsystem.security.service.JWTConfigService;
import com.example.newsmanagementsystem.user.entity.AppUser;
import com.example.newsmanagementsystem.user.entity.AuthProvider;
import com.example.newsmanagementsystem.user.entity.Role;
import com.example.newsmanagementsystem.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String GOOGLE = "google";
    private static final String GITHUB = "github";

    private final JWTConfigService jwtconfig;

    SecurityConfig(JWTConfigService jwtconfig) {
        this.jwtconfig = jwtconfig;
    }

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
                        .requestMatchers(
                                "/",
                                "/error",
                                "/oauth2/**",
                                "/login/**"
                        )
                        .permitAll()


                        .requestMatchers("/api/me")
                        .authenticated()
                        .anyRequest().authenticated()


                )
//                .httpBasic(Customizer.withDefaults())

                .formLogin(config -> config.successHandler((request, response, authentication) -> {
                   jwtconfig.onAuthLoginForm(request,response,authentication);

                }))
//                .oauth2Login((config)->config.successHandler((request,response,authentication)->{
//                            OidcUser googleUser=(OidcUser) authentication.getPrincipal();
//                            String username=googleUser.getAttribute("name");
//                            System.out.println(username);
//                            Optional<AppUser> u =repo.findByUsernameIgnoreCase(username);
//                            if(u.isEmpty()){
//                                AppUser newUser=new AppUser();
//                                newUser.setCreatedAt(LocalDateTime.now());
//                                newUser.setPasswordHash(passwordEncoder().encode("hello123"));
//                                newUser.setRole(Role.REPORTER);
//                                newUser.setUsername(username);
//                                repo.save(newUser);
//                            }
//                            AppUser user=db.generateToken(username);
//                            response.setContentType("application/json");
//                            response.getWriter().write(
//                                    "{\"accessToken\":\""
//                                            + user.getAccessToken()
//                                            + "\"}"
//                            );
//
//
//                        }))
//
                .csrf(  config->config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .oauth2Login(config -> config.successHandler(
                        (request, response, authentication) -> {

                            OAuth2AuthenticationToken oauthToken =
                                    (OAuth2AuthenticationToken) authentication;

                            OAuth2User oauthUser = oauthToken.getPrincipal();
                            String provider =
                                    oauthToken.getAuthorizedClientRegistrationId();

                            Object providerId;
                            AuthProvider obj;
                            if (GOOGLE.equals(provider)) {
                                obj=AuthProvider.GOOGLE;
                            }else{
                                obj = AuthProvider.GITHUB;
                            }


                            if (GOOGLE.equals(provider)) {
                                providerId = oauthUser.getAttribute("sub");
                            } else if (GITHUB.equals(provider)) {
                                providerId = oauthUser.getAttribute("id");
                            } else {
                                throw new IllegalArgumentException(
                                        "Unsupported provider: " + provider
                                );
                            }

                            if (providerId == null) {
                                throw new IllegalStateException(
                                        "Provider user ID is missing"
                                );
                            }

                            String username = oauthUser.getAttribute("name");


                            Optional<AppUser> u =
                                    repo.findByAuthProviderAndProviderUserId(obj,String.valueOf(providerId));
                            AppUser pass;
                            if (u.isEmpty()) {
                                AppUser newUser = new AppUser();
                                newUser.setCreatedAt(LocalDateTime.now());
                                newUser.setPasswordHash(
                                        passwordEncoder().encode("hello123")
                                );
                                newUser.setRole(Role.REPORTER);
                                newUser.setUsername(username);
                                newUser.setAuthProvider(obj);
                                newUser.setProviderUserId(String.valueOf(providerId));
                                repo.save(newUser);
                                pass=newUser;
                            }
                            else{
                                pass=u.get();
                            }
                            response.setContentType("application/json");
                            jwtconfig.onAuth2Login(request,response,pass);



                        }
                ))

                .oauth2ResourceServer(config -> config.opaqueToken(config2 -> config2.introspector(token -> {

                    return jwtconfig.verify(token);
                }) ));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
