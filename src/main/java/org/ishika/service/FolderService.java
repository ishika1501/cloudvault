
package org.ishika.service;
import java.util.UUID;
import org.ishika.model.Folder;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;

public interface FolderService {
  Folder createFolder(FolderRequest folderRequest);
  Folder updateFolder(FolderRequest folderRequest, String folderId);
  FolderContentsResponse getFolderContents(String folderId, UUID userId);
  Folder getFolderOrThrow(String folderId);
  Folder getFolderForUserOrThrow(String folderId, UUID userId);
  void deleteFolder(String folderId, UUID userId);
  Folder getRootFolder(UUID userId);
}
