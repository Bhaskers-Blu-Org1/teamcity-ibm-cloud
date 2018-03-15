/**
 * @Author: Vidhi.Shah@ibm.com
 * 
 * Class: ImageList
 * Description: Fetch list of all private image template from softlayer account. We use softlayer api for making restful call and get json result.
 * 
 */
package ibm.buildServer.clouds.softlayer;

//import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.springframework.http.client.OkHttpClientHttpRequestFactory;

public class ImageList {
	
	
	
	public static void main(String[] args) {
		System.out.println("hell");
		try
		{
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://vidhi.shah@ibm.com:c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda@api.softlayer.com/rest/v3/SoftLayer_Account/PrivateBlockDeviceTemplateGroups.json")
		  .get()
		  .addHeader("Cache-Control", "no-cache")
		  .addHeader("Postman-Token", "ca1428a6-8b8c-4abd-9c09-bdb7f176bf95")
		  .build();

		Response response = client.newCall(request).execute();
		System.out.println(response);
		}
		catch(IOException e)
		{
			
		}
		
	}

}
