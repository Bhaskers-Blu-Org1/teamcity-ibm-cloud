/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.ApiClient;
import com.softlayer.api.service.Location;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.block.device.Template;
import com.softlayer.api.service.virtual.guest.block.device.template.Group;
import com.softlayer.api.service.virtual.guest.network.Component;
import com.softlayer.api.service.virtual.guest.SupplementalCreateObjectOptions;
import com.softlayer.api.service.virtual.disk.Image;

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


public class IBMCloudInstance implements CloudInstance {
  private InstanceStatus myStatus;
  private ScheduledExecutorService executor;
  /**
   * Set in #setName()
   */
  private String id;
  /**
   * Set in #setName()
   */
  private String name;
  /**
   * Set in #setName()
   */
  private String hostname;
  /**
   * Set in IBMCloudImage#setImage()
   */
  private IBMCloudImage image;
  public Guest guest;
  private Date startedTime;
  private IBMCloudImageDetails imageDetails;
  private CloudInstanceUserData userData;
  public ApiClient ibmClient;
  private final static Logger LOG = Loggers.SERVER;
  /**
   * Time in milliseconds.
   */
  int taskDelayTime = 60 * 1000;
  private CloudErrorInfo myCurrentError = null;
  private boolean metadataSet = false;

  /**
   * Used to create a new TeamCity instance and a new VSI. If CustomizeMachineType = true:
   * Set RAM, CPU, Disk Type & Disk Size. Else:  Set Flavor List.
   */
  public IBMCloudInstance(IBMCloudImageDetails details,
      CloudInstanceUserData data, ApiClient ibmClient) {
    this(details, data, ibmClient, new Guest(), new Date());
    Group blockDevice = new Group();
    blockDevice.setGlobalIdentifier(details.getVsiTemplate());
    guest.setBlockDeviceTemplateGroup(blockDevice);
    guest.setDatacenter(new Location());
    guest.getDatacenter().setName(details.getDatacenter());
    Component networkComponent = new Component();
    networkComponent.setMaxSpeed(Long.valueOf(details.getNetwork()));
    guest.getNetworkComponents().add(networkComponent);
    guest.setHostname(details.getAgentName());
    guest.setDomain(details.getDomainName());
    guest.setHourlyBillingFlag(details.getVsiBilling());
    if(details.getCustomizeMachineType()) {
      guest.setStartCpus(details.getMaxCores());
      guest.setMaxMemory(details.getMaxMemory());
      guest.setLocalDiskFlag(details.getLocalDiskFlag());
      Template template = new Template();
      Image diskImage = new Image();
      diskImage.setCapacity(Long.valueOf(details.getDiskSize()));
      template.setDiskImage(diskImage);
      blockDevice.getBlockDevices().add(template);
    } else {
      SupplementalCreateObjectOptions supplementObject = new SupplementalCreateObjectOptions(); 
      supplementObject.setFlavorKeyName(details.getFlavorList());
      guest.setSupplementalCreateObjectOptions(supplementObject);
    }
  }

  /**
   * Used to create a new TeamCity instance and connect it to an existing VSI.
   */
  public IBMCloudInstance(IBMCloudImageDetails details, CloudInstanceUserData data,
      ApiClient ibmClient, Guest guest, Date dateTime) {
    this.guest = guest;
    startedTime = dateTime;
    imageDetails = details;
    userData = data;
    this.ibmClient = ibmClient;
    myStatus = InstanceStatus.UNKNOWN;
    executor = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Instance name is set in the format of hostname_Id.
   */
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

  /**
   * Get IP address using SoftLayer API.
   */
  public String getNetworkIdentity() {
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

  /**
   * called by TC server. It is responsible for finding the linkage to show hyperlink
   * on instance name after it is connected to agent. Should return false if it's
   * passed a null value.
   */
  public boolean containsAgent(AgentDescription agent) {
    if(name == null || agent == null 
        || agent.getConfigurationParameters().get("ibm.instance.name") == null) {
      return false;
    }
    String instanceNameFromAgent
      = agent.getConfigurationParameters().get("ibm.instance.name");
    return instanceNameFromAgent.equals(name);
  }

  public CloudErrorInfo getErrorInfo() {
    return myCurrentError;
  }

  /**
   * called by IBMCloudImage. Start a vsi.
   */
  public void start() {
    try {
      guest = Guest.service(ibmClient).createObject(guest);
      setName();
      LOG.info("Softlayer Hostname " + hostname + " and ID is " + id);
      System.out.println("Softlayer Hostname " + hostname + " and ID is " + id);
      myStatus = InstanceStatus.SCHEDULED_TO_START;
      myCurrentError = null;
      writeInstanceId();
    } catch (Exception e) { 
      System.out.println("Error: " + e);
      LOG.warn("IBMCloudInstance Error: " + e);
      myStatus = InstanceStatus.ERROR;
      myCurrentError = new CloudErrorInfo("Failed to start cloud instance" + e);
      throw e;  
    }
  }

  /**
   * called in #start(). Write instance Id into a file.
   */
  private void writeInstanceId() {
    try {
      File file = new File(image.TEAMCITY_INSTANCES);
      file.createNewFile();
      FileWriter fw = new FileWriter(file, true);
      PrintWriter writer = new PrintWriter(fw);
      //instanceInfo has information of profileId, imageId and vsiId: "IBMSL-10 0 51234567"
      String instanceInfo = userData.getProfileId() + " " + getImageName() + " " + id;
      writer.write(instanceInfo);
      writer.close();
      fw.close();
    } catch (IOException e) {
      LOG.error("IBMCloudInstance error: " + e);
    }
  }

  /**
   * called by IBMCloudClient. It will create a new thread for IBMTerminateInstaceTask
   * to make sure instance is terminated.
   */
  public void terminate() {
    myStatus = InstanceStatus.SCHEDULED_TO_STOP;
    CloudAsyncTaskExecutor executor
      = new CloudAsyncTaskExecutor("Async tasks for terminating vsi");
    IBMTerminateInstanceTask task = new IBMTerminateInstanceTask(this);
    executor.submit("terminate vsi", new Runnable() {
      public void run() {
        try {
          task.run();
          executor.scheduleWithFixedDelay("Terminate instance", task,
              taskDelayTime, taskDelayTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
          LOG.warn("IBMCloudInstance Error: " + e);
          myCurrentError = new CloudErrorInfo(
              "Failed to stop cloud instance with id: " + id , e.getMessage(),
              e);
        }
      }
    });
  }

  /**
   * Check if metadata of the instance is set.
   */
  public boolean metadataIsSet() {
    return metadataSet;
  }

  /**
   * called by IBMUpdateInstancesTask when instance is RUNNING.
   * Serialize CloudInstanceUserData, and set as SoftLayer user metadata.
   * Add two configuration parameters: instance name and image name.
   */
  public void setMetadata() {
    try {
      List<String> userDataList = new ArrayList<String>();
      userData.addAgentConfigurationParameter("ibm.instance.name", name);
      userData.addAgentConfigurationParameter("ibm.image.name", getImageName());
      userDataList.add(userData.serialize());
      Long virtualGuestId = new Long(getInstanceId());
      Guest.Service virtualGuestService = Guest.service(ibmClient, virtualGuestId);
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
