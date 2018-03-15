/**
 * @Author: Vidhi.Shah@ibm.com
 * 
 * Class: ImageList
 * Description: Fetch list of all private image template from softlayer account. We use softlayer api for making restful call and get json result.
 * 
 */
package ibm.buildServer.clouds.softlayer;

//import com.squareup.okhttp.OkHttpClient;
import okhttp3.OkHttpClient;

public class ImageList {
	
	OkHttpClient client = new OkHttpClient();

	  String run(String url) throws IOException {
	    Request request = new Request.Builder()
	        .url(url)
	        .build();

	    try (Response response = client.newCall(request).execute()) {
	      return response.body().string();
	    }
	  }

	
	public static void main(String[] args) {
		System.out.println("hell");
		
		ImageList example = new ImageList();
	    String response;
		try {
			response = example.run("https://raw.github.com/square/okhttp/master/README.md");
			 System.out.println(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   

		
		
	}

}
