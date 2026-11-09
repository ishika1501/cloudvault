package org.ishika.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;

@Data
public class FileUploadSessionRequest {

  @NotNull(message = "Folder ID is required")
  private String folderId;

  @NotNull
  private UUID ownerId;

  @NotBlank(message = "File name is required")
  @Size(max = 255, message = "File name must not exceed 255 characters")
  private String fileName;

  @NotBlank(message = "Content type is required")
  @Size(max = 255, message = "Content type must not exceed 255 characters")
  private String contentType;

  @NotNull(message = "File size is required")
  @PositiveOrZero(message = "File size cannot be negative")
  private long sizeBytes;
}