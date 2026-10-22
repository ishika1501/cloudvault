package org.ishika.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ishika.model.Folder;
import org.ishika.repository.FolderRepository;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {


  private final UserService userService;
  private final FolderRepository folderRepository;

  @Override
  public Folder createFolder(FolderRequest folderRequest) {
    validateRequest(folderRequest);
    Folder folder = prepareFolder(folderRequest);
    folderRepository.save(folder);
    return folder;
  }

  @Override
  public Folder updateFolder(FolderRequest folderRequest, String folderId) {
    validateUpdateRequest(folderRequest, folderId);
    Folder folder = prepareFolder(folderRequest);
    folderRepository.save(folder);
    return folder;
  }

  @Override
  public FolderContentsResponse getFolderContents(String folderId) {
    Folder folder = getFolderOrThrow(folderId);
    List<Folder> folders = folderRepository
        .findByParentFolderId(folderId);
    return new FolderContentsResponse(folders, Collections.emptyList());
  }

  @Override
  public void deleteFolder(String id) {
    Folder folder = getFolderOrThrow(id);
    folderRepository.deleteById(id);
  }

  private void validateUpdateRequest(FolderRequest folderRequest, String folderId) {
    Folder folder = getFolderOrThrow(folderId);
    if (!folder.getParentId().equals(folderRequest.getParentId())) {
      getFolderOrThrow(folderRequest.getParentId());
    }
  }

  private Folder prepareFolder(FolderRequest folderRequest) {
    Folder folder = new Folder();
    folder.setFolderName(folderRequest.getFolderName());
    folder.setUserId(folderRequest.getUserId());
    folder.setCreatedAt(Timestamp.from(Instant.now()));
    folder.setCreatedAt(Timestamp.from(Instant.now()));
    return folder;
  }

  private void validateRequest(FolderRequest folderRequest) {
    userService.getUserByIdOrThrow(folderRequest.getUserId());
    getFolderOrThrow(folderRequest.getParentId());
  }

  private Folder getFolderOrThrow(String parentId) {
    Optional<Folder> folder = folderRepository.findById(parentId);
    if (folder.isEmpty()) {
      throw new RuntimeException("Folder doesn't exist");
    }
    return folder.orElse(null);
  }

}