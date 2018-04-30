package ibm.buildServer.clouds.softlayer;

import com.intellij.openapi.diagnostic.Logger;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.Status;
import com.softlayer.api.service.virtual.guest.power.State;
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
    Status vsiStatus;
    State vsiState;
    Guest.Service service;
    Guest guest;
    for(SoftlayerCloudImage image : client.getImages()) {
      for(SoftlayerCloudInstance instance : image.getInstances()) {
        currentStatus = instance.getStatus();
        try {
          // This logic is modeled on https://github.com/softlayer/softlayer-java/blob/master/examples/src/main/java/com/softlayer/api/example/OrderVirtualServer.java
          service = instance.guest.asService(instance.softlayerClient);
          service.withMask().status().name();
          service.withMask().powerState().name();
          guest = service.getObject();
          vsiStatus = guest.getStatus();
          vsiState = guest.getPowerState();
          newStatus = teamcityStatus(vsiStatus, vsiState, currentStatus);
        // This catch block is only meant to catch "object not found" errors
        // returned by SoftLayer but at this time it's unkown if this exception
        // is available as a Java class.
        } catch(Exception e) {
          System.out.println("Error: " + e);
          LOG.warn("Error: " + e);
          newStatus = InstanceStatus.ERROR;
        }
        System.out.println("New status is " + newStatus.getName());
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
      Status vsiStatus,
      State vsiState,
      InstanceStatus currentStatus) {
    if(vsiStatus == null) {
      System.out.println("vsiStatus is null");
    } else {
      System.out.println("vsiStatus is " + vsiStatus.getName());
    }
    if(vsiState == null) {
      System.out.println("vsiState is null");
    } else {
      System.out.println("vsiState is " + vsiState.getName());
    }
    if(currentStatus == InstanceStatus.ERROR_CANNOT_STOP) {
      return currentStatus;
    }
    if(vsiStatus != null && vsiStatus.getName().equals("Terminating")) {
      return InstanceStatus.STOPPING;
    }
    if(vsiStatus != null && vsiStatus.getName().equals("Disconnected")) {
      return InstanceStatus.STOPPED;
    }
    if(vsiState != null && vsiState.getName().equals("Halted")) {
      return InstanceStatus.STARTING;
    }
    if(vsiState != null && vsiState.getName().equals("Running")) {
      return InstanceStatus.RUNNING;
    }
    return currentStatus;
  }
}
