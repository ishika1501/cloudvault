package org.ishika.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadSessionResponse {

  private UUID fileId;
  private UUID uploadSessionId;
  private String uploadId;
  private String objectKey;
  private Long chunkSizeBytes;
  private String status;
}