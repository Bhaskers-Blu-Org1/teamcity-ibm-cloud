/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

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
    agentDescription = new FakeAgentDescription();
    instanceData = new CloudInstanceUserData(
        "fake-agent-name",
        System.getenv("SOFTLAYER_API"),
        "ibmwdtest.com",
        Long.MAX_VALUE,
        "fake-profile",
        "This is a fake cloud profile for unit testing.",
        agentDescription.getConfigurationParameters());
    client = new SoftlayerCloudClient(parameters);
    details = new SoftlayerCloudImageDetails(
        parameters.getCloudImages().iterator().next());
    image = new SoftlayerCloudImage(details);
    image.setCredentials(
        System.getenv("SOFTLAYER_USER"), System.getenv("SOFTLAYER_API"));
    client.addImage(image);
  }

  @Test
  @DisplayName("Test if we can add & retrieve images")
  public void addImageTest() {
    SoftlayerCloudClientFactory factory = new SoftlayerCloudClientFactory(
        new FakeCloudRegistrar(),
        new FakeCloudManager(),
        new FakePluginDescriptor());
    client = factory.createNewClient(new FakeCloudState(), parameters);
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
    String message;
    CloudInstance instance = client.startNewInstance(image, instanceData);
    Assertions.assertNotNull(instance, "instance is null");
    client.start();
    System.out.println("Started instance " + instance.getName());
    Assertions.assertNotNull(instance.getStatus(), "Status is null.");
    InstanceStatus status = instance.getStatus();
    System.out.println("instance status is " + status.getName());
    while(status != InstanceStatus.RUNNING) {
      status = instance.getStatus();
      message = "instance status is " + status.getName();
      Assertions.assertFalse(status.isError(), message);
    }
    System.out.println("Terminating instance " + instance.getName());
    client.terminateInstance(instance);
    status = instance.getStatus();
    while(
        status == InstanceStatus.SCHEDULED_TO_STOP
        || status == InstanceStatus.STOPPING) {
      status = instance.getStatus();
      message = "instance status is " + status.getName();
      Assertions.assertFalse(status.isError(), message);
    }
    image.removeInstance(instance.getInstanceId());
    int size = image.getInstances().size();
    String messages = "There are " + size + " instances, there should be 0.";
    Assertions.assertEquals(size, 0, messages);
    
  }

  @Test
  @DisplayName("Test findInstanceByAgent runs and returns null.")
  public void testFindInstanceByAgent() {
    Assertions.assertNull(client.findInstanceByAgent(agentDescription));
  }
}
