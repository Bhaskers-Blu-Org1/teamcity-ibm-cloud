/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.ApiClient;
import com.softlayer.api.service.Location;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.block.device.template.Group;
import com.softlayer.api.service.virtual.guest.network.Component;
import com.softlayer.api.service.virtual.guest.SupplementalCreateObjectOptions;

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
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import com.intellij.openapi.diagnostic.Logger;

public class IBMCloudInstance implements CloudInstance
{
  private InstanceStatus myStatus;
  private ScheduledExecutorService executor;
  // id, name, and hostname are set in the setName() method.
  private String id;
  private String name;
  private String hostname;
  private IBMCloudImage image; // Set when IBMCloudImage calls setImage
  public Guest guest;
  private Date startedTime;
  private IBMCloudImageDetails imageDetails;
  private CloudInstanceUserData userData;
  public ApiClient ibmClient;
  private final static Logger LOG = Loggers.SERVER;
  int taskDelayTime = 60 * 1000;
  private CloudErrorInfo myCurrentError = null;
  private boolean metadataSet = false;

  public IBMCloudInstance(IBMCloudImageDetails details,
      CloudInstanceUserData data,
      ApiClient ibmClient) {
    this(details, data, ibmClient, new Guest(), new Date());
    // Setting VsiTemplate.
    Group blockDevice = new Group();
    blockDevice.setGlobalIdentifier(details.getVsiTemplate());
    guest.setBlockDeviceTemplateGroup(blockDevice);
    
    // Setting Datacenter.
    guest.setDatacenter(new Location());
    guest.getDatacenter().setName(details.getDatacenter());

    Component networkComponent = new Component();
    networkComponent.setMaxSpeed(Long.valueOf(details.getNetwork()));
    guest.getNetworkComponents().add(networkComponent);
    
    // Setting Host name, Domain name & VsiBilling Type.
    guest.setHostname(details.getAgentName());
    guest.setDomain(details.getDomainName());
    guest.setHourlyBillingFlag(details.getVsiBilling());
    
    /* If CustomizeMachineType = true: Set RAM, CPU & Disk Type.
     * Else:  Set Flavor List.
     */
    if(details.getCustomizeMachineType()) {
     	guest.setStartCpus(details.getMaxCores());
	    guest.setMaxMemory(details.getMaxMemory());
	    guest.setLocalDiskFlag(details.getLocalDiskFlag());
    }
    else {
	    SupplementalCreateObjectOptions supplementObject = new SupplementalCreateObjectOptions(); 
	    supplementObject.setFlavorKeyName(details.getFlavorList());
	    guest.setSupplementalCreateObjectOptions(supplementObject);
    }

  }

  public IBMCloudInstance(IBMCloudImageDetails details,
      CloudInstanceUserData data,
      ApiClient ibmClient,
      Guest guest,
      Date dateTime) {
	  this.guest = guest;
	  startedTime = dateTime;
	  imageDetails = details;
	  userData = data;
	  this.ibmClient = ibmClient;
	  myStatus = InstanceStatus.UNKNOWN;
	  executor = Executors.newSingleThreadScheduledExecutor();
  }

  public void setName() {
    id = guest.getId().toString();
    hostname = guest.getHostname();
    if (hostname != null && id != null) {
      name = hostname + "_" + id; 
    }
  }

  public IBMCloudImage getImage() {
    return image;
  }

  public void setImage(IBMCloudImage image) {
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
    if(name == null) {
      LOG.warn("SoftLayer instance name has not been set.");
      return false;
    }
    return agent.getConfigurationParameters().get("INSTANCE_NAME").equals(name);
  }

  public CloudErrorInfo getErrorInfo() {
	  return myCurrentError;
  }

  public void start() {
    if(ibmClient != null) {
      // println statements are for printing to screen during test as logging has
      // not been implemented in automated unit tests.
      System.out.println(ibmClient);
    }
    try {
      guest = Guest.service(ibmClient).createObject(guest);
      setName();
      LOG.info("Softlayer Hostname " + hostname + " and ID is " + id);
      System.out.println("Softlayer Hostname " + hostname + " and ID is " + id);
      myStatus = InstanceStatus.SCHEDULED_TO_START;
      myCurrentError = null;
      writeInstanceId();
    } catch (Exception e) {
    	  // Any exception related to softlayer api or start of VSI will be caught here. 
      System.out.println("Error: " + e);
      LOG.warn("IBMCloudInstance Error: " + e);
      myStatus = InstanceStatus.ERROR;
      // Catch exception as cloud error and throw error to ibmCloudImage file.
      myCurrentError = new CloudErrorInfo("Failed to start cloud instance" + e);
      throw e;  
    }
  }

  private void writeInstanceId() {
    try {
      File file = new File(image.TEAMCITY_INSTANCES);
      file.createNewFile();
      FileWriter fw = new FileWriter(file, true);
      PrintWriter writer = new PrintWriter(fw);
      writer.write(getName());
      writer.close();
      fw.close();
    } catch (IOException e) {
      LOG.error("IBMCloudInstance error: " + e);
    }
  }

  public void terminate() {
	  myStatus = InstanceStatus.SCHEDULED_TO_STOP;
	  CloudAsyncTaskExecutor executor = new CloudAsyncTaskExecutor(
		        "Async tasks for terminating vsi");
	  IBMTerminateInstanceTask task = new IBMTerminateInstanceTask(this);
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
	            LOG.warn("IBMCloudInstance Error: " + e);
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
      Guest.Service virtualGuestService = Guest.service(ibmClient,
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
