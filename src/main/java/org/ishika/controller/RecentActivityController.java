package org.ishika.controller;

import java.util.List;
import java.util.UUID;

import org.ishika.response.RecentActivityResponse;
import org.ishika.security.CurrentUserService;
import org.ishika.service.RecentActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recent")
public class RecentActivityController {

  private final RecentActivityService recentActivityService;
  private final CurrentUserService currentUserService;

  public RecentActivityController(RecentActivityService recentActivityService, CurrentUserService currentUserService) {

    this.recentActivityService = recentActivityService;
    this.currentUserService = currentUserService;
  }

  @GetMapping
  public ResponseEntity<List<RecentActivityResponse>> getRecentItems() {

    UUID userId = currentUserService.getCurrentUserId();

    return ResponseEntity.ok(recentActivityService.getRecentItems(userId));
  }
}