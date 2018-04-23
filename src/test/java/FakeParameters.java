import jetbrains.buildServer.clouds.*;

public class FakeParameters extends CloudClientParameters {
  public Map<String,String> parameters;
  public List<CloudImageParameters> imageParameters;

  public FakeParameters() {
    parameters = new HashMap<String,String>();
    imageParameters = new List<CloudImageParameters>();
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
