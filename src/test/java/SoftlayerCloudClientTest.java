import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

class SoftlayerCloudClientTest {
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
  @DisplayName("Test if we can add & retrieve images")
  public void addImageTest() {
    int size = client.getImages().size();
    message = "There are " + size + " images, there should be 1.";
    Assertions.assertEquals(size, 1, message);
  }

  @Test
  @DisplayName("Test findImageById")
  public void findImageByIdTest() {
    String sourceId = details.getSourceId();
    String message = "findImageById did not return the expected image.";
    Assertions.assertSame(client.findImageById(sourceId), image, message);
  }

  @Test
  @DisplayName("canStartNewInstance should return true because there are no running instances")
  public void testCanStartNewInstance() {
    Assertions.assertTrue(client.canStartNewInstance(image));
  }

  @Test
  @DisplayName("Test generateAgentName")
  public void testGenerateAgentName() {
    String name = client.generateAgentName(agentDescription);
    String message = "generateAgentName did not return the expected name.";
    Assertions.assertEquals(name, "fake-agent-name", message);
  }

  @Test
  @DisplayName("Test start and terminate.")
  public void testStartAndTerminate() {
    SoftlayerCloudClientFactory factory = new SoftlayerCloudClientFactory(
        new FakeCloudRegistrar(),
        new FakeCloudManager(),
        new FakePluginDescriptor());
    client = factory.createNewClient(new FakeCloudState(), parameters);
    SoftlayerCloudInstance instance =
      client.startNewInstance(image, instanceData);
    client.start();
    while(instance.getStatus() != InstanceStatus.RUNNING) {
      Assertions.assertFalse(instance.getStatus().isError());
    }
    client.terminateInstance(instance);
    while(
        instance.getStatus() == InstanceStatus.SCHEDULED_TO_STOP
        || instance.getStatus() == InstanceStatus.STOPPING) {
      Assertions.assertFalse(instance.getStatus().isError());
    }
    size = image.getInstances().size();
    message = "There are " + size + " instances, there should be 0.";
    Assertions.assertEquals(size, 0, message);
  }

  @Test
  @DisplayName("Test findInstanceByAgent runs and returns null.")
  public void testFindInstanceByAgent() {
    Assertions.assertNull(client.findInstanceByAgent(agentDescription));
  }
}
