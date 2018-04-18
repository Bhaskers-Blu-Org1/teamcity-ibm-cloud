package ibm.buildServer.clouds.softlayer;

import com.softlayer.exception.ObjectNotFound;

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
    for(image : client.getImages()) {
      for(instanceId : image.getInstances()) {
        guest = image.findInstanceById(instanceId).guest;
        currentStatus = image.findInstanceById(instanceId).getStatus();
        try {
          vsiStatus = guest.asService(softlayerClient).getStatus().getName();
          vsiState = guest.asService(softlayerClient).getPowerState().getName();
          newStatus = teamcityStatus(vsiStatus, vsiState, currentStatus);
        } catch(ObjectNotFound e) {
          LOG.warn("Error: " + e);
          newStatus = IntanceStatus.ERROR;
        }
        if(newStatus != currentStatus) {
          image.findInstanceById(instanceId).setStatus(newStatus);
        }
        if(removable(image.findInstanceById(instanceId).getStatus())) {
          image.removeInstance(instanceId);
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
