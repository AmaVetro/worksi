package cl.duoc.worksi.integration;

import java.nio.file.Files;
import java.nio.file.Path;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;

public final class IntegrationTestSupport {

  private static final Path AI_CONTEXT =
      Path.of("..", "ai-service").toAbsolutePath().normalize();

  private static GenericContainer<?> aiContainer;

  private IntegrationTestSupport() {}

  public static boolean isEnabled() {
    if (!DockerClientFactory.instance().isDockerAvailable()) {
      return false;
    }
    String external = System.getenv("WORKSI_IT_AI_URL");
    if (external != null && !external.isBlank()) {
      return true;
    }
    return Files.isDirectory(AI_CONTEXT);
  }

  public static Path aiServiceContext() {
    return AI_CONTEXT;
  }

  public static void startAiContainerIfNeeded() {
    String external = System.getenv("WORKSI_IT_AI_URL");
    if (external != null && !external.isBlank()) {
      return;
    }
    if (aiContainer != null && aiContainer.isRunning()) {
      return;
    }
    aiContainer =
        new GenericContainer<>(
                new org.testcontainers.images.builder.ImageFromDockerfile("worksi-ai-it", false)
                    .withFileFromPath(".", AI_CONTEXT))
            .withExposedPorts(8000)
            .waitingFor(
                org.testcontainers.containers.wait.strategy.Wait.forHttp("/health")
                    .forPort(8000)
                    .forStatusCodeMatching(status -> status == 200))
            .withStartupTimeout(java.time.Duration.ofMinutes(25));
    aiContainer.start();
  }

  public static void stopAiContainerIfStarted() {
    if (aiContainer != null) {
      aiContainer.stop();
      aiContainer = null;
    }
  }

  public static String resolveAiBaseUrl() {
    String external = System.getenv("WORKSI_IT_AI_URL");
    if (external != null && !external.isBlank()) {
      return external.replaceAll("/$", "");
    }
    if (aiContainer == null || !aiContainer.isRunning()) {
      throw new IllegalStateException("Contenedor IA de integracion no iniciado");
    }
    return "http://" + aiContainer.getHost() + ":" + aiContainer.getMappedPort(8000);
  }
}
