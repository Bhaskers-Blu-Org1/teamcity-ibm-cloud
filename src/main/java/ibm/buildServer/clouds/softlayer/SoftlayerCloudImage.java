package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.CloudImage;
import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.CloudErrorInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.io.File;

public class SoftlayerCloudImage implements CloudImage
{
  SoftlayerCloudImageDetails details;
  private final Map<String, SoftlayerCloudInstance> instances =
    new ConcurrentHashMap<>();
  
  public SoftlayerCloudImage(SoftlayerCloudImageDetails details) {
      this.details = details;
  }

  @NotNull
  public String getId() {
    return details.getSourceId();
  }

  @NotNull
  public String getName() {
    return details.getSourceId();
  }

  @NotNull
  public Collection<? extends CloudInstance> getInstances() {
    return instances;
  }

  @Nullable
  public SoftlayerCloudInstance findInstanceById(
      @NotNull final String instanceId) {
        return instances.get(instanceId);
  }

  @Nullable
  @Override
  public Integer getAgentPoolId() {
    //TODO: Implement agent pools on the frontend making agentPoolId a field in
    //SoftlayerCloudImageDetails.
    return null;
  }

  @Nullable
  public CloudErrorInfo getErrorInfo() {
    return null;
  }

  @NotNull
  public SoftlayerCloudInstance startNewInstance(
      @NotNull final CloudInstanceUserData data) {
    if(canStartNewInstance()) {
      return createInstance(data);
    }  
    LOG.info(
        "Can not start new instance becuause " 
        + details.getProfileID()
        + " has reached the maximum number of running instances.");
    return null;
  }

  private boolean canStartNewInstance() {
    //TODO: Implement max instances. Only start new instance if limit has not been
    //reached.
    return true;
  }

  protected SoftlayerCloudInstance createInstance(CloudInstanceUserData data) {
    instance = new SoftlayerCloudInstance(details, data);
    instance.setImage(this);
    instance.start();
    instances.put(instance.getInstanceId(), instance);
    return instance;
  }
}
