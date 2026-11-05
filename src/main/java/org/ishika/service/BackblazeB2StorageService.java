package org.ishika.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class BackblazeB2StorageService implements ObjectStorageService {

  private final S3Presigner s3Presigner;
  private final String bucketName;

  public BackblazeB2StorageService(S3Presigner s3Presigner, @Value("${backblaze.b2.bucket}") String bucketName) {
    this.s3Presigner = s3Presigner;
    this.bucketName = bucketName;
  }

  @Override
  public String generateUploadUrl(String objectKey, String contentType) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(objectKey)
        .contentType(contentType).build();
    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10))
        .putObjectRequest(putObjectRequest).build();
    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }
}