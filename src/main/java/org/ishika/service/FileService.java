package org.ishika.service;

import java.io.IOException;
import java.util.UUID;
import org.ishika.model.FileMetaData;
import org.ishika.request.FileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  FileMetaData createFile(FileRequest request);
  FileMetaData uploadFile(MultipartFile file, String folderId, UUID userId) throws IOException;
  String generateDownloadUrl(String fileId, UUID userId);
}