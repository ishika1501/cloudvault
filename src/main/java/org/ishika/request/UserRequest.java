package org.ishika.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must contain at least 8 characters")
  private String password;

  @NotBlank(message = "Display name is required")
  @Size(max = 100, message = "Display name cannot exceed 100 characters")
  private String displayName;
}