package org.ishika.controller;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ishika.model.Folder;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;
import org.ishika.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FolderService folderService;

  @PostMapping
  public Folder createFolder(@RequestBody FolderRequest folderRequest){
    return folderService.createFolder(folderRequest);
  }

  @GetMapping("/{id}/contents")
  public FolderContentsResponse getContents(@PathVariable String id) {
    return folderService.getFolderContents(id);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    folderService.deleteFolder(id);
    return ResponseEntity.noContent().build();
  }

}
