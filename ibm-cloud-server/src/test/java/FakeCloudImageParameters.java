/*
* @author: Scott Wyman Neagle
* scottwn@ibm.com
**/

import ibm.buildServer.clouds.ibm.IBMCloudConstants;

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
    parameters.put(IBMCloudConstants.AGENT_NAME, "fake-agent");
    parameters.put(IBMCloudConstants.DATACENTER_LIST, "dal13");
    parameters.put(IBMCloudConstants.DISK_TYPE, "false");
    parameters.put(IBMCloudConstants.DOMAIN_NAME, "ibmwdtest.com");
    parameters.put(IBMCloudConstants.CUSTOMIZE_MACHINE_TYPE, "true");
    parameters.put(IBMCloudConstants.MAX_CORES, "1");
    parameters.put(IBMCloudConstants.MAX_MEMORY, "1024");
    parameters.put(IBMCloudConstants.NETWORK, "100");
    parameters.put(IBMCloudConstants.VSI_BILLING, "{\"type\":\"Hourly\", \"value\":\"true\"}");
    parameters.put(
            IBMCloudConstants.VSI_TEMPLATE_LIST,
            "{\"type\":\"TC-buildAgent-autoAuth-Apr9\", \"value\":\"aaad7259-06ff-453b-bedc-e425661fa151\"}");
    parameters.put(IBMCloudConstants.PROFILE_ID, "fake-profile");
    parameters.put(IBMCloudConstants.SOURCE_ID, "fake-source-id");
    parameters.put(IBMCloudConstants.MAXIMUM_INSTANCES, "");
    
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
