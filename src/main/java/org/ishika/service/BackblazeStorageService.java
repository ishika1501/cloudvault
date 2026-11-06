package org.ishika.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@RequiredArgsConstructor
public class BackblazeStorageService implements StorageService {

  private final S3Client s3Client;

  @Value("${backblaze.b2.bucket}")
  private String bucketName;

  @Override
  public String uploadFile(MultipartFile file) throws IOException {
    // Generate a unique key for this file.
    String storageKey = "cloudvault/" + UUID.randomUUID();
    String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
    PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(storageKey).contentType(contentType)
        .build();
    // Upload the actual file bytes.
    try (InputStream inputStream = file.getInputStream()) {
      s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
    }

    return storageKey;
  }

  @Override
  public void deleteFile(String storageKey) {
    DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(storageKey).build();
    s3Client.deleteObject(request);
  }
}