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

public class SoftlayerCloudClient implements CloudClientEx {
  boolean initialized = false;
  private final CloudAsyncTaskExecutor executor;
  int taskDelayTime = 60 * 1000; // Time in milliseconds.
  private final Map<String, SoftlayerCloudImage> images;
  private Logger LOG = Loggers.SERVER;
  private CloudErrorInfo myCurrentError = null;

  public SoftlayerCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor("Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, SoftlayerCloudImage>();
  }

  public void addImage(SoftlayerCloudImage image) {
	  if (!images.containsKey(image.getName())) {
		  images.put(image.getName(), image);
		  LOG.info("new image: " + image.getName());
	  } else {
		  images.get(image.getName()).setDetails(image.getDetails());
		  LOG.info("same image: " + image.getName());
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
    for(SoftlayerCloudImage image : images.values()) {
      final SoftlayerCloudInstance instance = image.findInstanceById(instanceName);
      if(instance != null) {
        return instance;
      }
    }
    return null;
  }

  public void terminateInstance(@NotNull final CloudInstance baseInstance) {
    SoftlayerCloudInstance instance = (SoftlayerCloudInstance) baseInstance;
    instance.terminate(); 
  }

  public void start() {
    SoftlayerUpdateInstancesTask updateInstancesTask = new SoftlayerUpdateInstancesTask(this);
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
}
