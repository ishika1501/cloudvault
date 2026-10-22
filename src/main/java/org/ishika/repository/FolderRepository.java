package org.ishika.repository;

import java.util.List;
import java.util.UUID;
import org.ishika.model.Folder;
import org.ishika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, String> {
  List<Folder> findByParentFolderId(String folderId);
}
