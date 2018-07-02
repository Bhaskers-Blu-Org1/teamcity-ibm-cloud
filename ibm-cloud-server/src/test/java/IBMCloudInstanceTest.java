/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.ibm.IBMCloudClient;
import ibm.buildServer.clouds.ibm.IBMCloudImage;
import ibm.buildServer.clouds.ibm.IBMCloudImageDetails;
import ibm.buildServer.clouds.ibm.IBMCloudInstance;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

import com.softlayer.api.*;

import java.util.Date;

class IBMCloudInstanceTest {
  private CloudClientParameters parameters;
  private IBMCloudClient client;
  private IBMCloudImage image;
  private IBMCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;
  private ApiClient ibmClient;
  private IBMCloudInstance instance;
  private Date startTests;

  @BeforeEach
  public void assignClientWithImage() {
    parameters = new FakeParameters();
    agentDescription = new FakeAgentDescription();
    instanceData = new CloudInstanceUserData(
        "fake-agent-name",
        System.getenv("IBM_CLOUD_API"),
        "ibmwdtest.com",
        Long.MAX_VALUE,
        "fake-profile",
        "This is a fake cloud profile for unit testing.",
        agentDescription.getConfigurationParameters());
    ibmClient = new RestApiClient().withCredentials(
        System.getenv("IBM_CLOUD_USER"),
        System.getenv("IBM_CLOUD_API"));
    startTests = new Date();
    client = new IBMCloudClient(parameters);
    details = new IBMCloudImageDetails(
        parameters.getCloudImages().iterator().next());
    image = new IBMCloudImage(details);
    instance = new IBMCloudInstance(details, instanceData, ibmClient);
  }

  @AfterEach
  public void terminateInstances() {
    instance.terminate();
    for(IBMCloudInstance instanceFromImage : image.getInstances()) {
      instanceFromImage.terminate();
    }
    client.terminateAllInstances();
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
    Assertions.assertFalse(startedTime.before(startTests));
    Assertions.assertFalse(startedTime.after(new Date()));
  }

  @Test
  @DisplayName("Expect containsAgent to return false because we're using the fake agent")
  public void testContainsAgent() {
    Assertions.assertFalse(instance.containsAgent(agentDescription));
  }
}
