package org.ishika.repository;

import java.util.List;
import java.util.UUID;
import org.ishika.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
  List<Folder> findByParentIdAndUserId(String parentId, UUID userId);
}
