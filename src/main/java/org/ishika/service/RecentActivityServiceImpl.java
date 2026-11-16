package org.ishika.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ishika.enums.FileStatus;
import org.ishika.model.FileMetaData;
import org.ishika.model.Folder;
import org.ishika.model.RecentActivity;
import org.ishika.repository.FileRepository;
import org.ishika.repository.FolderRepository;
import org.ishika.repository.RecentActivityRepository;
import org.ishika.response.RecentActivityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecentActivityServiceImpl implements RecentActivityService {

  private final RecentActivityRepository recentRepository;
  private final FolderRepository folderRepository;
  private final FileRepository fileRepository;

  public RecentActivityServiceImpl(RecentActivityRepository recentRepository, FolderRepository folderRepository,
      FileRepository fileRepository) {

    this.recentRepository = recentRepository;
    this.folderRepository = folderRepository;
    this.fileRepository = fileRepository;
  }

  @Override
  @Transactional
  public void recordFolderOpen(String folderId, UUID userId) {

    Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found"));

    if (!folder.getUserId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
    }

    saveActivity(userId, folderId, "FOLDER");
  }

  @Override
  @Transactional
  public void recordFileOpen(String fileId, UUID userId) {

    FileMetaData file = fileRepository.findById(fileId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

    if (!file.getOwnerId().equals(userId) || file.getStatus() == FileStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
    }

    saveActivity(userId, fileId, "FILE");
  }

  private void saveActivity(UUID userId, String itemId, String itemType) {

    RecentActivity activity = recentRepository.findByUserIdAndItemIdAndItemType(userId, itemId, itemType)
        .orElseGet(RecentActivity::new);

    activity.setUserId(userId);
    activity.setItemId(itemId);
    activity.setItemType(itemType);
    activity.setLastOpenedAt(new Timestamp(System.currentTimeMillis()));

    recentRepository.save(activity);
  }

  @Override
  @Transactional
  public List<RecentActivityResponse> getRecentItems(UUID userId) {

    List<RecentActivityResponse> result = new ArrayList<>();

    List<RecentActivity> activities = recentRepository.findByUserIdOrderByLastOpenedAtDesc(userId);

    for (RecentActivity activity : activities) {

      if ("FOLDER".equals(activity.getItemType())) {

        Folder folder = folderRepository.findById(activity.getItemId()).orElse(null);

        if (folder == null || !folder.getUserId().equals(userId)) {
          recentRepository.delete(activity);
          continue;
        }

        result.add(new RecentActivityResponse(folder.getFolderId(), "FOLDER", folder.getFolderName(),
            activity.getLastOpenedAt()));

      } else if ("FILE".equals(activity.getItemType())) {

        FileMetaData file = fileRepository.findById(activity.getItemId()).orElse(null);

        if (file == null || !file.getOwnerId().equals(userId) || file.getStatus() == FileStatus.DELETED) {
          recentRepository.delete(activity);
          continue;
        }

        result.add(
            new RecentActivityResponse(file.getFileId(), "FILE", file.getFileName(), activity.getLastOpenedAt()));
      }
    }

    return result;
  }
}