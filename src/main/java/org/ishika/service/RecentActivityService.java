package org.ishika.service;

import java.util.List;
import java.util.UUID;

import org.ishika.response.RecentActivityResponse;

public interface RecentActivityService {
  void recordFolderOpen(String folderId, UUID userId);
  void recordFileOpen(String fileId, UUID userId);
  List<RecentActivityResponse> getRecentItems(UUID userId);
}