/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.log.Loggers;

import com.softlayer.api.service.provisioning.version1.Transaction;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.Status;
import com.softlayer.api.service.virtual.guest.power.State;
import com.softlayer.api.service.Account;
import com.softlayer.api.ApiException;

import java.util.*;


public class IBMUpdateInstancesTask implements Runnable {
  private IBMCloudClient client;

  public IBMUpdateInstancesTask(IBMCloudClient client) {
    this.client = client;
  }

  /**
   * Called by IBMCloudClient. Upate the status of all instances of a cloud profile.
   * Update instance user metadata when disk is mounted. It runs every minute.
   * @see <a href="https://github.com/softlayer/softlayer-java/blob/master/examples/src/main/java/com/softlayer/api/example/OrderVirtualServer.java">OrderVirtualServer</a>
   */
  public void run() {
    Logger LOG = Loggers.SERVER;
    InstanceStatus newStatus;
    InstanceStatus currentStatus;
    Status vsiStatus;
    State vsiState;
    Transaction vsiTransaction;
    Guest.Service service;
    Guest guest;
    String message;
    for(IBMCloudImage image : client.getImages()) {
      for(IBMCloudInstance instance : image.getInstances()) {
        currentStatus = instance.getStatus();
        boolean isStopped = checkStopped(instance);
        if (isStopped) {
          newStatus = InstanceStatus.STOPPED;
        } else {
          try {
            service = instance.guest.asService(instance.ibmClient);
            service.withMask().status().name();
            service.withMask().powerState().name();
            service.withMask().activeTransaction();
            service.withMask().activeTransaction().transactionStatus().friendlyName();
            guest = service.getObject();
            vsiStatus = guest.getStatus();
            vsiState = guest.getPowerState();
            vsiTransaction = guest.getActiveTransaction();
            if(vsiState != null && vsiState.getName().equals("Running")
                && !instance.metadataIsSet()) {
              instance.setMetadata();
            }
            newStatus = teamcityStatus(vsiStatus, vsiState, vsiTransaction, currentStatus);
          } catch(ApiException.NotFound e) {
            System.out.println("Error: " + e);
            LOG.warn("Error: " + e);
            newStatus = InstanceStatus.ERROR;
          }
        }
        message = "New status for " + instance.getInstanceId() + " is " + newStatus.getName();
        System.out.println(message);
        instance.setStatus(newStatus);
        if(removable(instance.getStatus())) {
          image.removeInstance(instance.getInstanceId());
        }
      }
    }
  }

  /**
   * If instance status is STOPPED or ERROR, the instance will be removed from image.
   */
  public boolean removable(InstanceStatus status) {
    return status == InstanceStatus.ERROR || status == InstanceStatus.STOPPED;
  }

  /**
   * Update instance status based on vsiStatus, vsiState and vsiTransaction.
   * If user clicks stop we set the status SCHEDULED_TO_STOP
   */
  private InstanceStatus teamcityStatus(Status vsiStatus, State vsiState,
      Transaction vsiTransaction, InstanceStatus currentStatus) {
    if (vsiStatus == null) {
      System.out.println("vsiStatus is null");
    } else {
      System.out.println("vsiStatus is " + vsiStatus.getName());
    }
    if(vsiState == null) {
      System.out.println("vsiState is null");
    } else {
      System.out.println("vsiState is " + vsiState.getName());
    }
    if (vsiTransaction == null) {
      System.out.println("vsiTransaction is null");
    } else {
      System.out.println("vsiTransaction is " + 
          vsiTransaction.getTransactionStatus().getFriendlyName());
    }
    if (currentStatus == InstanceStatus.ERROR_CANNOT_STOP) {
      return currentStatus;
    }
    if (vsiStatus != null && vsiStatus.getName().equals("Terminating")) {
      return InstanceStatus.STOPPING;
    }
    if (vsiStatus != null && vsiStatus.getName().equals("Disconnected")) {
      return InstanceStatus.STOPPED;
    }
    if (currentStatus == InstanceStatus.SCHEDULED_TO_STOP) {
      return InstanceStatus.SCHEDULED_TO_STOP;
    }
    if(vsiState != null && vsiState.getName().equals("Halted")) {
      return InstanceStatus.STARTING;
    }
    if (vsiState != null && vsiState.getName().equals("Running") && vsiTransaction == null) {
      return InstanceStatus.RUNNING;
    }
    return currentStatus;
  }

  /**
   * Check whether the instance is already removed from SL.
   * If yes, we don't call SL api to update status, in order to avoid unnecessary
   * ObjectNotFound exception.
   */
  private boolean checkStopped(IBMCloudInstance instance) {
    InstanceStatus currentStatus = instance.getStatus();
    if (currentStatus == InstanceStatus.SCHEDULED_TO_STOP
        || currentStatus == InstanceStatus.STOPPING) {
      Account.Service accountService = Account.service(instance.ibmClient);
      List<Guest> guests = accountService.getVirtualGuests();
      for (Guest accountGuest : guests) {
        if (accountGuest.getId().toString().equals(instance.getInstanceId())) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }
}
