import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.clouds.*;

public class FakeCloudProfile implements CloudProfile {
  private String profileId;
  private String projectId;
  private List<CloudImageParameters> images;
  private CloudClientParameters parameters;

  public FakeCloudProfile() {
    profileId = "fake-profile-id";
    projectId = "fake-project-id";
    images = new ArrayList<CloudImageParameters>();
    images.add(new FakeCloudImageParameters());
    parameters = new FakeParameters();
  }

  public String getProfileId() {
    return profileId;
  }

  public String getProjectId() {
    return projectId;
  }
  
  public String profileDescription() {
    return this.toString();
  }

  public String getCloudCode() {
    return "fake-cloud-code";
  }

  public String getDescription() {
    return profileDescription();
  }

  public Collection<CloudImageParameters> getImagesParameters() {
    return images;
  }

  public CloudClientParameters getParameters() {
    return parameters;
  }

  public String getProfileName() {
    return "fake-profile-name";
  }

  public Map<String,String> getProfileProperties() {
    return parameters.getParameters();
  }

  public Long getTerminateIdleTime() {
    return Long.MAX_VALUE;
  }

  public boolean isEnabled() {
    return true;
  }
}
