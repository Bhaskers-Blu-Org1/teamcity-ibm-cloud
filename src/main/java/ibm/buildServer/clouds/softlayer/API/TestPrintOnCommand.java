package ibm.buildServer.clouds.softlayer.API;

import java.util.List;
import java.util.TreeMap;

import com.softlayer.api.ApiClient;
import com.softlayer.api.RestApiClient;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.Group;

public class TestPrintOnCommand {

	public TestPrintOnCommand() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		
		String username = "vidhi.shah@ibm.com";
		String apiKey = "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda";
		ApiClient client = new RestApiClient().withCredentials(username, apiKey);
		
		
		System.out.println( Account.service(client).getAccountStatus().getName());
		
	}

}
