package org.ishika.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.ishika.response.LoginResponse;
import org.ishika.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {

  private final UserService userService;
  private final OAuthCodeStore codeStore;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  public GoogleOAuthSuccessHandler(UserService userService, OAuthCodeStore codeStore) {
    this.userService = userService;
    this.codeStore = codeStore;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
    Map<String, Object> attributes = googleUser.getAttributes();

    String googleId = (String) attributes.get("sub");
    String email = (String) attributes.get("email");
    String displayName = (String) attributes.get("name");
    Boolean emailVerified = (Boolean) attributes.get("email_verified");

    if (!Boolean.TRUE.equals(emailVerified) || googleId == null || email == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Google email is not verified");
      return;
    }

    LoginResponse loginResponse = userService.loginWithGoogle(googleId, email, displayName);
    String code = codeStore.create(loginResponse);
    response.sendRedirect(frontendUrl + "/oauth/callback?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8));
  }
}
