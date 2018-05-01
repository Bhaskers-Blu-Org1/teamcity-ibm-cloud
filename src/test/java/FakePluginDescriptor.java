/*
* @author: scott wyman neagle
* scottwn@ibm.com
**/

import java.io.File;

import jetbrains.buildServer.web.openapi.PluginDescriptor;

// This is a mock object for unit testing. It's used to construct CloudClientFactory.

public class FakePluginDescriptor implements PluginDescriptor {
  File root;

  public FakePluginDescriptor() {
    root = new File("/");
  }

  public String getParameterValue(String key) {
    return "fake-parameter";
  }

  public String getPluginName() {
    return "fake-plugin";
  }

  public String getPluginResourcesPath() {
    return root.toString() + "settings.jsp";
  }

  public String getPluginResourcesPath(String relativePath) {
    return root.toString() + relativePath;
  }

  public File getPluginRoot() {
    return root;
  }

  public String getPluginVersion() {
    return "0";
  }
}
