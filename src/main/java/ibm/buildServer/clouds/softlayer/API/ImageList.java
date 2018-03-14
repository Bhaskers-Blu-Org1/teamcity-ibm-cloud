/**
 * @Author: Vidhi.Shah@ibm.com
 * 
 * Class: ImageList
 * Description: Fetch list of all private image template from softlayer account. We use softlayer-api java sdk..
 * 
 */
package ibm.buildServer.clouds.softlayer.API;

import java.io.IOException;
import java.util.TreeMap;
import java.util.List;

import com.softlayer.api.*;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.*;



public class ImageList   {
	
	public static TreeMap<Long,String> getPrivateImageTemplate()
	{
		ApiClient client = new RestApiClient().withCredentials("vidhi.shah@ibm.com", "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda");
		List<Group> imageList = Account.service(client).getPrivateBlockDeviceTemplateGroups();
		TreeMap<Long,String> imageMap = new TreeMap<Long, String>();
		for(Group group: imageList)
		{
			imageMap.put(group.getId(),group.getName());
		}
		
		return imageMap;
	}
}
