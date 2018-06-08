/*
* @author: Scott Wyman Neagle
* scottwn@ibm.com
**/

import ibm.buildServer.clouds.ibm.IBMCloudClient;
import ibm.buildServer.clouds.ibm.IBMCloudClientFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.server.CloudManagerBase;
import jetbrains.buildServer.clouds.server.ProjectCloudIntegrationStatus;

// This is a mock object for unit testing. It's used to construct CloudClientFactory.

public class FakeCloudManager implements CloudManagerBase {
  private List<CloudProfile> profiles;
  private List<IBMCloudClientFactory> cloudTypes;
  private IBMCloudClient client;
  private ProjectCloudIntegrationStatus status;

  public FakeCloudManager() {
    profiles = new ArrayList<CloudProfile>();
    profiles.add(new FakeCloudProfile());
    IBMCloudClientFactory factory =
      new IBMCloudClientFactory(
          new FakeCloudRegistrar(),
          this,
          new FakePluginDescriptor());
    cloudTypes = new ArrayList<IBMCloudClientFactory>();
    cloudTypes.add(factory);
    client = new IBMCloudClient(new FakeParameters());
  }

  public boolean isIntegrationEnabled(String projectId) {
    return true;
  }

  public boolean isConfigurable(String projectId) {
    return true;
  }

  public Collection<CloudProfile> listProfilesByProject(
      String projectId,
      boolean includeFromSubprojects) {
    return profiles;
  }

  public Collection<CloudProfile> listAllProfiles() {
    return profiles;
  }

  public Collection<IBMCloudClientFactory> getCloudTypes() {
    return cloudTypes;
  }

  public CloudType findCloudType(String cloudName) {
    return cloudTypes.get(0);
  }

  public CloudProfile findProfileById(String projectId, String profileId) {
    return profiles.get(0);
  }

  public CloudProfile findProfileGloballyById(String profileId) {
    return profiles.get(0);
  }

  public CloudClientEx getClientIfExists(String projectId, String profileId) {
    return client;
  }

  public CloudClientEx getClientIfExistsByProjectExtId(
      String ProjectExtId,
      String profileId) {
    return client;
  }

  public CloudClientEx getClient(String projectId, String profileId) {
    return client;
  }

  public void updateStatus(String projectId, ProjectCloudIntegrationStatus newStatus) {
    status = newStatus;
  }

  public ProjectCloudIntegrationStatus getProjectIntegrationStatus(String projectId) {
    return status;
  }

  public void updateProfile(String projectId, CloudProfile cloudProfile) {
    // Do nothing.
  }
}
