/*
* @author: Jiawei He
* jiawei.he@ibm.com
**/

// Terminate instance when no active transaction.

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.service.provisioning.version1.Transaction;
import com.softlayer.api.service.virtual.Guest;

import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.log.Loggers;

import com.intellij.openapi.diagnostic.Logger;

public class IBMTerminateInstanceTask implements Runnable{
	private IBMCloudInstance instance;
	private final static Logger LOG = Loggers.SERVER;
	private boolean deleted = false;
	
	public IBMTerminateInstanceTask(IBMCloudInstance instance) {
	    this.instance = instance;
	  }

	@Override
	public void run() {
		if (instance == null) {
			return;
		}
		Transaction vsiTransaction;
	    Guest.Service service = instance.guest.asService(instance.ibmClient);
	    Guest guest;
	    service.withMask().activeTransaction();
        service.withMask().activeTransaction().transactionStatus().friendlyName();
        guest = service.getObject();
        vsiTransaction = guest.getActiveTransaction();

		if (vsiTransaction == null && !deleted) {
		    LOG.info("Cancelling VSI " + instance.getName());
		    try {
		      service.deleteObject();
		      deleted = true;
		      LOG.info("Instance already terminated");
		    } catch (Exception e) {
		      LOG.warn("Error: " + e);
		      instance.setStatus(InstanceStatus.ERROR_CANNOT_STOP);
		      throw e;
		    }
		}
	}

}
