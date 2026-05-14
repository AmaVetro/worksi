package cl.duoc.worksi.client;

import cl.duoc.worksi.config.WorksiAiProperties;
import cl.duoc.worksi.dto.ai.MatchApiRequest;
import cl.duoc.worksi.dto.ai.MatchApiResponse;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AiMatchClient {
  private final RestClient worksiAiRestClient;
  private final WorksiAiProperties props;

  public AiMatchClient(RestClient worksiAiRestClient, WorksiAiProperties props) {
    this.worksiAiRestClient = worksiAiRestClient;
    this.props = props;
  }

  public Optional<MatchApiResponse> match(String cvText, String jobText) {
    if (!props.isEnabled()) {
      return Optional.empty();
    }
    try {
      MatchApiResponse body =
          worksiAiRestClient
              .post()
              .uri("/match")
              .contentType(MediaType.APPLICATION_JSON)
              .body(new MatchApiRequest(cvText, jobText))
              .retrieve()
              .body(MatchApiResponse.class);
      if (body == null) {
        return Optional.empty();
      }
      return Optional.of(body);
    } catch (RestClientException ex) {
      return Optional.empty();
    }
  }
}
