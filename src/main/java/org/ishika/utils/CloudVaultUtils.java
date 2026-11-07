package org.ishika.utils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CloudVaultUtils {

  public static String generateObjectKey(UUID ownerId, String fileId) {
    LocalDate today = LocalDate.now(ZoneOffset.UTC);
    return String.format("users/%s/%d/%02d/%s", ownerId, today.getYear(), today.getMonthValue(), fileId);
  }
}
