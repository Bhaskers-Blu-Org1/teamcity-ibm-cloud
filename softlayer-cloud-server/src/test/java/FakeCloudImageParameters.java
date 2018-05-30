/*
* @author: Scott Wyman Neagle
* scottwn@ibm.com
**/

import ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jetbrains.buildServer.clouds.*;

// This is a mock object for unit testing. It needs to be added to CloudClientParameters
// and CloudProfile.

public class FakeCloudImageParameters extends CloudImageParameters {
  public Map<String,String> parameters;

  public FakeCloudImageParameters() {
    parameters = new HashMap<String,String>();
    parameters.put(SoftlayerCloudConstants.AGENT_NAME, "fake-agent");
    parameters.put(SoftlayerCloudConstants.DATACENTER_LIST, "dal13");
    parameters.put(SoftlayerCloudConstants.DISK_TYPE, "{\"type\":\"SAN\", \"value\":\"false\"}");
    parameters.put(SoftlayerCloudConstants.DOMAIN_NAME, "ibmwdtest.com");
    parameters.put(SoftlayerCloudConstants.MAX_CORES, "1");
    parameters.put(SoftlayerCloudConstants.MAX_MEMORY, "1024");
    parameters.put(SoftlayerCloudConstants.NETWORK, "100");
    parameters.put(SoftlayerCloudConstants.VSI_BILLING, "{\"type\":\"Hourly\", \"value\":\"true\"}");
    parameters.put(
            SoftlayerCloudConstants.VSI_TEMPLATE_LIST,
            "{\"type\":\"TC-buildAgent-autoAuth-Apr9\", \"value\":\"aaad7259-06ff-453b-bedc-e425661fa151\"}");
    parameters.put(SoftlayerCloudConstants.PROFILE_ID, "fake-profile");
    parameters.put(SoftlayerCloudConstants.SOURCE_ID, "fake-source-id");
    parameters.put(SoftlayerCloudConstants.MAXIMUM_INSTANCES, "");
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
