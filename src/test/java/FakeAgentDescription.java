import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.serverSide.*;

public class FakeAgentDescription implements AgentDescription {
  private Map<String,String> parameters;
  private List<RunType> runTypes;
  private List<String> plugins;

  public FakeAgentDescription() {
    parameters = new HashMap<String,String>();
    runTypes = new ArrayList<RunType>();
    plugins = new ArrayList<String>();
    parameters.put("name","fake-agent-name");
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
}
