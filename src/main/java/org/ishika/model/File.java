package org.ishika.model;

import java.sql.Timestamp;
import org.ishika.enums.FileStatus;

public class File {
  private String fileId;
  private String ownerId;
  private String folderId;
  private long size;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private FileStatus status;
}
