package cl.duoc.worksi.util;

import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.Workload;

public final class JobSemanticText {
  private JobSemanticText() {}

  public static String build(String title, String description, Modality modality, Workload workload) {
    String t = title == null ? "" : title.trim();
    String d = description == null ? "" : description.trim();
    return t + "\n\n" + d + "\n\n" + modality.name() + "\n\n" + workload.name();
  }
}
