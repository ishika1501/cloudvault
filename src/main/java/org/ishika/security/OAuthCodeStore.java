package org.ishika.security;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import org.ishika.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class OAuthCodeStore {

  private final ConcurrentHashMap<String, Entry> codes = new ConcurrentHashMap<>();
  private final SecureRandom random = new SecureRandom();

  public String create(LoginResponse response) {
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    codes.put(code, new Entry(response, System.currentTimeMillis() + 60_000));
    return code;
  }

  public LoginResponse consume(String code) {
    Entry entry = codes.remove(code);
    if (entry == null || entry.expiry < System.currentTimeMillis()) {
      return null;
    }
    return entry.response;
  }
  private record Entry(LoginResponse response, long expiry) {

  }
}
