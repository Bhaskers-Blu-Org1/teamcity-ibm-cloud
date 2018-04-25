import jetbrains.buildServer.clouds.*;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClientFactory;

public class FakeCloudRegistrar implements CloudRegistrar {
  private SoftlayerCloudClientFactory factory;

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
