package org.ishika.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(
    name = "recent_activity",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_recent_user_item",
        columnNames = {"user_id", "item_id", "item_type"}
    )
)
@Data
public class RecentActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "item_id", nullable = false)
  private String itemId;

  @Column(name = "item_type", nullable = false, length = 20)
  private String itemType;

  @Column(name = "last_opened_at", nullable = false)
  private Timestamp lastOpenedAt;
}