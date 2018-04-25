import ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jetbrains.buildServer.clouds.*;

public class FakeCloudImageParameters extends CloudImageParameters {
  public Map<String,String> parameters;

  public FakeCloudImageParameters() {
    parameters = new HashMap<String,String>();
    parameters.put(SoftlayerCloudConstants.AGENT_NAME, "fake-agent");
    parameters.put(SoftlayerCloudConstants.DATACENTER_LIST, "dal13");
    parameters.put(SoftlayerCloudConstants.DISK_TYPE, "false");
    parameters.put(SoftlayerCloudConstants.DOMAIN_NAME, "ibmwdtest.com");
    parameters.put(SoftlayerCloudConstants.MAX_CORES, "1");
    parameters.put(SoftlayerCloudConstants.MAX_MEMORY, "1024");
    parameters.put(SoftlayerCloudConstants.NETWORK, "100");
    parameters.put(
        SoftlayerCloudConstants.VSI_TEMPLATE_LIST,
        "TC-buildAgent-autoAuth-Apr9");
    parameters.put(SoftlayerCloudConstants.PROFILE_ID, "fake-profile");
    parameters.put(SoftlayerCloudConstants.SOURCE_ID, "fake-source-id");
  }

  public Integer getAgentPoolId() {
    return 0;
  }

  public String getId() {
    return "fake-image";
  }

  public String getParameter(String paramName) {
    return parameters.get(paramName);
  }

  public Set<String> getParameterNames() {
    return parameters.keySet();
  }

  public Map<String,String> getParameters() {
    return parameters;
  }
}
