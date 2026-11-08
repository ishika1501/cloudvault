package org.ishika.controller;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ishika.model.Folder;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;
import org.ishika.security.CurrentUserService;
import org.ishika.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FolderService folderService;
  private final CurrentUserService currentUserService;

  @PostMapping
  public Folder createFolder(@RequestBody FolderRequest folderRequest) {
    UUID userId = currentUserService.getCurrentUserId();
    folderRequest.setUserId(userId);
    return folderService.createFolder(folderRequest);
  }

  @GetMapping("/{folderId}/contents")
  public ResponseEntity<FolderContentsResponse> getFolderContents(@PathVariable String folderId) {
    System.out.println("FOLDER CONTROLLER HIT: " + folderId);
    UUID userId = currentUserService.getCurrentUserId();
    return ResponseEntity.ok(folderService.getFolderContents(folderId, userId));
  }

  @PutMapping("/{folderId}")
  public ResponseEntity<Folder> updateFolder(@PathVariable String folderId, @RequestBody FolderRequest folderRequest) {
    UUID userId = currentUserService.getCurrentUserId();
    folderRequest.setUserId(userId);
    return ResponseEntity.ok(folderService.updateFolder(folderRequest, folderId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFolder(@PathVariable String id) {
    UUID userId = currentUserService.getCurrentUserId();
    folderService.deleteFolder(id, userId);
    return ResponseEntity.noContent().build();
  }
}
