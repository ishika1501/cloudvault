
package org.ishika.service;

import static org.ishika.utils.Constant.HOME_DIRECTORY;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.ishika.model.Folder;
import org.ishika.repository.FolderRepository;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl implements FolderService {

  private final UserService userService;
  private final FolderRepository folderRepository;

  public FolderServiceImpl(@Lazy UserService userService, FolderRepository folderRepository) {
    this.userService = userService;
    this.folderRepository = folderRepository;
  }

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
    getFolderOrThrow(folderId);
    List<Folder> folders = folderRepository.findByParentId(folderId);
    return new FolderContentsResponse(folders, Collections.emptyList());
  }

  @Override
  public Folder getFolderOrThrow(String parentId) {
    Optional<Folder> folder = folderRepository.findById(parentId);
    if (folder.isEmpty()) {
      throw new RuntimeException("Folder doesn't exist");
    }
    return folder.get();
  }

  @Override
  public void deleteFolder(String id) {
    getFolderOrThrow(id);
    folderRepository.deleteById(id);
  }

  private void validateUpdateRequest(FolderRequest folderRequest,
      String folderId) {
    Folder folder = getFolderOrThrow(folderId);
    if (!folder.getParentId().equals(folderRequest.getParentId())) {
      getFolderOrThrow(folderRequest.getParentId());
    }
  }

  private Folder prepareFolder(FolderRequest folderRequest) {
    Folder folder = new Folder();
    folder.setFolderId(UUID.randomUUID().toString());
    folder.setFolderName(folderRequest.getFolderName());
    folder.setParentId(folderRequest.getParentId());
    folder.setUserId(folderRequest.getUserId());
    folder.setCreatedAt(Timestamp.from(Instant.now()));
    folder.setModifiedAt(Timestamp.from(Instant.now()));
    return folder;
  }

  private void validateRequest(FolderRequest folderRequest) {
    userService.getUserByIdOrThrow(folderRequest.getUserId());

    if (!parentFolderValidationRequired(folderRequest.getFolderName())) {
      return;
    }

    getFolderOrThrow(folderRequest.getParentId());
  }

  private boolean parentFolderValidationRequired(final String folderName) {
    return !HOME_DIRECTORY.equals(folderName);
  }
}
