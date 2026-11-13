
package org.ishika.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final GoogleOAuthSuccessHandler googleOAuthSuccessHandler;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      GoogleOAuthSuccessHandler googleOAuthSuccessHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.googleOAuthSuccessHandler = googleOAuthSuccessHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())

        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )

        .authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

            // Normal email/password registration and login
            .requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()

            // Google OAuth
            .requestMatchers("/oauth2/**", "/login/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/google/exchange").permitAll()

            // All other endpoints require JWT authentication
            .anyRequest().authenticated()
        )

        .oauth2Login(oauth2 -> oauth2
            .successHandler(googleOAuthSuccessHandler)
        )

        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }
}
