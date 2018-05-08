/*
* @author: Jiawei He
* jiawei.he@ibm.com
**/

//terminate instance when no active transaction

package ibm.buildServer.clouds.softlayer;

import com.softlayer.api.service.provisioning.version1.Transaction;
import com.softlayer.api.service.virtual.Guest;

import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.log.Loggers;

import com.intellij.openapi.diagnostic.Logger;

public class SoftlayerTerminateInstanceTask implements Runnable{
	private SoftlayerCloudInstance instance;
	private final static Logger LOG = Loggers.SERVER;
	
	public SoftlayerTerminateInstanceTask(SoftlayerCloudInstance instance) {
	    this.instance = instance;
	  }

	@Override
	public void run() {
		if (instance == null) {
			return;
		}
		Transaction vsiTransaction;
	    Guest.Service service = instance.guest.asService(instance.softlayerClient);
	    Guest guest;
	    service.withMask().activeTransaction();
        service.withMask().activeTransaction().transactionStatus().friendlyName();
        guest = service.getObject();
        vsiTransaction = guest.getActiveTransaction();

		if (vsiTransaction == null) {
			LOG.info("Cancelling SoftLayer VSI " + instance.getName());
		    try {
		      service.deleteObject();
		      instance.setStatus(InstanceStatus.SCHEDULED_TO_STOP);
		      LOG.info("Instance already terminated");
		    } catch (Exception e) {
		      LOG.warn("Error: " + e);
		      instance.setStatus(InstanceStatus.ERROR_CANNOT_STOP);
		    }
		} else {
			LOG.info("Active transaction");
		}
	}

}
