/*
* @author: Jiawei He
* jiawei.he@ibm.com
**/

// Terminate instance when no active transaction.

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.service.provisioning.version1.Transaction;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.ApiClient;

import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.log.Loggers;

import com.intellij.openapi.diagnostic.Logger;

public class IBMTerminateInstanceTask implements Runnable{
	private final static Logger LOG = Loggers.SERVER;
	private boolean deleted = false;
    private IBMCloudInstance instance;
    private ApiClient ibmClient;
    private String name;
    private Guest vsi;
	
	public IBMTerminateInstanceTask(IBMCloudInstance instance) {
      this(instance.ibmClient, instance.getName(), instance.guest);
	  this.instance = instance;
	}

    public IBMTerminateInstanceTask(ApiClient client, String instanceName,
        Guest guest) {
      ibmClient = client;
      name = instanceName;
      vsi = guest;
    }

	@Override
	public void run() {
		if (ibmClient  == null) {
			return;
		}
		Transaction vsiTransaction;
	    Guest.Service service = vsi.asService(ibmClient);
	    Guest guest;
	    service.withMask().activeTransaction();
        service.withMask().activeTransaction().transactionStatus().friendlyName();
        guest = service.getObject();
        vsiTransaction = guest.getActiveTransaction();

		if (vsiTransaction == null && !deleted) {
		    LOG.info("Cancelling VSI " + name);
		    try {
		      service.deleteObject();
		      deleted = true;
		      LOG.info("Instance already terminated");
		    } catch (Exception e) {
		      LOG.warn("Error: " + e);
              if(instance != null) {
		        instance.setStatus(InstanceStatus.ERROR_CANNOT_STOP);
              }
		      throw e;
		    }
		}
	}

}
