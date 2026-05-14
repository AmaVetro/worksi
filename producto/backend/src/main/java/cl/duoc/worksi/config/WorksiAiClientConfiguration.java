package cl.duoc.worksi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(WorksiAiProperties.class)
public class WorksiAiClientConfiguration {

  @Bean
  public RestClient worksiAiRestClient(WorksiAiProperties props) {
    SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
    rf.setConnectTimeout(props.getConnectTimeoutMs());
    rf.setReadTimeout(props.getReadTimeoutMs());
    String base = props.getBaseUrl().trim();
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    return RestClient.builder().baseUrl(base).requestFactory(rf).build();
  }
}
