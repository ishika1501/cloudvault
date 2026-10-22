package org.ishika.model;


import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "folder")
@Data

public class Folder {

  @GeneratedValue
  @Column(name = "folder_id")
  private String folderId;

  @Column(name = "folder_name")
  private String folderName;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "parent_id")
  private String parentId;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @Column(name = "created_at")
  private Timestamp modifiedAt;
}