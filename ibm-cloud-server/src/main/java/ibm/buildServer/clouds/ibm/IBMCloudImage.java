/*
* @author: Scott Wyman Neagle, scottwn@ibm.com
* 
* File Description:
* Handles: Object for Cloud Image.
* Handles: Has functions for fetching ProfileId, SourceId, Instances, PoolId for cloud image.
**/

package ibm.buildServer.clouds.ibm;

import jetbrains.buildServer.clouds.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.softlayer.api.*;

//TODO: Is this import statement necessary? It's the same package.
import ibm.buildServer.clouds.ibm.IBMCloudImageDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a TeamCity cloud image.
 */
public class IBMCloudImage implements CloudImage {

  private IBMCloudImageDetails details;
  private final Map<String, IBMCloudInstance> instances = new ConcurrentHashMap<>();
  private CloudErrorInfo myCurrentError = null;
  public ApiClient ibmClient;

  /**
   * A string used to identify a persistent file where instances are identified.
   */
  public final static String TEAMCITY_INSTANCES = "teamcity_instances";

  //TODO: Where does details come from?
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

  /**
   * @return a collecton of instances of the image.
   */
  @NotNull
  public Collection<IBMCloudInstance> getInstances() {
    return Collections.unmodifiableCollection(instances.values());
  }

  /**
   * Remove the instance with instanceId from the hashmap.
   * @param instanceId an 8-digit numeric string representing a VSI ID.
   * @see <a href="https://softlayer.github.io/reference/datatypes/SoftLayer_Virtual_Guest/#id">Guest</a>
   */
  public void removeInstance(String instanceId) {
    instances.remove(instanceId);
  }

  /**
   * Add instance to the instances hashmap.
   */
  public void addInstance(IBMCloudInstance instance) {
    instances.putIfAbsent(instance.getInstanceId(), instance);
  }

  @Nullable
  public IBMCloudInstance findInstanceById(@NotNull final String instanceId) {
    return instances.get(instanceId);
  }

  /**
   * Get the agent pool id set for cloud image.
   */
  @Nullable
  @Override
  public Integer getAgentPoolId() {
    return details.getAgentPoolId();
  }

  @Nullable
  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  /**
   * Method will start new instance
   * @param data An object of CloudInstanceUserData provided by the TeamCity core.
   * @see #canStartNewInstance()
   * @see #createInstance(CloudInstanceUserData)
   */
  public IBMCloudInstance startNewInstance(@NotNull final CloudInstanceUserData data) {
    if (canStartNewInstance()) {
      return createInstance(data);
    }
    return null;
  }

  /**
   * Check whether TC server can start new instance by checking the max count set 
   * by user. maxInstances == 0 means user can start infinite instances.
   */
  protected boolean canStartNewInstance() {
    return getMaxInstances() == 0 || instances.size() < getMaxInstances();
  }

  /**
   * Create a new instance using CloudInstanceUserData. Called by #startNewInstance.
   * @see IBMCloudInstance#start()
   */
  protected IBMCloudInstance createInstance(CloudInstanceUserData data) {
    IBMCloudInstance instance = new IBMCloudInstance(details, data, ibmClient);
    try {
      instance.setImage(this);
      instance.start();
      instances.put(instance.getInstanceId(), instance);
      myCurrentError = null;
    } catch (Exception e) {
      
      myCurrentError = new CloudErrorInfo("Failed to start cloud image: ", e.getMessage(), e);
      throw e;
    }
    return instance;
  }

  /**
   * Set credentials for ibmClient.
   */
  public void setCredentials(String username, String apiKey) {
    ibmClient = new RestApiClient().withCredentials(username, apiKey);
  }
}
