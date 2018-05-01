/*
* @author: scott wyman neagle
* scottwn@ibm.com
**/

import ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetbrains.buildServer.clouds.*;

// This is a mock object for unit testing. It's returned by CloudProfile.getParameters
// and used to construct CloudClient and CloudImageDetails. It's also passed to
// createNewClient.

public class FakeParameters extends CloudClientParameters {
  public Map<String,String> parameters;
  public List<CloudImageParameters> imageParameters;

  public FakeParameters() {
    parameters = new HashMap<String,String>();
    parameters.put(
        SoftlayerCloudConstants.USER_NAME,
        System.getenv("SOFTLAYER_USER"));
    parameters.put(
        SoftlayerCloudConstants.API_KEY,
        System.getenv("SOFTLAYER_API"));
    imageParameters = new ArrayList<CloudImageParameters>();
    imageParameters.add(new FakeCloudImageParameters());
  }

  public Collection<CloudImageParameters> getCloudImages() {
    return imageParameters;
  }

  public String getParameter(String name) {
    return parameters.get(name);
  }

  public Map<String,String> getParameters() {
    return parameters;
  }

  public String getProfileDescription() {
    return "This is a fake cloud profile for unit testing.";
  }

  public Collection<String> listParameterNames() {
    return parameters.values();
  }
}
