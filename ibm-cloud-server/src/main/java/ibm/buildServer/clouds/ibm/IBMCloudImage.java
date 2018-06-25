/*
* @author: Scott Wyman Neagle, scottwn@ibm.com
* 
* File Description:
* Handles: Object for Cloud Image.
* Handles: Has functions for fetching ProfileId, SourceId, Instances, PoolId for cloud image.
**/

package ibm.buildServer.clouds.ibm;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.softlayer.api.*;

import ibm.buildServer.clouds.ibm.IBMCloudImageDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.HashMap;
import java.util.Map;

public class IBMCloudImage implements CloudImage {

  private IBMCloudImageDetails details;
  private final Map<String, IBMCloudInstance> instances = new ConcurrentHashMap<>();
  private CloudErrorInfo myCurrentError = null;
  private final static Logger LOG = Loggers.SERVER;
  public ApiClient ibmClient;
  public final static String TEAMCITY_INSTANCES = "teamcity_instances";

  public IBMCloudImage(IBMCloudImageDetails details) {
    this.details = details;
  }

  @NotNull
  public IBMCloudImageDetails getDetails() {
    return details;
  }

  public void setDetails(IBMCloudImageDetails details) {
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
  public long getMaxInstances() {
    return details.getMaxInstances();
  }

  // Return a collecton of instances of the image.
  @NotNull
  public Collection<IBMCloudInstance> getInstances() {
    return Collections.unmodifiableCollection(instances.values());
  }

  // Remove the instance with instanceId.
  public void removeInstance(String instanceId) {
    instances.remove(instanceId);
  }

  // Add instance to the instances hashmap.
  public void addInstance(IBMCloudInstance instance) {
    instances.putIfAbsent(instance.getInstanceId(), instance);
  }

  @Nullable
  public IBMCloudInstance findInstanceById(@NotNull final String instanceId) {
    return instances.get(instanceId);
  }

  // Get the agent pool id set for cloud image.
  @Nullable
  @Override
  public Integer getAgentPoolId() {
    return details.getAgentPoolId();
  }

  @Nullable
  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  // Method will start new instance
  @NotNull
  public IBMCloudInstance startNewInstance(@NotNull final CloudInstanceUserData data) {
    if (canStartNewInstance()) {
      return createInstance(data);
    }
    return null;
  }

  // Check whether TC server can start new instance by checking the max count set by user.
  protected boolean canStartNewInstance() {
    // maxInstances == 0 means user can start infinite instances.
    return getMaxInstances() == 0 || instances.size() < getMaxInstances();
  }

  // Create a new instance using CloudInstanceUserData.
  protected IBMCloudInstance createInstance(CloudInstanceUserData data) {
    IBMCloudInstance instance = new IBMCloudInstance(details, data, ibmClient);
    try {
      instance.setImage(this);
      instance.start();
      instances.put(instance.getInstanceId(), instance);
      myCurrentError = null;
    } catch (Exception e) {
      
      /* Exception from IBMCloudInstance, thrown to file IBMCloudClient. 
       * On TC server UI, this exception will show up on Agents->Cloud tab.
       */
      myCurrentError = new CloudErrorInfo("Failed to start cloud image: ", e.getMessage(), e);
      throw e;
    }
    return instance;
  }

  // Set credentials for ibmClient.
  public void setCredentials(String username, String apiKey) {
    ibmClient = new RestApiClient().withCredentials(username, apiKey);
  }
}
