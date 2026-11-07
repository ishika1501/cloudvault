package org.ishika.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.Data;

@Entity
@Table(name = "file_upload_sessions")
@Data
public class FileUploadSession {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(name = "file_id")
  private String fileId;

  @Column(name = "storage_upload_id")
  private String storageUploadId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private FileUploadSessionStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @Column(name = "updated_at")
  private Timestamp updatedAt;
}