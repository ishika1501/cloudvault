package org.ishika.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.ishika.model.RecentActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentActivityRepository extends JpaRepository<RecentActivity, UUID> {
  Optional<RecentActivity> findByUserIdAndItemIdAndItemType(UUID userId, String itemId, String itemType);
  List<RecentActivity> findByUserIdOrderByLastOpenedAtDesc(UUID userId);
}