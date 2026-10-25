package org.ishika.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import lombok.Data;

@Data
public class FileRequest {

  @NotNull(message = "Folder ID is required")
  String folderId;

  @NotNull(message = "User ID is required")
  UUID userId;

  @NotBlank(message = "File name is required")
  @Size(max = 255, message = "File name must not exceed 255 characters")
  String name;

  @Size(max = 255, message = "Content type must not exceed 255 characters")
  String contentType;

  @NotNull(message = "File size is required")
  @Positive(message = "File size must be greater than zero")
  Long sizeBytes;
}
