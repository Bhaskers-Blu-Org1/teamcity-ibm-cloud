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
  //use a hashset to store the instanceIDs that user clicked stop
  private Set<String> clickedStopInstances;
  private String currentInstanceId;

  public IBMUpdateInstancesTask(IBMCloudClient client) {
    this.client = client;
    clickedStopInstances = new HashSet<>();
  }

  public void run() {
    Logger LOG = Loggers.SERVER;
    LOG.info("IBMUpdateInstancesTask is running.");
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
        currentInstanceId = instance.getInstanceId();
        boolean isStopped = checkStopped(instance);        
        if (isStopped) {
        	newStatus = InstanceStatus.STOPPED;
        } else {
        	try {
                // This logic is modeled on https://github.com/softlayer/softlayer-java/blob/master/examples/src/main/java/com/softlayer/api/example/OrderVirtualServer.java
                service = instance.guest.asService(instance.ibmClient);
                service.withMask().status().name();
                service.withMask().powerState().name();
                service.withMask().activeTransaction();
                service.withMask().activeTransaction().transactionStatus().friendlyName();
                guest = service.getObject();
                vsiStatus = guest.getStatus();
                vsiState = guest.getPowerState();
                vsiTransaction = guest.getActiveTransaction();
                // Update instance user metadata when disk is mounted.
                if(vsiState != null
                    && vsiState.getName().equals("Running")
                    && !instance.metadataIsSet()) {
                  instance.setMetadata();
                }
                newStatus = teamcityStatus(
                    vsiStatus,
                    vsiState,
                    vsiTransaction,
                    currentStatus);
              } catch(ApiException.NotFound e) {
                System.out.println("Error: " + e);
                LOG.warn("Error: " + e);
                newStatus = InstanceStatus.ERROR;
              }
        }
        message = "New status for " + currentInstanceId + " is "
          + newStatus.getName();
        // This print statement is for checking the status during automated unit
        // tests.
        System.out.println(message);
        instance.setStatus(newStatus);
        if(removable(instance.getStatus())) {
          image.removeInstance(instance.getInstanceId());
          clickedStopInstances.remove(instance.getInstanceId());
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
      Transaction vsiTransaction,
      InstanceStatus currentStatus) {
    if(vsiStatus == null) {
      // println statements are for printing to screen during tests as logging has
      // not been implemented in automated unit tests.
      System.out.println("vsiStatus is null");
    } else {
      System.out.println("vsiStatus is " + vsiStatus.getName());
    }
    if(vsiState == null) {
      System.out.println("vsiState is null");
    } else {
      System.out.println("vsiState is " + vsiState.getName());
    }
    if(vsiTransaction == null) {
      System.out.println("vsiTransaction is null");
    } else {
      System.out.println("vsiTransaction is "
          + vsiTransaction.getTransactionStatus().getFriendlyName());
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
    //If user clicks stop, we just show the status SCHEDULED_TO_STOP 
    if (clickedStopInstances.contains(currentInstanceId)) {
      return InstanceStatus.SCHEDULED_TO_STOP;
    }
    if(vsiState != null && vsiState.getName().equals("Halted")) {
      return InstanceStatus.STARTING;
    }
    if(vsiState != null
        && vsiState.getName().equals("Running")
        && vsiTransaction == null) {
      return InstanceStatus.RUNNING;
    }
    return currentStatus;
  }
  
  public void setClickedStop(String instanceId) {
	  clickedStopInstances.add(instanceId);
  }
  
  private boolean checkStopped(IBMCloudInstance instance) {
    InstanceStatus currentStatus = instance.getStatus();
    //Check whether the instance is already removed from SL.
    //If yes, we don't call SL api to update status, in order to avoid unnecessary ObjectNotFound exception.
    if (currentStatus == InstanceStatus.SCHEDULED_TO_STOP || currentStatus == InstanceStatus.STOPPING) {
      Account.Service accountService = Account.service(instance.ibmClient);
      List<Guest> guests = accountService.getVirtualGuests();
      for(Guest accountGuest : guests) {
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
