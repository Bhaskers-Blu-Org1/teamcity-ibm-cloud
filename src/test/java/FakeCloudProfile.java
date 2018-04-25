import jetbrains.buildServer.clouds.*;

public class FakeCloudProfile implements CloudProfile {
  private String profileId;
  private String projectId;

  public FakeCloudProfile() {
    profileId = "fake-profile-id";
    projectId = "fake-project-id";
  }

  public String getProfileId() {
    return profileId;
  }

  public String getProjectId() {
    return projectId;
  }
}
