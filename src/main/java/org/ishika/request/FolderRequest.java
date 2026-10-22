package org.ishika.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderRequest {

  private UUID userId;
  private String folderName;
  private String parentId;
}