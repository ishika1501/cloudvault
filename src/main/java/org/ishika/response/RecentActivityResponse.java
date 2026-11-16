package org.ishika.response;

import java.sql.Timestamp;

public record RecentActivityResponse(
    String itemId,
    String itemType,
    String itemName,
    Timestamp lastOpenedAt
) {}