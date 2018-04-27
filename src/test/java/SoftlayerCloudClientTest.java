import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudClientFactory;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

class SoftlayerCloudClientTest {
  private CloudClientParameters parameters;
  private SoftlayerCloudClient client;
  private SoftlayerCloudImage image;
  private SoftlayerCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;

  @BeforeEach
  public void assignClientWithImage() {
    parameters = new FakeParameters();
    //System.out.println("parameters: " + parameters.getProfileDescription());
    agentDescription = new FakeAgentDescription();
    //System.out.println("agentDescription: "
    //    + agentDescription.getConfigurationParameters().get("name"));
    instanceData = new CloudInstanceUserData(
        "fake-agent-name",
        System.getenv("SOFTLAYER_API"),
        "ibmwdtest.com",
        Long.MAX_VALUE,
        "fake-profile",
        "This is a fake cloud profile for unit testing.",
        agentDescription.getConfigurationParameters());
    //System.out.println("instanceData: " + instanceData.getProfileDescription());
    client = new SoftlayerCloudClient(parameters);
    //System.out.println("client: " + client.isInitialized());
    details = new SoftlayerCloudImageDetails(
        parameters.getCloudImages().iterator().next());
    //System.out.println("details: " + details.toString());
    image = new SoftlayerCloudImage(details);
    //System.out.println("image: " + image.getName());
    client.addImage(image);
    //System.out.println(client.getImages());
  }

  @Test
  @DisplayName("Test if we can add & retrieve images")
  public void addImageTest() {
    int size = client.getImages().size();
    String message = "There are " + size + " images, there should be 1.";
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
    CloudInstance instance = client.startNewInstance(image, instanceData);
    client.start();
    System.out.println("Started instance " + instance.getName());
    while(instance.getStatus() != InstanceStatus.RUNNING) {
      Assertions.assertFalse(instance.getStatus().isError());
    }
    System.out.println("Terminating instance " + instance.getName());
    client.terminateInstance(instance);
    while(
        instance.getStatus() == InstanceStatus.SCHEDULED_TO_STOP
        || instance.getStatus() == InstanceStatus.STOPPING) {
      Assertions.assertFalse(instance.getStatus().isError());
    }
    int size = image.getInstances().size();
    String message = "There are " + size + " instances, there should be 0.";
    Assertions.assertEquals(size, 0, message);
  }

  @Test
  @DisplayName("Test findInstanceByAgent runs and returns null.")
  public void testFindInstanceByAgent() {
    Assertions.assertNull(client.findInstanceByAgent(agentDescription));
  }

}
