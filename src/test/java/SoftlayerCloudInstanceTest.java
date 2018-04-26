import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

import com.softlayer.api.*;

import java.util.Date;

class SoftlayerCloudInstanceTest {
  private CloudClientParameters parameters;
  private SoftlayerCloudClient client;
  private SoftlayerCloudImage image;
  private SoftlayerCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;
  private ApiClient softlayerClient;
  private SoftlayerCloudInstance instance;
  private Date startTests;

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
    softlayerClient = new RestApiClient().withCredentials(
        System.getenv("SOFTLAYER_USER"),
        System.getenv("SOFTLAYER_API"));
    startTests = new Date();
    SoftlayerCloudClient client = new SoftlayerCloudClient(parameters);
    details = new SoftlayerCloudImageDetails(
        parameters.getCloudImages().iterator().next());
    image = new SoftlayerCloudImage(details);
    instance = new SoftlayerCloudInstance(details, instanceData, softlayerClient);
  }

  @Test
  @DisplayName("Test getting and setting an image in an instance.")
  public void testGetImage() {
    instance.setImage(image);
    String message = "getImage returned the wrong image.";
    Assertions.assertSame(instance.getImage(), image, message);
    message = "getImageId returned the wrong ID.";
    Assertions.assertEquals(instance.getImageId(), image.getId(), message);
  }

  @Test
  @DisplayName("Test getInstanceId")
  public void testGetInstanceId() {
    Assertions.assertNull(instance.getInstanceId());
  }

  @Test
  @DisplayName("Test getName")
  public void testGetName() {
    Assertions.assertNull(instance.getName());
  }

  @Test
  @DisplayName("Test getStartedTime returns the time at which the instance was created.")
  public void testGetStartedTime() {
    Date startedTime = instance.getStartedTime();
    Assertions.assertTrue(startedTime.after(startTests));
    Assertions.assertTrue(startedTime.before(new Date()));
  }

  @Test
  @DisplayName("Expect containsAgent to return false because we're using the fake agent")
  public void testContainsAgent() {
    String message = "Agent name was "
      + agentDescription.getConfigurationParameters().get("name");
    Assertions.assertFalse(instance.containsAgent(agentDescription));
  }
}
