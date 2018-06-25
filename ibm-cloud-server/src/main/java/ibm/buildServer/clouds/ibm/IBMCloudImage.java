/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import jetbrains.buildServer.clouds.*;
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

public class IBMCloudImage implements CloudImage
{
  private IBMCloudImageDetails details;
  private final Map<String, IBMCloudInstance> instances = new ConcurrentHashMap<>();
  public ApiClient ibmClient;
  private CloudErrorInfo myCurrentError = null;

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

  @NotNull
  public Collection<IBMCloudInstance> getInstances() {
    return Collections.unmodifiableCollection(instances.values());
  }

  public void removeInstance(String instanceId) {
    instances.remove(instanceId);
  }

  public void addInstance(IBMCloudInstance instance) {
    instances.putIfAbsent(instance.getInstanceId(), instance);
  }

  @Nullable
  public IBMCloudInstance findInstanceById(
      @NotNull final String instanceId) {
    return instances.get(instanceId);
  }

  @Nullable
  @Override
  public Integer getAgentPoolId() {
    return details.getAgentPoolId();
  }

  @Nullable
  public CloudErrorInfo getErrorInfo() {
	  return myCurrentError;
  }

  @NotNull
  public IBMCloudInstance startNewInstance(@NotNull final CloudInstanceUserData data) {
    if(canStartNewInstance()) {
      return createInstance(data);
    }  
    return null;
  }

  protected boolean canStartNewInstance() {
	//maxInstances == 0 means infinite instances.
	return getMaxInstances() == 0 || instances.size() < getMaxInstances();
  }

  protected IBMCloudInstance createInstance(CloudInstanceUserData data) {
	IBMCloudInstance instance = new IBMCloudInstance(details, data, ibmClient);
	try
	{
	    instance.setImage(this);
	    instance.start();
	    instances.put(instance.getInstanceId(), instance);
	    myCurrentError = null;
	}
	catch(Exception e)
	{
		/*
		 *  Exception from IBMCloudInstance and generate its stacktraces. Exception thrown to file IBMCloudClient. 
		 *  On TC server UI, this exception will show up on Agents->Cloud tab.
		 * */
		
		myCurrentError = new CloudErrorInfo("Failed to start cloud image: ", e.getMessage(), e);
		throw e;
	}
    return instance;
  }

  public void setCredentials(String username, String apiKey) {
    ibmClient = new RestApiClient().withCredentials(username, apiKey);
  }
}
