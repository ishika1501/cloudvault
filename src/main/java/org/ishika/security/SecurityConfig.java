package org.ishika.security;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final GoogleOAuthSuccessHandler googleOAuthSuccessHandler;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
      GoogleOAuthSuccessHandler googleOAuthSuccessHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.googleOAuthSuccessHandler = googleOAuthSuccessHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.cors(Customizer.withDefaults())

        .csrf(csrf -> csrf.disable())

        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

        .authorizeHttpRequests(auth -> auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

            // CORS preflight requests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // Normal email/password registration and login
            .requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()

            // Google OAuth
            .requestMatchers("/oauth2/**", "/login/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/google/exchange").permitAll()

            // All other endpoints require authentication
            .anyRequest().authenticated())

        // Return 401 for unauthenticated API requests
        .exceptionHandling(ex -> ex.authenticationEntryPoint(
            (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

        // Google OAuth login
        .oauth2Login(oauth2 -> oauth2.successHandler(googleOAuthSuccessHandler))

        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:5173"));

    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
