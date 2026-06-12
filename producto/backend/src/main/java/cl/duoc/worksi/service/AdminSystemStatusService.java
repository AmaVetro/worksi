package cl.duoc.worksi.service;

import cl.duoc.worksi.config.WorksiAiProperties;
import cl.duoc.worksi.dto.admin.AdminSystemStatusResponse;
import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AdminSystemStatusService {
  private static final String UP = "UP";
  private static final String DOWN = "DOWN";
  private static final int HEALTH_TIMEOUT_MS = 3000;

  private final DataSource dataSource;
  private final WorksiAiProperties aiProperties;

  public AdminSystemStatusService(DataSource dataSource, WorksiAiProperties aiProperties) {
    this.dataSource = dataSource;
    this.aiProperties = aiProperties;
  }

  public AdminSystemStatusResponse systemStatus() {
    return new AdminSystemStatusResponse(UP, databaseStatus(), aiStatus());
  }

  private String databaseStatus() {
    try (Connection conn = dataSource.getConnection()) {
      if (conn.isValid(2)) {
        return UP;
      }
      return DOWN;
    } catch (Exception ex) {
      return DOWN;
    }
  }

  private String aiStatus() {
    if (!aiProperties.isEnabled()) {
      return DOWN;
    }
    String base = aiProperties.getBaseUrl().trim();
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
    rf.setConnectTimeout(HEALTH_TIMEOUT_MS);
    rf.setReadTimeout(HEALTH_TIMEOUT_MS);
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> body =
          RestClient.builder()
              .baseUrl(base)
              .requestFactory(rf)
              .build()
              .get()
              .uri("/health")
              .retrieve()
              .body(Map.class);
      if (body != null && UP.equalsIgnoreCase(String.valueOf(body.get("status")))) {
        return UP;
      }
      return DOWN;
    } catch (RestClientException ex) {
      return DOWN;
    }
  }
}
