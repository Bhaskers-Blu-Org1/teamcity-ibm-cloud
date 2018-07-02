/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import ibm.buildServer.clouds.ibm.IBMCloudClient;
import ibm.buildServer.clouds.ibm.IBMCloudImage;
import ibm.buildServer.clouds.ibm.IBMCloudImageDetails;
import ibm.buildServer.clouds.ibm.IBMCloudInstance;
import ibm.buildServer.clouds.ibm.IBMCloudClientFactory;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.*;

import com.softlayer.api.ApiClient;
import com.softlayer.api.RestApiClient;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.Guest;

import com.google.gson.Gson;

import java.util.List;
import java.util.ArrayList;

class IBMCloudClientTest {
  private CloudClientParameters parameters;
  private IBMCloudClient client;
  private IBMCloudImage image;
  private IBMCloudImageDetails details;
  private AgentDescription agentDescription;
  private CloudInstanceUserData instanceData;

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
    client.addImage(image);
  }

  @AfterEach
  public void cancelVSIs() {
    client.terminateAllInstances();
  }

  @Test
  @DisplayName("Test if we can add & retrieve images")
  public void addImageTest() {
    IBMCloudClientFactory factory = new IBMCloudClientFactory(
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
    Assertions.assertTrue(((IBMCloudInstance) instance).metadataIsSet());
    Long vsiId;
    Long instanceId = new Long(instance.getInstanceId());
    System.out.println("Retrieving metadata for " + instanceId);
    List<Guest> metadata = retrieveMetadata();
    Gson gson = new Gson();
    for(Guest vsi : metadata) {
      vsiId = new Long(vsi.getId());
      if(vsiId.equals(instanceId)) {
        System.out.println("Found instance " + vsiId);
        // getUserData() returns a list; the first element in that list is the
        // user data. It is a UserData object, getValue() returns a string.
        CloudInstanceUserData data = CloudInstanceUserData.
          deserialize(vsi.getUserData().get(0).getValue());
        String fakeAgentName = instanceData.getAgentName();
        String agentName = data.getAgentName();
        message = "Agent name should be " + fakeAgentName + " but the server returned "
          + agentName;
        Assertions.assertEquals(fakeAgentName, agentName, message);
      }
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

  private List<Guest> retrieveMetadata() {
    ApiClient ibmClient = new RestApiClient().
      withCredentials(System.getenv("IBM_CLOUD_USER"), System.getenv("IBM_CLOUD_API"));
    Account.Service accountService = Account.service(ibmClient);
    accountService.setMask("mask[userData]");
    List<Guest> output = new ArrayList<Guest>();
    try {
      output = accountService.getVirtualGuests();
    } catch (Exception e) {
      System.out.println("Unable to retrieve metadata unformation. " + e.getMessage());
    }
    return output;
  }
}
