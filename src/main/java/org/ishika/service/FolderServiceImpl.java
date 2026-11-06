package org.ishika.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.ishika.enums.FileStatus;
import org.ishika.model.Folder;
import org.ishika.repository.FolderRepository;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.ishika.model.FileMetaData;
import org.ishika.repository.FileRepository;

@Service
public class FolderServiceImpl implements FolderService {

  private final UserService userService;
  private final FolderRepository folderRepository;
  private final FileRepository fileRepository;


  public FolderServiceImpl(@Lazy UserService userService, FolderRepository folderRepository,
      FileRepository fileRepository) {

    this.userService = userService;
    this.folderRepository = folderRepository;
    this.fileRepository = fileRepository;
  }

  @Override
  public Folder createFolder(FolderRequest request) {
    validateCreateRequest(request);
    Folder folder = new Folder();
    folder.setFolderId(UUID.randomUUID().toString());
    folder.setFolderName(request.getFolderName());
    folder.setParentId(request.getParentId());
    folder.setUserId(request.getUserId());

    Timestamp now = Timestamp.from(Instant.now());

    folder.setCreatedAt(now);
    folder.setModifiedAt(now);
    return folderRepository.save(folder);
  }

  @Override
  public Folder updateFolder(FolderRequest request, String folderId) {
    // Find the folder and check its owner.
    Folder folder = getFolderForUserOrThrow(folderId, request.getUserId());
    // Check whether the new parent folder belongs to the same user.
    validateParentFolder(request.getParentId(), request.getUserId());
    // Update the existing folder.
    folder.setFolderName(request.getFolderName());
    folder.setParentId(request.getParentId());
    folder.setModifiedAt(Timestamp.from(Instant.now()));
    return folderRepository.save(folder);
  }

  @Override
  public FolderContentsResponse getFolderContents(String folderId, UUID userId) {
    // Verify that the folder belongs to this user
    getFolderForUserOrThrow(folderId, userId);
    // Fetch all subfolders
    List<Folder> folders = folderRepository.findByParentId(folderId);
    // Fetch all files inside this folderd
    List<FileMetaData> files = fileRepository.findByFolderIdAndStatusNot(folderId, FileStatus.DELETED);
    return new FolderContentsResponse(folders, files);
  }

  @Override
  public Folder getFolderOrThrow(String folderId) {
    return folderRepository.findById(folderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));
  }

  @Override
  public Folder getFolderForUserOrThrow(String folderId, UUID userId) {
    Folder folder = getFolderOrThrow(folderId);
    if (!folder.getUserId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
    }

    return folder;
  }

  @Override
  public void deleteFolder(String folderId) {
    getFolderOrThrow(folderId);
    folderRepository.deleteById(folderId);
  }

  private void validateCreateRequest(FolderRequest request) {
    // First check whether the user exists.
    userService.getUserByIdOrThrow(request.getUserId());
    // Then check the parent folder, if one was provided.
    validateParentFolder(request.getParentId(), request.getUserId());
  }

  private void validateParentFolder(String parentId, UUID userId) {
    // No parent means this is a root-level folder.
    if (parentId == null) {
      return;
    }
    getFolderForUserOrThrow(parentId, userId);
  }
}
