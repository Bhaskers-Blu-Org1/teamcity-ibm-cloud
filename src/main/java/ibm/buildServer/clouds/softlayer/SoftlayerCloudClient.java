package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.base.connector.CloudAsyncTaskExecutor;
import jetbrains.buildServer.serverSide.AgentDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;

public class SoftlayerCloudClient implements CloudClientEx {
  boolean initialized = false;
  private final CloudAsyncTaskExecutor executor;
  int taskDelayTime = 60 * 1000; // Time in milliseconds.
  private final Map<String, SoftlayerCloudImage> images;

  public SoftlayerCloudClient(CloudClientParameters params) {
    executor = new CloudAsyncTaskExecutor(
        "Async tasks for cloud " + params.getProfileDescription());
    images = new HashMap<String, SoftlayerCloudImage>();
  }
    
  public boolean isInitialized() {
    return initialized;
  }

  public SoftlayerCloudImage findImageById(String imageId)
      throws CloudException {
    return images.get(imageId);
  }

  public Collection<? extends CloudImage> getImages() throws CloudException {
    return images;
  }

  public CloudErrorInfo getErrorInfo() {
    return null;
  }

  public boolean canStartNewInstance(CloudImage image) {
    return image.canStartNewInstance();
  }

  public String generateAgentName(AgentDescription agentDescription) {
    return agentDescription.getDefinedParameters().get("name");
  }

  public CloudInstance startNewInstance(CloudImage image,
      CloudInstanceUserData data) {
    return ((SoftlayerCloudImage) image).startNewInstance(data);
  }

  @Nullable
  public SoftlayerCloudInstance findInstanceByAgent(
      @NotNull final AgentDescription agentDescription) {
    final String instanceName = agent.getConfigurationParameters()
      .get("softlayer.instance.name");
    if(instanceName == null) {
      return null;
    }
    for(SoftlayerCloudImage image : images.values()) {
      final SoftlayerCloudInstance instance
        = image.findInstanceById(instanceName);
      if(instance != null) {
        return instance;
      }
    }
    return null
  }

  public void terminateInstance(@NotNull final CloudInstance instance) {
    instance.terminate(); 
  }

  public void start() {
    SoftlayerUpdateInstancesTask updateInstancesTask
      = new SoftlayerUpdateInstancesTask(this);
    executor.submit("Client start", new Runnable() {
      public void run() {
        try {
          updateInstancesTask.run();
          executor.scheduleWithFixedDelay(
              "Update instances",
              updateInstancesTask,
              taskDelayTime,
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

  public void restartInstance(SoftlayerCloudInstance instance) {
    LOG.warn("SoftLayer does not support restarting instances.");
    if(instance.status = InstanceStatus.RUNNING) {
      LOG.warn(instance.getName() + " is already running.");
    } else {
      terminateInstance(instance);
    }
  }
}
