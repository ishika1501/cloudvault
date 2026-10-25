package org.ishika.service;

public interface ObjectStorageService {

  String generateUploadUrl(String objectKey, String contentType);
}