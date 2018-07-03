/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.ibm.IBMCloudClient;
import ibm.buildServer.clouds.ibm.IBMCloudImage;
import ibm.buildServer.clouds.ibm.IBMCloudImageDetails;
import ibm.buildServer.clouds.ibm.IBMCloudInstance;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

import java.util.ArrayList;
import java.util.List;

class IBMCloudImageTest {
  private CloudClientParameters parameters;
  private IBMCloudClient client;
  private IBMCloudImage image;
  private IBMCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;
  private List<IBMCloudInstance> instances;

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
    client = new IBMCloudClient(parameters);
    details = new IBMCloudImageDetails(
        parameters.getCloudImages().iterator().next());
    image = new IBMCloudImage(details);
    image.setCredentials(
        System.getenv("IBM_CLOUD_USER"), System.getenv("IBM_CLOUD_API"));
    instances = new ArrayList<>();
  }
  
  @Test
  @DisplayName("Test getId")
  public void testGetId() {
    String message = "Source ID does not match.";
    Assertions.assertEquals(image.getId(), details.getSourceId(), message);
  }
  
  @Test
  @DisplayName("Test empty max instance equals 0.")
  public void testEmptyMaxInstanceEqualsZero() {
    String message = "Max instance does not match.";
    Assertions.assertEquals(image.getMaxInstances(), 0, message);
  }
  
  @Test
  @DisplayName("Test 0 max instance means you can add infinity.")
  public void testEmptyMaxInstancesMeansInfinity() {
	String message = "Instance should not be null";
    FakeCloudInstance instance;
    for (int i = 0; i < 2; i++) {
      instance = new FakeCloudInstance(details, instanceData, image.ibmClient);
      instance.setId(String.valueOf(i));
      image.addInstance(instance)
    }
    Assertions.assertTrue(image.canStartNewInstance());
  }
  
  @Test
  @DisplayName("Test setting max instances.")
  public void testSetMaxInstances() {
    details.setMaxInstances(1);
    Assertions.assertTrue(image.canStartNewInstance());
    FakeCloudInstance instance = new FakeCloudInstance(details, instanceData,
        image.ibmClient);
    instance.setId("1");
    image.addInstance(instance);
    instance = new FakeCloudInstance(details, instanceData, image.ibmClient);
    instance.setId("2");
    Assertions.assertFalse(image.canStartNewInstance());
    details.setMaxInstances(2);
    Assertions.assertTrue(image.canStartNewInstance());
    image.addInstance(instance);
    Assertions.assertFalse(image.canStartNewInstance());
  }
}
