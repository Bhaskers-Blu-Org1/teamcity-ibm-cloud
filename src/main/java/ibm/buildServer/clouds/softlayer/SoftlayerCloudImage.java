/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.softlayer;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.softlayer.api.*;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.HashMap;
import java.util.Map;

public class SoftlayerCloudImage implements CloudImage
{
  private SoftlayerCloudImageDetails details;
  private final Map<String, SoftlayerCloudInstance> instances =
    new ConcurrentHashMap<>();
  public ApiClient softlayerClient;
  private final static Logger LOG = Loggers.SERVER;

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
  public String getProfileId() {
    return details.getProfileId();
  }

  @NotNull
  public Collection<SoftlayerCloudInstance> getInstances() {
    return Collections.unmodifiableCollection(instances.values());
  }

  public void removeInstance(String instanceId) {
    instances.remove(instanceId);
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
    return null;
  }

  protected boolean canStartNewInstance() {
    //TODO: Implement max instances. Only start new instance if limit has not been
    //reached.
    return true;
  }

  protected SoftlayerCloudInstance createInstance(CloudInstanceUserData data) {
    SoftlayerCloudInstance instance
      = new SoftlayerCloudInstance(details, data, softlayerClient);
    instance.setImage(this);
    instance.start();
    instances.put(instance.getInstanceId(), instance);
    return instance;
  }

  public void setCredentials(String username, String apiKey) {
    softlayerClient = new RestApiClient().withCredentials(username, apiKey);
  }
}
