package org.ishika.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ishika.enums.FileStatus;
import org.ishika.model.FileMetaData;
import org.ishika.repository.FileRepository;
import org.ishika.request.FileRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FolderService folderService;
  private final FileRepository fileRepository;

  @Override
  public FileMetaData createFile(final FileRequest request) {
    validateRequest(request);
    final FileMetaData fileMetaData = prepareFileMetaData(request);
    return fileRepository.save(fileMetaData);
  }

  private FileMetaData prepareFileMetaData(final FileRequest request) {
    FileMetaData fileMetaData = new FileMetaData();
    fileMetaData.setSize(request.getSizeBytes());
    fileMetaData.setStatus(FileStatus.PENDING);
    fileMetaData.setFolderId(request.getFolderId());
    fileMetaData.setContentType(request.getContentType());
    fileMetaData.setFileId(UUID.randomUUID().toString());
    fileMetaData.setCreatedAt(Timestamp.from(Instant.now()));
    return fileMetaData;
  }

  private void validateRequest(final FileRequest request) {
    folderService.getFolderOrThrow(request.getFolderId());
  }
}