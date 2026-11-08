package org.ishika.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ishika.enums.FileStatus;
import org.ishika.model.FileMetaData;
import org.ishika.repository.FileRepository;
import org.ishika.request.FileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

  private final FolderService folderService;
  private final FileRepository fileRepository;
  private final StorageService storageService;

  private final S3Presigner s3Presigner;

  @Value("${backblaze.b2.bucket}")
  private String bucketName;

  @Override
  public FileMetaData createFile(final FileRequest request) {
    validateRequest(request);
    FileMetaData fileMetaData = prepareFileMetaData(request);
    return fileRepository.save(fileMetaData);
  }

  @Override
  public FileMetaData uploadFile(MultipartFile file, String folderId, UUID userId) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
    }
    if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is required");
    }
    // 2. Check that the folder belongs to the user.
    folderService.getFolderForUserOrThrow(folderId, userId);
    // 3. Upload the actual file to Backblaze.
    String storageKey = storageService.uploadFile(file);
    try {
      // 4. Prepare metadata.
      FileMetaData metadata = new FileMetaData();

      metadata.setFileId(UUID.randomUUID().toString());
      metadata.setFileName(file.getOriginalFilename());
      metadata.setStorageKey(storageKey);
      metadata.setOwnerId(userId);
      metadata.setFolderId(folderId);
      metadata.setSize(file.getSize());
      metadata.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
      metadata.setStatus(FileStatus.PENDING);

      Timestamp now = Timestamp.from(Instant.now());

      metadata.setCreatedAt(now);
      metadata.setUpdatedAt(now);
      // 5. Save metadata in PostgreSQL.
      return fileRepository.save(metadata);

    } catch (RuntimeException exception) {

      // If database saving fails, clean up the uploaded object.
      try {
        storageService.deleteFile(storageKey);
      } catch (RuntimeException cleanupException) {
        log.error("Failed to clean up uploaded object: {}", storageKey, cleanupException);
      }

      throw exception;
    }
  }


  @Override
  public String generateDownloadUrl(String fileId, UUID userId) {

    // 1. Find file metadata in PostgreSQL
    FileMetaData metadata = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    // 2. Verify file ownership
    if (!metadata.getOwnerId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    // 3. Create request for the file stored in Backblaze
    GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(metadata.getStorageKey())
        .build();

    // 4. Generate presigned URL valid for 10 minutes
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest).build();

    // 5. Generate and return URL
    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  @Override
  public void softDeleteFile(String fileId, UUID userId) {

    // 1. Find the file in PostgreSQL
    FileMetaData metadata = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    // 2. Check whether the user owns this file
    if (!metadata.getOwnerId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    // 3. Check whether the file is already deleted
    if (metadata.getStatus() == FileStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File already deleted");
    }

    // Save the current status before deleting
    metadata.setPreviousStatus(metadata.getStatus());

    // Mark file as deleted
    metadata.setStatus(FileStatus.DELETED);

    // 5. Record deletion time
    Timestamp now = Timestamp.from(Instant.now());
    metadata.setDeletedAt(now);
    metadata.setUpdatedAt(now);

    // 6. Save changes in PostgreSQL
    fileRepository.save(metadata);
  }

  @Override
  public void hardDeleteFile(String fileId, UUID userId) {
    // 1. Find file metadata
    FileMetaData metadata = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    // 2. Verify ownership
    if (!metadata.getOwnerId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }
    // 3. Only allow permanent deletion of soft-deleted files
    if (metadata.getStatus() != FileStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be soft deleted before permanent deletion");
    }
    // 4. Delete actual file from Backblaze B2
    storageService.deleteFile(metadata.getStorageKey());
    // 5. Delete metadata from PostgreSQL
    fileRepository.delete(metadata);
  }


  @Override
  public void restoreFile(String fileId, UUID userId) {

    // 1. Find the file
    FileMetaData metadata = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    // 2. Verify ownership
    if (!metadata.getOwnerId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    // 3. Check whether the file is soft-deleted
    if (metadata.getStatus() != FileStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is not deleted");
    }

    // 4. Restore its previous status
    if (metadata.getPreviousStatus() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Previous file status is missing");
    }

    metadata.setStatus(metadata.getPreviousStatus());

    // 5. Clear deletion information
    metadata.setPreviousStatus(null);
    metadata.setDeletedAt(null);

    // 6. Update timestamp
    metadata.setUpdatedAt(Timestamp.from(Instant.now()));

    // 7. Save changes
    fileRepository.save(metadata);
  }

  @Override
  public void renameFile(String fileId, String newFileName, UUID userId) {

    FileMetaData metadata = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    if (!metadata.getOwnerId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    if (metadata.getStatus() == FileStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot rename a deleted file");
    }

    if (newFileName == null || newFileName.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name cannot be empty");
    }

    metadata.setFileName(newFileName.trim());
    metadata.setUpdatedAt(Timestamp.from(Instant.now()));

    fileRepository.save(metadata);
  }

  private FileMetaData prepareFileMetaData(FileRequest request) {
    FileMetaData metadata = new FileMetaData();
    metadata.setFileId(UUID.randomUUID().toString());
    metadata.setOwnerId(request.getUserId());
    metadata.setFolderId(request.getFolderId());
    metadata.setFileName(request.getName());
    metadata.setSize(request.getSizeBytes());
    metadata.setContentType(request.getContentType());
    metadata.setStatus(FileStatus.PENDING);

    Timestamp now = Timestamp.from(Instant.now());

    metadata.setCreatedAt(now);
    metadata.setUpdatedAt(now);

    return metadata;
  }

  private void validateRequest(FileRequest request) {
    folderService.getFolderForUserOrThrow(request.getFolderId(), request.getUserId());
  }
}