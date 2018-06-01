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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

public class SoftlayerCloudClient implements CloudClientEx {
  boolean initialized = false;
  private CloudAsyncTaskExecutor executor;
  int taskDelayTime = 60 * 1000; // Time in milliseconds.
  private final Map<String, SoftlayerCloudImage> images;
  private Logger LOG = Loggers.SERVER;
  private CloudErrorInfo myCurrentError = null;
  private SoftlayerUpdateInstancesTask updateInstancesTask;
  private final Map<String, AgentDescription> agents;

  public SoftlayerCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor("Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, SoftlayerCloudImage>();
    agents = new HashMap<>();
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
  
  public boolean containsAgent(String agentName) {
	return agents.containsKey(agentName);
  }
  
  public AgentDescription getAgent(String agentName) {
	return agents.get(agentName);
  }

  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  public boolean canStartNewInstance(@NotNull final CloudImage baseImage) {
    SoftlayerCloudImage image = (SoftlayerCloudImage) baseImage;
    return image.canStartNewInstance();
  }

  public String generateAgentName(AgentDescription agentDescription) {
	  LOG.info("find 1 agentdes in generateagentname");
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
	  LOG.info("find 1 agentdes in findinstancebyagentname");
	  final String instanceName = agentDescription.getConfigurationParameters()
    		.get("INSTANCE_NAME");
    
    if(instanceName == null) {
      return null;
    }
    agents.put(instanceName, agentDescription);
    LOG.info("agent name in client:" + instanceName + ",agent map size:" + agents.size());
  //  for(SoftlayerCloudImage image : images.values()) {
    SoftlayerCloudImage image = images.get(agentDescription.getConfigurationParameters().get("IMAGE_NAME"));
    if (image == null) {
    	return null;
    }
    final SoftlayerCloudInstance instance = image.findInstanceById(instanceName);
      if(instance != null) {
    	  LOG.info("find instance in findinstancebyagentname");
        return instance;
      }
  //  }
    LOG.info("return null in findinstancebyagentname");
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
}
