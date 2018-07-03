/*
 * @author: Scott Wyman Neagle
 * scottwn@ibm.com
 */

package ibm.buildServer.clouds.ibm;

import jetbrains.buildServer.clouds.*;

import com.softlayer.api.ApiClient;

public class FakeCloudInstance extends IBMCloudInstance {
  private String id;

  public FakeCloudInstance(IBMCloudImageDetails details,
      CloudInstanceUserData data, ApiClient ibmClient) {
    super(details, data, ibmClient);
  }

  public setId(String id) {
    this.id = id;
  }

  public String getInstanceId() {
    return id;
  }
}
