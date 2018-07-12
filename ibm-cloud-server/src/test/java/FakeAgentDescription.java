/*
* @author: scott wyman neagle
* scottwn@ibm.com
**/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetbrains.buildServer.serverSide.*;

// This is a mock object for unit testing. It needs to be passed to containsAgent,
// generateAgentName, and findInstanceByAgent. It's also used to construct CloudInstanceUserData.

public class FakeAgentDescription implements AgentDescription {
  private Map<String,String> parameters;
  private List<RunType> runTypes;
  private List<String> plugins;

  public FakeAgentDescription() {
    parameters = new HashMap<String,String>();
    runTypes = new ArrayList<RunType>();
    plugins = new ArrayList<String>();
  }

  public Map<String,String> getAvailableParameters() {
    return parameters;
  }

  public List<RunType> getAvailableRunTypes() {
    return runTypes;
  }

  public List<String> getAvailableVcsPlugins() {
    return plugins;
  }

  public Map<String,String> getBuildParameters() {
    return parameters;
  }

  public Map<String,String> getConfigurationParameters() {
    return parameters;
  }

  public int getCpuBenchmarkIndex() {
    return 0;
  }

  public String getOperatingSystemName() {
    return "FAKE_OS";
  }

  public boolean isCaseInsensitiveEnvironment() {
    return false;
  }

  public Map<String,String> getDefinedParameters() {
    return getConfigurationParameters();
  }
  
  public void addParameter(String key, String value) {
    parameters.put(key, value);
  }
}
