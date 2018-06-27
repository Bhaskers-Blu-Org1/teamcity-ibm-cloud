/*
* @author: Scott Wyman Neagle, scottwn@ibm.com
* 
* File description: 
*  Handles: cloud profile life cycle. 
*  Handles: start/terminate/restart/generate agent name for VSI.
*  Handles: Re-Connecting old agents to corresponding cloud image.
**/

package ibm.buildServer.clouds.ibm;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.base.connector.CloudAsyncTaskExecutor;
import jetbrains.buildServer.serverSide.AgentDescription;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.ApiClient;


/**
 * Represents a cloud profile in TeamCity server.
 */
public class IBMCloudClient implements CloudClientEx {
  private CloudAsyncTaskExecutor executor;
  private final Map<String, IBMCloudImage> images;
  private Logger LOG = Loggers.SERVER;
  private CloudErrorInfo myCurrentError = null;
  /**
   * Each profile has a task which runs once a minute to update the statuses of
   * the cloud instances.
   * @see IBMUpdateInstancesTask
   */
  private IBMUpdateInstancesTask updateInstancesTask;
  /**
   * ApiClient is a SoftLayer API class used to make calls.
   * @see <a href="https://github.com/softlayer/softlayer-java/blob/master/src/main/java/com/softlayer/api/ApiClient.java">ApiClient</a>
   */
  private ApiClient ibmClient;
  /**
   * Set to true when #start() is called.
   */
  boolean initialized = false;
  /**
   * Time in milliseconds.
   */
  int taskDelayTime = 60 * 1000;

  public IBMCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor(
        "Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, IBMCloudImage>();
    updateInstancesTask = new IBMUpdateInstancesTask(this);
  }

  /**
   * Called when parsing through all cloud profiles images from IBMCloudClientFactory
   * Only new Cloud images are added to HashMap.
   */
  public void addImage(IBMCloudImage image) {
    if (ibmClient == null) {
      ibmClient = image.ibmClient;
    }
    if (!images.containsKey(image.getName())) {
      images.put(image.getName(), image);
    } else {
      images.get(image.getName()).setDetails(image.getDetails());
    }   
  }

  public boolean isInitialized() {
    return initialized;
  }

  /**
   * @param imageID a String representation of a sequential integer, for example
   *                "0" or "1".
   * @return the IBMCloudImage associated with this ID.
   */
  public IBMCloudImage findImageById(String imageId) throws CloudException {
    return images.get(imageId);
  }

  /**
   * @return a Collection of IBMCloudImage objects.
   */
  public Collection<IBMCloudImage> getImages() throws CloudException {
    return images.values();
  }

  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  /**
   * Checks if the image can start new instance.
   */
  public boolean canStartNewInstance(@NotNull final CloudImage baseImage) {
    IBMCloudImage image = (IBMCloudImage) baseImage;
    return image.canStartNewInstance();
  }

  /**
   * Get agent name.
   * @param agentDescription An object that represents a build agent.
   * @return the value associated with the key "name" in the map returned by
   *         AgentDescription#getConfigurationParameters
   */
  public String generateAgentName(AgentDescription agentDescription) {
    return agentDescription.getConfigurationParameters().get("name");
  }

  /**
   * TC server will call this method to start instance on new images added to HashMap.
   * @param image the image that will be used to start the instance.
   * @param data an object provided by the TeamCity server containing necessary
   *             data for starting an instance.
   * @return the instance that was started.
   */
  public CloudInstance startNewInstance(CloudImage image,
      CloudInstanceUserData data) {
    CloudInstance cloudInstance = null;
    try {
      cloudInstance = ((IBMCloudImage) image).startNewInstance(data);
      myCurrentError = null;
    } catch(Exception e) {
      myCurrentError
        = new CloudErrorInfo("Failed to start cloud client ", e.getMessage(), e);
    }
    return cloudInstance;
  }

  /**
   * Called by TC server in order to link an agent to a running instance. The method
   * pull the instance name in the form hostname_instanceID from the agent and parses
   * the instance ID from that. It also takes the image name, which is a sequential
   * integer, from the agent. The linkage is done using both the instance ID and
   * the image ID.
   * @param agentDescription an object representing an agent.
   * @return the IBMCloudInstance object representing the cloud instance where
   *         this agent is running, or null.
   */
  @Nullable
  public IBMCloudInstance findInstanceByAgent(
      @NotNull final AgentDescription agentDescription) {
    final String instanceName
      = agentDescription.getConfigurationParameters().get("INSTANCE_NAME");
    if(instanceName == null) {
      return null;
    }
    IBMCloudImage image
      = images.get(agentDescription.getConfigurationParameters().get("IMAGE_NAME"));
    if (image != null) {
      String instanceID = instanceName.split("_")[1];
      return image.findInstanceById(instanceID);
    }
    return null;
  }

  /**
   * TC server will call this method to terminate running instances. 
   * @see jetbrains.buildServer.clouds.CloudClientEx#terminateInstance(jetbrains.buildServer.clouds.CloudInstance)
   */
  public void terminateInstance(@NotNull final CloudInstance baseInstance) {
    IBMCloudInstance instance = (IBMCloudInstance) baseInstance;
    updateInstancesTask.setClickedStop(instance.getInstanceId());
    instance.terminate(); 
  }

  /**
   * called on client object in IBMCloudClientFactory when new cloud profile is
   * created.
   * @see #updateInstancesTask
   * @see #initialized
   * */
  public void start() {
    executor.submit("Client start", new Runnable() {
      public void run() {
        try {
          updateInstancesTask.run();
          executor.scheduleWithFixedDelay("Update instances",
              updateInstancesTask, taskDelayTime, taskDelayTime,
              TimeUnit.MILLISECONDS);
        } finally {
          initialized = true;
        }
      }
    });
  }

  // Dispose the executor of updateInstanceTask.
  public void dispose() {
    executor.dispose();
  }

  @Override
  public void restartInstance(@NotNull final CloudInstance baseInstance) {
    LOG.warn("SoftLayer does not support restarting instances.");
    if(baseInstance.getStatus() == InstanceStatus.RUNNING) {
      LOG.warn(baseInstance.getName() + " is already running.");
    } else {
      terminateInstance(baseInstance);
    }
  }
  
  /**
   * Called by IBMCloudClientFactory when a cloud profile is updated.
   * @see #dispose()
   * @see #start()
   */
  public void restartUpdateInstancesTask(CloudClientParameters params) {
    dispose();
    executor = new CloudAsyncTaskExecutor(
        "Async tasks for cloud " + params.getProfileDescription());
    start();
  }

  /**
   * Called by #connectRunningInstances(). If the vsi does not have metadata, terminate
   * it; otherwise create an instance object and connect it to the image.
   * com.softlayer.api.service.virtual.Guest#getUserData() returns a list; the first
   * element in that list is the user data. It is a com.softlayer.api.service.virtual.guest.attribute.UserData object,
   * com.softlayer.api.service.virtual.guest.attribute.UserData#getValue() returns
   * a String.
   * @see IBMTerminateInstanceTask
   * @see IBMCloudImage#addInstance
   * @param vsi an object representing a VSI.
   * @param image the image used to start this VSI.
   */
  private void checkMetadata(Guest vsi, IBMCloudImage image) {
    
    if(vsi.getUserData() == null || vsi.getUserData().size() == 0) {
      // Terminate this instance because the metadata was never set.
      String name = vsi.getHostname() + "_" + vsi.getId().toString();
      executor = new CloudAsyncTaskExecutor(
          "Terminating orphan instance " + name);
      IBMTerminateInstanceTask task = new IBMTerminateInstanceTask(
          ibmClient, name, vsi);
      executor.submit("terminate orphan vsi", new Runnable() {
        public void run() {
          try {
            task.run();
            executor.scheduleWithFixedDelay("Terminate orphan instnace.", task,
                taskDelayTime, taskDelayTime, TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            LOG.warn("IBMCloudClient error: " + e);
          }
        }
      });
    } else { 
      String metadata = vsi.getUserData().get(0).getValue();
      CloudInstanceUserData data = CloudInstanceUserData.deserialize(metadata);
      String metadataImageName = data.getAgentConfigurationParameter("IMAGE_NAME");
      if(metadataImageName.equals(image.getName())) {
        IBMCloudInstance teamcityInstance = new IBMCloudInstance(
            image.getDetails(), data, ibmClient, vsi,
            vsi.getProvisionDate().getTime());
        teamcityInstance.setName();
        teamcityInstance.setImage(image);
        image.addInstance(teamcityInstance);
      }
    }
  }

  /**
   * Called by #retrieveRunningInstances(). Finds running VSIs that were started
   * using a certain IBMCloudImage object. Also checks a persistent file on the server
   * to make sure that the instance was started by this server.
   * @see #checkMetadata(IBMCloudInstance, IBMCloudImage)
   * @see IBMCloudImage#getDetails()
   * @param instances a java.util.List of com.softlayer.api.service.virtual.Guest
   * @param image the image for which we're searching for VSIs.
   */
  private void connectRunningInstances(List<Guest> instances,
      IBMCloudImage image) {
    File file = new File(image.TEAMCITY_INSTANCES);
    String teamcityInstances = "";
    if(file.exists()) {
      try {
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        teamcityInstances = reader.readLine();
        reader.close();
        fr.close();
      } catch (Exception e) {
        LOG.error("IBMCloudClient error: " + e);
      }
    }
    String agentName = image.getDetails().getAgentName();
    for(Guest instance : instances) {
      if(teamcityInstances.contains(instance.getId().toString())
          && instance.getHostname().equals(agentName)) {
        checkMetadata(instance, image);
      }
    }
  }

  /**
   * Called by IBMCloudClientFactory. Get a List of VSIs from the SoftLayer API and
   * reconnect the ones we recognize as started by this server.
   * @see #ibmClient
   * @see #connectRunningInstances(List<Guest>, IBMCloudImage)
   * @see <a href="https://softlayer.github.io/java/RetrieveMetadata.java/">RetrieveMetadata</a>
   */
  public void retrieveRunningInstances() {
    Account.Service accountService = Account.service(ibmClient);
    accountService.setMask("mask[userData]");
    try {
      List<Guest> instances = accountService.getVirtualGuests();
      for(IBMCloudImage image : getImages()) {
        connectRunningInstances(instances, image);
      }
    } catch (Exception e) {
      LOG.error("Unable to retrieve the SoftLayer metadata information. " + e);
    }
  }
}
