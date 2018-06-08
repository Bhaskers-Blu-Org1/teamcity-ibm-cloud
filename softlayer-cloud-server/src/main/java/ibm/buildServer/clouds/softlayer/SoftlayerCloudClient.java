/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.softlayer;

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

import com.softlayer.api.service.Account;

public class SoftlayerCloudClient implements CloudClientEx {
  boolean initialized = false;
  private CloudAsyncTaskExecutor executor;
  int taskDelayTime = 60 * 1000; // Time in milliseconds.
  private final Map<String, SoftlayerCloudImage> images;
  private Logger LOG = Loggers.SERVER;
  private CloudErrorInfo myCurrentError = null;
  private SoftlayerUpdateInstancesTask updateInstancesTask;

  public SoftlayerCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor("Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, SoftlayerCloudImage>();
    updateInstancesTask = new SoftlayerUpdateInstancesTask(this);
  }

  public void addImage(SoftlayerCloudImage image) {
	  if (!images.containsKey(image.getName())) {
		  images.put(image.getName(), image);
	  } else {
		  images.get(image.getName()).setDetails(image.getDetails());
	  }   
  }
    
  public boolean isInitialized() {
    return initialized;
  }

  public SoftlayerCloudImage findImageById(String imageId)
      throws CloudException {
    return images.get(imageId);
  }

  public Collection<SoftlayerCloudImage> getImages() throws CloudException {
    return images.values();
  }

  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  public boolean canStartNewInstance(@NotNull final CloudImage baseImage) {
    SoftlayerCloudImage image = (SoftlayerCloudImage) baseImage;
    return image.canStartNewInstance();
  }

  public String generateAgentName(AgentDescription agentDescription) {
    return agentDescription.getConfigurationParameters().get("name");
  }

  public CloudInstance startNewInstance(CloudImage image, CloudInstanceUserData data) {
	  
	  CloudInstance cloudInstance = null;
	  try
	  {
		  cloudInstance = ((SoftlayerCloudImage) image).startNewInstance(data);
		  myCurrentError = null;
	  }
	  catch(Exception e)
	  {
		  /*
		   * Catch exception from SoftlayerCloudImage and generate its stacktraces. 
		   * On TC server UI, this exception will show up on Cloud Profile tab.
		   * */
		  myCurrentError = new CloudErrorInfo("Failed to start cloud client ", e.getMessage(), e);
	  }
	  return cloudInstance;
  }

  @Nullable
  public SoftlayerCloudInstance findInstanceByAgent(@NotNull final AgentDescription agentDescription) {
    final String instanceName = agentDescription.getConfigurationParameters()
    		.get("INSTANCE_NAME");
    
    if(instanceName == null) {
      return null;
    }
    SoftlayerCloudImage image = images.get(agentDescription.getConfigurationParameters().get("IMAGE_NAME"));
    if (image != null) {
    	//Instance name is set in the format of hostname_instanceID.
        String instanceID = instanceName.split("_")[1];
        return image.findInstanceById(instanceID);
    }
    return null;
  }

  public void terminateInstance(@NotNull final CloudInstance baseInstance) {
    SoftlayerCloudInstance instance = (SoftlayerCloudInstance) baseInstance;
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

  public void connectRunningInstances() {
    for(SoftlayerCloudImage image : images) {
      // Retrieve instance metadata from SoftLayer API.
      Account.Service accountService = Account.service(image.softlayerClient);
      accountService.setMask("mask[userData]");
      try {
        //Gson gson = new Gson();
        LOG.info(accountService.getVirtualGuests());
      } catch (Exception e) {
        LOG.error("Unable to retrieve the SoftLayer metadata information. "
            + e.getMessage());
      }
      // Connect instance if metadata matches this image.
    }
  }
}
