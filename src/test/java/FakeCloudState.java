import ibm.buildServer.clouds.softlayer.SoftlayerCloudInstance;
import java.util.ArrayList;
import java.util.List;
import jetbrains.buildServer.clouds.*;

public class FakeCloudState implements CloudState {
  private SoftlayerCloudInstance instance;

  public FakeCloudState() {
    // Do nothing.
  }

  public String getProfileId() {
    return "fake-profile-id";
  }

  public String getProjectId() {
    return "fake-project-id";
  }

  public boolean isInstanceStarted(String imageId, String instanceId) {
    return true;
  }

  public void registerRunningInstance(String imageId, String instanceId) {
    // Do nothing.
  }

  public void registerTerminatedInstance(String imageId, String instanceId) {
    // Do nothing.
  }

  public List<String> getStartedInstances(String imageId) {
    return new ArrayList<String>();
  }
}
