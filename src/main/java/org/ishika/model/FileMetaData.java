
package org.ishika.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import org.ishika.enums.FileStatus;
import org.springframework.data.domain.Persistable;

@Data
@Entity
@Table(name = "files")
public class FileMetaData implements Persistable<String> {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String fileId;

  @Transient
  private boolean isNew = true;

  @Override
  public String getId() {
    return fileId;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

  @PostLoad
  @PrePersist
  void markNotNew() {
    this.isNew = false;
  }

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "storage_key", nullable = false, unique = true)
  private String storageKey;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(name = "folder_id", nullable = false)
  private String folderId;

  @Column(name = "size_bytes", nullable = false)
  private long size;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at", nullable = false)
  private Timestamp updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private FileStatus status;

  @Column(name = "content_type", nullable = false, length = 255)
  private String contentType;
}
