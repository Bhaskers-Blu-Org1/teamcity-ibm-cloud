/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

import java.util.ArrayList;
import java.util.List;

class SoftlayerCloudImageTest {
  private CloudClientParameters parameters;
  private SoftlayerCloudClient client;
  private SoftlayerCloudImage image;
  private SoftlayerCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;
  private List<SoftlayerCloudInstance> instances;

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
    instances = new ArrayList<>();
  }
  
  @AfterEach
  public void terminateInstances() {
	  for (SoftlayerCloudInstance instance : instances) {
		  instance.terminate();
	  }
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
    for (int i = 0; i < 2; i++) {
    	SoftlayerCloudInstance instance = image.startNewInstance(instanceData);
        Assertions.assertNotNull(instance, message);
        instances.add(instance);
    }
  }
  
  @Test
  @DisplayName("Test setting max instances.")
  public void testSetMaxInstances() {
	String message = "Instance should not be null";
	details.setMaxInstances(1);
	SoftlayerCloudInstance instance = image.startNewInstance(instanceData);
    Assertions.assertNotNull(instance, message);
    instances.add(instance);
	instance = image.startNewInstance(instanceData);
    Assertions.assertNull(instance, "Max instance should be 1.");
    details.setMaxInstances(2);
    instance = image.startNewInstance(instanceData);
    Assertions.assertNotNull(instance, message);
    instances.add(instance);
    instance = image.startNewInstance(instanceData);
    Assertions.assertNull(instance, "Max instance should be 2.");
  }
}
