package ibm.buildServer.clouds.softlayer;

import com.intellij.openapi.diagnostic.Logger;
import com.softlayer.api.service.virtual.Guest;
import java.util.*;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.log.Loggers;

public class SoftlayerUpdateInstancesTask implements Runnable {
  private SoftlayerCloudClient client;

  public SoftlayerUpdateInstancesTask(SoftlayerCloudClient client) {
    this.client = client;
  }

  public void run() {
    Logger LOG = Loggers.SERVER;
    InstanceStatus newStatus;
    InstanceStatus currentStatus;
    String vsiStatus;
    String vsiState;
    Guest guest;
    for(SoftlayerCloudImage image : client.getImages()) {
      for(SoftlayerCloudInstance instance : image.getInstances()) {
        currentStatus = instance.getStatus();
        try {
          vsiStatus = instance.guest.getStatus().getName();
          vsiState = instance.guest.getPowerState().getName();
          newStatus = teamcityStatus(vsiStatus, vsiState, currentStatus);
        // This catch block is only meant to catch "object not found" errors
        // returned by SoftLayer but at this time it's unkown if this exception
        // is available as a Java class.
        } catch(Exception e) {
          LOG.warn("Error: " + e);
          newStatus = InstanceStatus.ERROR;
        }
        instance.setStatus(newStatus);
        if(removable(instance.getStatus())) {
          image.removeInstance(instance.getInstanceId());
        }
      }
    }
  }

  public boolean removable(InstanceStatus status) {
    return status == InstanceStatus.ERROR || status == InstanceStatus.STOPPED;
  }

  private InstanceStatus teamcityStatus(
      String vsiStatus,
      String vsiState,
      InstanceStatus currentStatus) {
    if(currentStatus == InstanceStatus.ERROR_CANNOT_STOP) {
      return currentStatus;
    }
    if(vsiStatus.equals("Terminating")) {
      return InstanceStatus.STOPPING;
    }
    if(vsiStatus.equals("Disconnected")) {
      return InstanceStatus.STOPPED;
    }
    if(vsiState.equals("Halted")) {
      return InstanceStatus.STARTING;
    }
    if(vsiState.equals("Running")) {
      return InstanceStatus.RUNNING;
    }
    return currentStatus;
  }
}
