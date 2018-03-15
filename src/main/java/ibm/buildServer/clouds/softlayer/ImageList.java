/**
 * @Author: Vidhi.Shah@ibm.com
 * 
 * Class: ImageList
 * Description: Fetch list of all private image template from softlayer account. We use softlayer api for making restful call and get json result.
 * 
 */
package ibm.buildServer.clouds.softlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.softlayer.api.*;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.*;

public class ImageList {
	
	public static HashMap<Long,String> getPrivateImageTemplate()
	{
		ApiClient client = new RestApiClient().withCredentials("vidhi.shah@ibm.com", "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda");
		List<Group> groupList = Account.service(client).getPrivateBlockDeviceTemplateGroups();
		HashMap<Long,String> groupMap = new HashMap<Long, String>();
		for(Group group: groupList)
		{
			groupMap.put(group.getId(),group.getName());
		}
		
		return groupMap;
	}
}
