package ibm.buildServer.clouds.softlayer;

import com.softlayer.api.ApiClient;
import com.softlayer.api.service.Location;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.block.device.template.Group;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.AgentDescription;
import jetbrains.buildServer.log.Loggers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

import com.intellij.openapi.diagnostic.Logger;

public class SoftlayerCloudInstance implements CloudInstance
{
  private InstanceStatus myStatus;
  private ScheduledExecutorService executor;
  // id is set in the start() method.
  private String id;
  private SoftlayerCloudImage image; // Set when SoftlayerCloudImage calls setImage
  public Guest guest;
  private Date startedTime;
  private SoftlayerCloudImageDetails imageDetails;
  private CloudInstanceUserData userData;
  private ApiClient softlayerClient;
  private final static Logger LOG = Loggers.SERVER;

  public SoftlayerCloudInstance(
      SoftlayerCloudImageDetails details,
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
    guest.setHourlyBillingFlag(true);
    // Hardcode agent using global identifier. Selecting agents is in the next
    // sprint.
    Group blockDevice = new Group();
    blockDevice.setGlobalIdentifier("aaad7259-06ff-453b-bedc-e425661fa151");
    guest.setBlockDeviceTemplateGroup(blockDevice);
    guest.setLocalDiskFlag(details.getLocalDiskFlag().contains("true"));
    guest.setDatacenter(new Location());
    guest.getDatacenter().setName(details.getDatacenter());
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

  public String getName() {
    return id;
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
    String address = getNetworkIdentity();
    return agent.getConfigurationParameters().get("name").contains(address);
  }

  public CloudErrorInfo getErrorInfo() {
    return null;
  }

  public void start() {
    try {
      this.guest = Guest.service(softlayerClient).createObject(this.guest);
      id = guest.getId().toString();
      LOG.info("Softlayer ID is " + id);
      myStatus = InstanceStatus.SCHEDULED_TO_START;
    } catch (Exception e) {
      LOG.warn("Error: " + e);
      myStatus = InstanceStatus.ERROR;
    }
  }

  public void terminate() {
    LOG.info("Cancelling SoftLayer VSI " + getName());
    try {
      guest.asService(softlayerClient).deleteObject();
      myStatus = InstanceStatus.SCHEDULED_TO_STOP;
    } catch (Exception e) {
      LOG.warn("Error: " + e);
      myStatus = InstanceStatus.ERROR_CANNOT_STOP;
    }
  }
}
