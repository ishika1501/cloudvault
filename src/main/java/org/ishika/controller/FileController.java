package org.ishika.controller;

import jakarta.validation.Valid;
import org.ishika.model.FileMetaData;
import org.ishika.request.FileRequest;
import org.ishika.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.UUID;
import org.ishika.security.CurrentUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

  private final FileService fileService;
  private final CurrentUserService currentUserService;

  @Autowired
  public FileController(final FileService fileService, final CurrentUserService currentUserService) {
    this.fileService = fileService;
    this.currentUserService = currentUserService;
  }

  @PostMapping
  public ResponseEntity<FileMetaData> createFile(@Valid @RequestBody final FileRequest fileRequest) {
    UUID userId = currentUserService.getCurrentUserId();
    fileRequest.setUserId(userId);
    return ResponseEntity.ok(fileService.createFile(fileRequest));
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file,
      @RequestParam("folderId") String folderId) throws IOException {
    UUID userId = currentUserService.getCurrentUserId();
    FileMetaData metadata = fileService.uploadFile(file, folderId, userId);
    return ResponseEntity.ok(metadata);
  }

  @GetMapping("/{fileId}/download")
  public ResponseEntity<String> downloadFile(@PathVariable String fileId) {
    UUID userId = currentUserService.getCurrentUserId();
    String downloadUrl = fileService.generateDownloadUrl(fileId, userId);
    return ResponseEntity.ok(downloadUrl);
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<Void> softDeleteFile(@PathVariable String fileId) {
    UUID userId = currentUserService.getCurrentUserId();
    fileService.softDeleteFile(fileId, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{fileId}/permanent")
  public ResponseEntity<Void> hardDeleteFile(@PathVariable String fileId) {
    UUID userId = currentUserService.getCurrentUserId();
    fileService.hardDeleteFile(fileId, userId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{fileId}/restore")
  public ResponseEntity<Void> restoreFile(@PathVariable String fileId) {
    UUID userId = currentUserService.getCurrentUserId();
    fileService.restoreFile(fileId, userId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{fileId}/rename")
  public ResponseEntity<Void> renameFile(@PathVariable String fileId, @RequestParam String newFileName) {
    UUID userId = currentUserService.getCurrentUserId();
    fileService.renameFile(fileId, newFileName, userId);
    return ResponseEntity.noContent().build();
  }
}