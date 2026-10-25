package org.ishika.controller;

import jakarta.validation.Valid;
import org.ishika.model.FileMetaData;
import org.ishika.request.FileRequest;
import org.ishika.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

  private final FileService fileService;

  @Autowired
  public FileController(final FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping
  public ResponseEntity<FileMetaData> createFile(@Valid @RequestBody final FileRequest fileRequest) {
    return ResponseEntity.ok(fileService.createFile(fileRequest));
  }
}