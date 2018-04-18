package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.log.Loggers;
import com.intellij.openapi.diagnostic.Logger;

import java.util.*;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import jetbrains.buildServer.clouds.InstanceStatus;

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
    try {
      for(image : client.getImages()) {
        for(instanceId : image.getInstances()) {
          guest = image.findInstanceById(instanceId).guest;
          currentStatus = image.findInstanceById(instanceId).getStatus();
          vsiStatus = guest.asService(softlayerClient).getStatus().getName();
          vsiState = guest.asService(softlayerClient).getPowerState().getName();
          newStatus = teamcityStatus(vsiStatus, vsiState, currentStatus);
          if(newStatus != currentStatus) {
            image.findInstanceById(instanceId).setStatus(newStatus);
          }
          if(removable(image.findInstanceById(instanceId).getStatus())) {
            image.removeInstance(instanceId);
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Error: " + e);
    }
  }

  private InstanceStatus teamcityStatus(vsiStatus, vsiState, currentStatus) {
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
