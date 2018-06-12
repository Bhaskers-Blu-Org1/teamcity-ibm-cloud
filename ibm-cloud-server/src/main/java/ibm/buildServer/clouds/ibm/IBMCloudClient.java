/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
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

public class IBMCloudClient implements CloudClientEx {
  boolean initialized = false;
  private CloudAsyncTaskExecutor executor;
  int taskDelayTime = 60 * 1000; // Time in milliseconds.
  private final Map<String, IBMCloudImage> images;
  private Logger LOG = Loggers.SERVER;
  private CloudErrorInfo myCurrentError = null;
  private IBMUpdateInstancesTask updateInstancesTask;

  public IBMCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor("Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, IBMCloudImage>();
    updateInstancesTask = new IBMUpdateInstancesTask(this);
  }

  public void addImage(IBMCloudImage image) {
	  if (!images.containsKey(image.getName())) {
		  images.put(image.getName(), image);
	  } else {
		  images.get(image.getName()).setDetails(image.getDetails());
	  }   
  }
    
  public boolean isInitialized() {
    return initialized;
  }

  public IBMCloudImage findImageById(String imageId)
      throws CloudException {
    return images.get(imageId);
  }

  public Collection<IBMCloudImage> getImages() throws CloudException {
    return images.values();
  }

  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  public boolean canStartNewInstance(@NotNull final CloudImage baseImage) {
    IBMCloudImage image = (IBMCloudImage) baseImage;
    return image.canStartNewInstance();
  }

  public String generateAgentName(AgentDescription agentDescription) {
    return agentDescription.getConfigurationParameters().get("name");
  }

  public CloudInstance startNewInstance(CloudImage image, CloudInstanceUserData data) {
	  
	  CloudInstance cloudInstance = null;
	  try
	  {
		  cloudInstance = ((IBMCloudImage) image).startNewInstance(data);
		  myCurrentError = null;
	  }
	  catch(Exception e)
	  {
		  /*
		   * Catch exception from IBMCloudImage and generate its stacktraces. 
		   * On TC server UI, this exception will show up on Cloud Profile tab.
		   * */
		  myCurrentError = new CloudErrorInfo("Failed to start cloud client ", e.getMessage(), e);
	  }
	  return cloudInstance;
  }

  @Nullable
  public IBMCloudInstance findInstanceByAgent(@NotNull final AgentDescription agentDescription) {
    final String instanceName = agentDescription.getConfigurationParameters()
    		.get("INSTANCE_NAME");
    
    if(instanceName == null) {
      return null;
    }
    IBMCloudImage image = images.get(agentDescription.getConfigurationParameters().get("IMAGE_NAME"));
    if (image != null) {
    	//Instance name is set in the format of hostname_instanceID.
        String instanceID = instanceName.split("_")[1];
        return image.findInstanceById(instanceID);
    }
    return null;
  }

  public void terminateInstance(@NotNull final CloudInstance baseInstance) {
    IBMCloudInstance instance = (IBMCloudInstance) baseInstance;
    updateInstancesTask.setClickedStop(instance.getInstanceId());
    instance.terminate(); 
  }

  public void start() {
    executor.submit("Client start", new Runnable() {
      public void run() {
        try {
          updateInstancesTask.run();
          executor.scheduleWithFixedDelay("Update instances", updateInstancesTask, taskDelayTime, 
        		  taskDelayTime, 
        		  TimeUnit.MILLISECONDS);
        } finally {
          initialized = true;
        }
      }
    });
  }

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
  
  public void restartUpdateInstancesTask(CloudClientParameters params) {
    dispose();
    executor = new CloudAsyncTaskExecutor("Async tasks for cloud " + params.getProfileDescription());
    start();
  }

  private void checkMetadata(Guest vsi, IBMCloudImage image) {
    if(vsi.getUserData() == null) {
      // Terminate this instance because the metadata was never set.
      String name = vsi.getHostname().toString() + "_" + vsi.getId().toString();
      executor 
        = new CloudAsyncTaskExecutor("Terminating orphan instance " + name);
      IBMTerminateInstanceTask task = new IBMTerminateInstanceTask(
          image.ibmClient,
          name,
          vsi);
      executor.submit("terminate orphan vsi", new Runnable() {
        public void run() {
          try {
            task.run();
            executor.scheduleWithFixedDelay(
                "Terminate orphan instnace.",
                task,
                taskDelayTime,
                taskDelayTime,
                TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            LOG.warn("IBMCloudClient error: " + e);
          }
        }
      });
    } else { 
      // Create an instance object and connect it to this image.
      // getUserData() returns a list; the first element in that list is the
      // user data. It is a UserData object, getValue() returns a string.
      String metadata = vsi.getUserData().get(0).getValue();
      LOG.info("Metadata: " + metadata);
      CloudInstanceUserData data = CloudInstanceUserData.deserialize(metadata);
      String metadataImageName = data.getAgentConfigurationParameter("IMAGE_NAME");
      LOG.info("Checking metadata image name " + metadataImageName
          + " against TeamCity image name " + image.getName());
      if(metadataImageName.equals(image.getName())) {
        LOG.info("They match.");
        IBMCloudInstance teamcityInstance = new IBMCloudInstance(image.getDetails(), data,
            image.ibmClient, vsi, vsi.getProvisionDate().getTime());
        teamcityInstance.setName();
        teamcityInstance.setImage(image);
        image.addInstance(teamcityInstance);
      }
    }
  }


  private void connectRunningInstances(List<Guest> instances, IBMCloudImage image) {
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
    LOG.info("Trying to find SoftLayer instances that match " + agentName);
    for(Guest instance : instances) {
      if(teamcityInstances.contains(instance.getId().toString())
          && instance.getHostname().contains(agentName)) {
        LOG.info(instance.getHostname() + " matches " + agentName
            + " for VSI ID " + instance.getId());
        checkMetadata(instance, image);
      }
    }
  }

  public void retrieveRunningInstances() {
    for(IBMCloudImage image : getImages()) {
      // Retrieve instance metadata from SoftLayer API.
      Account.Service accountService = Account.service(image.ibmClient);
      accountService.setMask("mask[userData]");
      try {
        List<Guest> instances = accountService.getVirtualGuests();
        // Connect instance if metadata matches this image.
        connectRunningInstances(instances, image);
      } catch (Exception e) {
        LOG.error("Unable to retrieve the SoftLayer metadata information. " + e);
      }
    }
  }
}
