/*
* @author: Jiawei He
* jiawei.he@ibm.com
**/

// Terminate instance when no active transaction.

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.service.provisioning.version1.Transaction;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.ApiClient;

import jetbrains.buildServer.log.Loggers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;

import com.intellij.openapi.diagnostic.Logger;

/**
 * Called by IBMCloudClientFactory. Use a concurrenthashmap to keep track of 
 * the terminating Guests with active transactions.
 */
public class IBMTerminateInstanceTask implements Runnable{
  private final static Logger LOG = Loggers.SERVER;
  private static ApiClient ibmClient;
  private static Map<String, Guest> guests;
	
  public IBMTerminateInstanceTask() {
    guests = new ConcurrentHashMap<>();
  }

  /** 
   * Iterate the map. If Guest has no active transactions, terminate it, and remove it from the map.
   */
  @Override
  public void run() {
    if (ibmClient == null) {
      return;
    }
    Map<String, Guest> view = Collections.unmodifiableMap(guests);
    for (Map.Entry<String, Guest> entry : view.entrySet()) {
      String vsiId = entry.getKey();
      Guest guest = entry.getValue();
      Guest.Service service = guest.asService(ibmClient);
      service.withMask().activeTransaction();
      service.withMask().activeTransaction().transactionStatus().friendlyName();
      Transaction vsiTransaction = service.getObject().getActiveTransaction();
      if (vsiTransaction == null) {
        try {
          service.deleteObject();
          guests.remove(vsiId);
          LOG.info(vsiId + " already terminated");
        } catch (Exception e) {
          LOG.warn("Error: " + e);
        }
      }
    }
  }

  /**
   * Called by IBMCloudClient.checkMetaData() and IBMCloudInstance.terminate(). Add Guest to map.
   * @param vsiId
   * @param guest
   * @param apiClient
   */
  public static void add(String vsiId, Guest guest, ApiClient apiClient) {
    ibmClient = apiClient;
    guests.put(vsiId, guest);
  }
}
