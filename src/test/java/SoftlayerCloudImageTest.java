import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;

import jetbrains.buildServer.clouds.*;

class SoftlayerCloudImageTest {
  private CloudClientParameters parameters;
  private SoftlayerCloudClient client;
  private SoftlayerCloudImage image;
  private SoftlayerCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;

  @BeforeAll
  public void setUp() {
    parameters = new FakeParameters();
    agentDescription = new FakeAgentDescription();
    instanceData = new CloudInstanceUserData(
        "fake-agent-name",
        System.getenv("SOFTLAYER_API"),
        "ibmwdtest.com",
        Long.MAX_VALUE,
        "fake-profile",
        "This is a fake cloud profile for unit testing.",
        agentDescription.getConfigurationParameters());
  }

  @BeforeEach
  public void assignClientWithImage() {
    SoftlayerCloudClient client = new SoftlayerCloudClient(parameters);
    details = new SoftlayerCloudImageDetails(client.getCloudImages().get(0));
    image = new SoftlayerCloudImage(details);
    client.addImage(image);
  }

  @Test
  @DisplayName("Test getId")
  public void testGetId() {
    String message = "Source ID does not match.";
    Assertions.assertEquals(image.getId(), details.getSourceId(), message);
  }
}
