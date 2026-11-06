package org.ishika.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
  String uploadFile(MultipartFile file) throws IOException;
  void deleteFile(String storageKey);
}