/*
* @author: scott wyman neagle
* scottwn@ibm.com
**/

import jetbrains.buildServer.clouds.*;

import ibm.buildServer.clouds.ibm.IBMCloudClientFactory;

// This is a mock object for unit testing. It's used to construct CloudClientFactory.

public class FakeCloudRegistrar implements CloudRegistrar {
  private CloudClientFactory factory;

  public FakeCloudRegistrar() {
    // Do nothing.
  }

  public void registerCloudFactory(CloudClientFactory factory) {
    this.factory = factory;
  }

  public void unregisterCloudFactory(CloudClientFactory factory) {
    if(this.factory == factory) {
      this.factory = null;
    }
  }
}
