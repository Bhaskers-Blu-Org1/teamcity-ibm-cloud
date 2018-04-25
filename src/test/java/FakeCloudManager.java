import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudClientFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.server.CloudManagerBase;
import jetbrains.buildServer.clouds.server.ProjectCloudIntegrationStatus;

public class FakeCloudManager implements CloudManagerBase {
  private List<CloudProfile> profiles;
  private List<SoftlayerCloudClientFactory> cloudTypes;
  private SoftlayerCloudClient client;
  private ProjectCloudIntegrationStatus status;

  public FakeCloudManager() {
    profiles = new ArrayList<CloudProfile>();
    profiles.add(new FakeCloudProfile());
    SoftlayerCloudClientFactory factory =
      new SoftlayerCloudClientFactory(
          new FakeCloudRegistrar(),
          this,
          new FakePluginDescriptor());
    cloudTypes.add(factory);
    client = new SoftlayerCloudClient(new FakeParameters());
  }

  boolean isIntegrationEnabled(String projectId) {
    return true;
  }

  boolean isConfigurable(String projectId) {
    return true;
  }

  Collection<CloudProfile> listProfilesByProject(
      String projectId,
      boolean includeFromSubprojects) {
    return profiles;
  }

  Collection<CloudProfile> listAllProfiles() {
    return profiles;
  }

  Collection<SoftlayerCloudClientFactory> getCloudTypes() {
    return cloudTypes;
  }

  CloudType findCloudType(String cloudName) {
    return cloudTypes.get(0);
  }

  CloudProfile findProfileById(String projectId, String profileId) {
    return profiles.get(0);
  }

  CloudProfile findProfileGloballyById(String profileId) {
    return profiles.get(0);
  }

  CloudClientEx getClientIfExists(String projectId, String profileId) {
    return client;
  }

  CloudClientEx getClientIfExistsByProjectExtId(
      String ProjectExtId,
      String profileId) {
    return client;
  }

  CloudClientEx getClient(String projectId, String profileId) {
    return client;
  }

  void updateStatus(String projectId, ProjectCloudIntegrationStatus newStatus) {
    status = newStatus;
  }

  ProjectCloudIntegrationStatus getProjectCloudIntegrationStatus(String projectId) {
    return status;
  }

  void updateProfile(String projectId, CloudProfile cloudProfile) {
    // Do nothing.
  }
}
