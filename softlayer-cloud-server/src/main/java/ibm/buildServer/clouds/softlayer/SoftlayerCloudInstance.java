/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.softlayer;

import com.softlayer.api.ApiClient;
import com.softlayer.api.service.Location;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.block.device.template.Group;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.base.connector.CloudAsyncTaskExecutor;
import jetbrains.buildServer.serverSide.AgentDescription;
import jetbrains.buildServer.log.Loggers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.intellij.openapi.diagnostic.Logger;

public class SoftlayerCloudInstance implements CloudInstance
{
  private InstanceStatus myStatus;
  private ScheduledExecutorService executor;
  // id and name is set in the start() method.
  private String id;
  private String name;
  private String hostname;
  private SoftlayerCloudImage image; // Set when SoftlayerCloudImage calls setImage
  public Guest guest;
  private Date startedTime;
  private SoftlayerCloudImageDetails imageDetails;
  private CloudInstanceUserData userData;
  public ApiClient softlayerClient;
  private final static Logger LOG = Loggers.SERVER;
  int taskDelayTime = 60 * 1000;
  private CloudErrorInfo myCurrentError = null;
  private boolean metadataSet = false;
  //public AgentDescription myAgent;
  public boolean isConnected = false;

  public SoftlayerCloudInstance(SoftlayerCloudImageDetails details,
      CloudInstanceUserData data,
      ApiClient softlayerClient) {
    myStatus = InstanceStatus.UNKNOWN;
    executor = Executors.newSingleThreadScheduledExecutor();
    guest = new Guest();
    guest.setHostname(details.getAgentName());
    guest.setDomain(details.getDomainName());
    guest.setStartCpus(details.getMaxCores());
    guest.setMaxMemory(details.getMaxMemory());
    // Hardcode hourly billing for now. Once the UI allows the user to select
    // hourly or monthly billing we'll do something like details.getBilling()
    //guest.setHourlyBillingFlag(true);
    guest.setHourlyBillingFlag(details.getVsiBilling());
    // Hardcode agent using global identifier. Selecting agents is in the next
    // sprint.
    Group blockDevice = new Group();
    //blockDevice.setGlobalIdentifier("aaad7259-06ff-453b-bedc-e425661fa151");
    blockDevice.setGlobalIdentifier(details.getVsiTemplate());
    guest.setBlockDeviceTemplateGroup(blockDevice);
    //guest.setLocalDiskFlag(details.getLocalDiskFlag().contains("true"));
    guest.setLocalDiskFlag(details.getLocalDiskFlag());
    guest.setDatacenter(new Location());
    guest.getDatacenter().setName(details.getDatacenter());
    guest.setPostInstallScriptUri("http://169.60.13.41/test.sh");
    startedTime = new Date();
    imageDetails = details;
    userData = data;
    this.softlayerClient = softlayerClient;
  }

  public SoftlayerCloudImage getImage() {
    return image;
  }

  public void setImage(SoftlayerCloudImage image) {
    this.image = image;
  }

  public String getImageId() {
    return image.getId();
  }

  public String getInstanceId() {
    return id;
  }
  
  public String getImageName() {
	return image.getName();
  }

  public String getName() {
    return name;
  }

  public String getNetworkIdentity() {
    // Get IP address using SoftLayer API.
    try {
      return guest.getPrimaryIpAddress();
    } catch (Exception e) {
      return null;
    }
  }

  public Date getStartedTime()  {
    return startedTime;
  }

  public InstanceStatus getStatus() {
    return myStatus;
  }

  public void setStatus(InstanceStatus status) {
    myStatus = status;
  }

  public boolean containsAgent(AgentDescription agent) {
	//return true;
    if(name == null) {
      LOG.warn("SoftLayer instance name has not been set.");
      return false;
    }
    LOG.info("containsAgent: " + id + "," + agent.getConfigurationParameters().get("INSTANCE_NAME") + ", name: " +agent.getConfigurationParameters().get("name"));
    if (agent.getConfigurationParameters().get("INSTANCE_NAME").equals(name)) {
    	LOG.info("containsAgent: " + name);
    	return true;
    }
    return false;
    //return agent.getConfigurationParameters().containsKey("INSTANCE_NAME");
    //return agent.getConfigurationParameters().get("INSTANCE_NAME").equals(name);
  }

  public CloudErrorInfo getErrorInfo() {
	  return myCurrentError;
  }

  public void start() {
    if(softlayerClient != null) {
      // println statements are for printing to screen during test as logging has
      // not been implemented in automated unit tests.
      System.out.println(softlayerClient);
    }
    try {
      guest = Guest.service(softlayerClient).createObject(guest);
      id = guest.getId().toString();
      hostname = guest.getHostname().toString();
      if (hostname != null && id != null) {
        name = hostname + "_" + id; 
      }
      LOG.info("Softlayer Hostname " + hostname + " and ID is " + id);
      System.out.println("Softlayer Hostname " + hostname + " and ID is " + id);
      myStatus = InstanceStatus.SCHEDULED_TO_START;
      myCurrentError = null;
    } catch (Exception e) {
    	  // Any exception related to softlayer api or start of VSI will be caught here. 
      System.out.println("Error: " + e);
      LOG.warn("SoftlayerCloudInstance Error: " + e);
      myStatus = InstanceStatus.ERROR;
      // Catch exception as cloud error and throw error to softlayerCloudImage file.
      myCurrentError = new CloudErrorInfo("Failed to start cloud instance" + e);
      throw e;  
    }
  }

  public void terminate() {
	  myStatus = InstanceStatus.SCHEDULED_TO_STOP;
	  CloudAsyncTaskExecutor executor = new CloudAsyncTaskExecutor(
		        "Async tasks for terminating vsi");
	  SoftlayerTerminateInstanceTask task = new SoftlayerTerminateInstanceTask(this);
	  executor.submit("terminate vsi", new Runnable() {
	      public void run() {
	        try {
	          task.run();
	          executor.scheduleWithFixedDelay(
	              "Terminate instance",
	              task,
	              taskDelayTime,
	              taskDelayTime,
	              TimeUnit.MILLISECONDS);
	        } catch (Exception e) {
	            LOG.warn("SoftlayerCloudInstance Error: " + e);
	            // catch exception with stacktraces message. On TC server UI, this exception will show up on Agents->Cloud tab.
	            myCurrentError = new CloudErrorInfo("Failed to stop cloud instance with id: " + id , e.getMessage(), e);
	        } finally {
	          
	        }
	      }
	    });
  }

  public boolean metadataIsSet() {
    return metadataSet;
  }

  public void setMetadata() {
    try {
      // Serialize CloudInstanceUserData and set as SoftLayer user metadata.
      List<String> userDataList = new ArrayList<String>();
      userData.addAgentConfigurationParameter("name", name);
      userData.addAgentConfigurationParameter("IMAGE_NAME", getImageName());
      userDataList.add(userData.serialize());
      Long virtualGuestId = new Long(getInstanceId());
      Guest.Service virtualGuestService = Guest.service(softlayerClient,
          virtualGuestId);
      virtualGuestService.setUserMetadata(userDataList);
      metadataSet = true;
    } catch (Exception e) {
      String message = "Error trying to set metadata: " + e;
      LOG.warn(message);
      System.out.println(message);
      myCurrentError = new CloudErrorInfo(message);
      metadataSet = false;
    }
  }
}
