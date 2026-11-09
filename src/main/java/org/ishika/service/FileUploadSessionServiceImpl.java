package org.ishika.service;

import static org.ishika.model.FileUploadSessionStatus.INITIATED;
import static org.ishika.model.FileUploadSessionStatus.INITIATING;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ishika.model.FileMetaData;
import org.ishika.model.FileUploadSession;
import org.ishika.repository.FileUploadSessionRepository;
import org.ishika.request.FileRequest;
import org.ishika.request.FileUploadSessionRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUploadSessionServiceImpl implements FileUploadSessionService {

  private final FileService fileService;
  private final FolderService folderService;
  private final StorageService storageService;
  private final FileUploadSessionRepository fileUploadSessionRepository;

  @Override
  public FileUploadSession initiateFileUploadSession(final FileUploadSessionRequest request) {
    validateRequest(request);
    FileUploadSession fileUploadSession = prepareUploadSession();
    fileUploadSessionRepository.save(fileUploadSession);
    final FileMetaData file = fileService.createFile(prepareFileRequest(request));
    return postFileCreationTask(fileUploadSession, file);
  }

  private FileUploadSession postFileCreationTask(final FileUploadSession fileUploadSession,
      final FileMetaData fileMetaData) {
    final String uploadId = storageService.initiateMultipartUpload(fileMetaData.getStorageKey(),
        fileMetaData.getContentType());
    fileUploadSession.setFileId(fileMetaData.getFileId());
    fileUploadSession.setStatus(INITIATED);
    fileUploadSession.setStorageUploadId(uploadId);
    fileUploadSessionRepository.save(fileUploadSession);
    return fileUploadSession;
  }

  private FileRequest prepareFileRequest(final FileUploadSessionRequest request) {
    FileRequest fileRequest = new FileRequest();
    fileRequest.setName(request.getFileName());
    fileRequest.setUserId(request.getOwnerId());
    fileRequest.setContentType(request.getContentType());
    fileRequest.setSizeBytes(request.getSizeBytes());
    fileRequest.setFolderId(request.getFolderId());
    return fileRequest;
  }

  private FileUploadSession prepareUploadSession() {
    FileUploadSession fileUploadSession = new FileUploadSession();
    fileUploadSession.setId(UUID.randomUUID().toString());
    fileUploadSession.setStatus(INITIATING);
    Instant now = Instant.now();
    fileUploadSession.setCreatedAt(Timestamp.from(now));
    fileUploadSession.setUpdatedAt(Timestamp.from(now));
    return fileUploadSession;
  }

  private void validateRequest(FileUploadSessionRequest request) {
    folderService.getFolderForUserOrThrow(request.getFolderId(), request.getOwnerId());
  }
}
