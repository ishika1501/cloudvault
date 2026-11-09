package org.ishika.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ishika.model.FileUploadSession;
import org.ishika.request.FileUploadSessionRequest;
import org.ishika.service.FileUploadSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file/upload-sessions")
public class FileUploadSessionController {

  private final FileUploadSessionService fileUploadSessionService;

  @PostMapping("/initiate")
  public ResponseEntity<FileUploadSession> initiateUpload(@Valid @RequestBody FileUploadSessionRequest request) {
    final FileUploadSession fileUploadSession = fileUploadSessionService.initiateFileUploadSession(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadSession);
  }
}
