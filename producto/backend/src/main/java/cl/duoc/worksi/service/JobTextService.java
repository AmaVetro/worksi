package cl.duoc.worksi.service;

import cl.duoc.worksi.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobTextService {

  public String buildJobText(Job job) {
    String title = job.getTitle() == null ? "" : job.getTitle().trim();
    String desc = job.getDescription() == null ? "" : job.getDescription().trim();
    String mod = job.getModality() == null ? "" : job.getModality().name();
    String load = job.getWorkload() == null ? "" : job.getWorkload().name();
    return title + "\n" + desc + "\n" + mod + "\n" + load;
  }
}
