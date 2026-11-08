package org.ishika.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;


  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    System.out.println("JWT FILTER HIT");
    System.out.println("REQUEST URI: " + request.getRequestURI());

    String authHeader = request.getHeader("Authorization");
    System.out.println("AUTH HEADER PRESENT: " + (authHeader != null));

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      System.out.println("TOKEN VALID: " + jwtService.isTokenValid(token));

      if (jwtService.isTokenValid(token)) {
        UUID userId = UUID.fromString(jwtService.extractUserId(token));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userId, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("AUTHENTICATED USER: " +
            SecurityContextHolder.getContext().getAuthentication().getPrincipal());
      }
    }

    filterChain.doFilter(request, response);
  }
}