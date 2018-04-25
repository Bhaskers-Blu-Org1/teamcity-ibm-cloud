import ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants;
import jetbrains.buildServer.clouds.*;

public class FakeParameters extends CloudClientParameters {
  public Map<String,String> parameters;
  public List<CloudImageParameters> imageParameters;

  public FakeParameters() {
    parameters = new HashMap<String,String>();
    parameters.add(
        SoftlayerCloudConstants.USER_NAME,
        System.getenv("SOFTLAYER_USER"));
    parameters.add(
        SoftlayerCloudConstants.API_KEY,
        System.getenv("SOFTLAYER_API"));
    imageParameters = new ArrayList<CloudImageParameters>();
    imageParameters.add(new FakeCloudImageParameters());
  }

  public Collection<CloudImageParameters> getCloudImages() {
    return imageParameters;
  }

  public getParameter(String name) {
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
