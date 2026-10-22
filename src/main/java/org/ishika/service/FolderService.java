package org.ishika.service;

import org.ishika.model.Folder;
import org.ishika.request.FolderRequest;
import org.ishika.response.FolderContentsResponse;

public interface FolderService {
  Folder createFolder(FolderRequest folderRequest);
  Folder updateFolder(FolderRequest folderRequest,String folderId);
  FolderContentsResponse getFolderContents(String folderId);
  void deleteFolder(String id);

}