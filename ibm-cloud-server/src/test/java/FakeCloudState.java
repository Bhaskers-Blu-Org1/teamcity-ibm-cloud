/*
* @author: scott wyman neagle
* scottwn@ibm.com
**/

import ibm.buildServer.clouds.ibm.IBMCloudInstance;

import java.util.ArrayList;
import java.util.List;

import jetbrains.buildServer.clouds.*;

// This is a mock object for unit testing. It needs to be passed to createNewClient.

public class FakeCloudState implements CloudState {
  private IBMCloudInstance instance;

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
