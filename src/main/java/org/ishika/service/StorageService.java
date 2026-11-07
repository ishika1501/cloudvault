package org.ishika.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
  String initiateMultipartUpload(String objectKey, String contentType);
  String uploadFile(MultipartFile file) throws IOException;
  void deleteFile(String storageKey);
}