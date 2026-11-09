package org.ishika.controller;

import org.ishika.response.LoginResponse;
import org.ishika.security.OAuthCodeStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

  private final OAuthCodeStore codeStore;

  public GoogleAuthController(OAuthCodeStore codeStore) {
    this.codeStore = codeStore;
  }

  @PostMapping("/exchange")
  public LoginResponse exchangeCode(@RequestBody Map<String, String> request) {

    String code = request.get("code");

    if (code == null || code.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization code is required");
    }

    LoginResponse response = codeStore.consume(code);

    if (response == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired authorization code");
    }

    return response;
  }
}
