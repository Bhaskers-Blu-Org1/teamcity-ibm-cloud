package ibm.buildServer.clouds.softlayer;

import com.softlayer.api.service.virtual.Guest;

import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.CloudErrorInfo;
import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.serverSide.AgentDescription;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

public class SoftlayerCloudInstance implements CloudInstance
{
  private InstanceStatus myStatus;
  private ScheduledExecutorService executor;
  // id and name are set in the start() method.
  private String id;
  private String name;
  private SoftlayerCloudImage image; // Set when SoftlayerCloudImage calls setImage
  private Guest guest;
  private Date startedTime;
  private SoftlayerCloudImageDetails imageDetails;
  private CloudInstanceUserData userData;

  public SoftlayerCloudInstance(
      SoftlayerCloudImageDetails details,
      CloudInstanceUserData data) {
    myStatus = InstanceStatus.UNKNOWN;
    executor = Executors.newSingleThreadScheduledExecutor();
    String username = 
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
    return name;
  }

  public String getNetworkIdentity() {
    // Get IP address using SoftLayer API.
    return guest.getPrimaryIpAddress();
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
}
