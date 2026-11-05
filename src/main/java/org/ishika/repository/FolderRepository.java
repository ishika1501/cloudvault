package org.ishika.repository;

import java.util.List;
import org.ishika.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
  List<Folder> findByParentId(String folderId);
}
