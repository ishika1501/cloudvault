package org.ishika.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

  private UUID id;
  private String email;
  private String displayName;
  private String token;
}