package ibm.buildServer.clouds.ibm;

import jetbrains.buildServer.clouds.CloudImageParameters;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import ibm.buildServer.clouds.ibm.IBMCloudConstants;

public class IBMCloudImageDetails{
  private String sourceId = null;
  private String name = null;
  private String profileId;
  private String vsiTemplate;
  private String datacenter;
  private String agentName;
  private String domainName;
  private String network;
  private String flavorList;
  private String localDiskFlag;
  private String diskSize;
  private String maxCores;
  private String maxMemory;
  private boolean customizeMachineType;
  private boolean vsiBilling;
  private long maxInstances;
  private int agentPoolId;

  public IBMCloudImageDetails(@NotNull final CloudImageParameters imageParameters) {
   
    name = getNameFromJSON(imageParameters.getParameter(IBMCloudConstants.VSI_TEMPLATE_LIST))
        +"_"+imageParameters.getParameter(IBMCloudConstants.SOURCE_ID);
    sourceId = imageParameters.getParameter(IBMCloudConstants.SOURCE_ID);
    profileId = imageParameters.getParameter(IBMCloudConstants.PROFILE_ID);
    vsiTemplate = getValueFromJSON(
        imageParameters.getParameter(IBMCloudConstants.VSI_TEMPLATE_LIST));
    datacenter = imageParameters.getParameter(IBMCloudConstants.DATACENTER_LIST);
    agentName = imageParameters.getParameter(IBMCloudConstants.AGENT_NAME);
    domainName = imageParameters.getParameter(IBMCloudConstants.DOMAIN_NAME);
    network = imageParameters.getParameter(IBMCloudConstants.NETWORK);
    flavorList = imageParameters.getParameter(IBMCloudConstants.FLAVOR_LIST);
    localDiskFlag = imageParameters.getParameter(IBMCloudConstants.DISK_TYPE);
    diskSize = imageParameters.getParameter(IBMCloudConstants.DISK_SIZE);
    customizeMachineType = Boolean.parseBoolean(
        imageParameters.getParameter(IBMCloudConstants.CUSTOMIZE_MACHINE_TYPE));
    vsiBilling = Boolean.parseBoolean(getValueFromJSON(
          imageParameters.getParameter(IBMCloudConstants.VSI_BILLING)));
    maxCores = imageParameters.getParameter(IBMCloudConstants.MAX_CORES);
    maxMemory = imageParameters.getParameter(IBMCloudConstants.MAX_MEMORY);
    try {
      maxInstances = Long.parseLong(
          imageParameters.getParameter(IBMCloudConstants.MAXIMUM_INSTANCES));
    } catch(Exception e) {
      maxInstances = -1;
    }
    agentPoolId = imageParameters.getAgentPoolId();
  }

  private String getValueFromJSON(String jsonString) {
    JSONObject jsonObject = new JSONObject(jsonString);
    String value = jsonObject.getString("value");
    return value;
  }
  
  public String getNameFromJSON(String jsonString) {
    JSONObject jsonObject = new JSONObject(jsonString);
    String value = jsonObject.getString("name");
    return value;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public String getVsiTemplate() {
    return vsiTemplate;
  }

  public void setVsiTemplate(String vsiTemplate) {
    this.vsiTemplate = vsiTemplate;
  }

  public String getDatacenter() {
    return datacenter;
  }

  public void setDatacenter(String datacenter) {
    this.datacenter = datacenter;
  }

  public String getAgentName() {
    return agentName;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getFlavorList() {
    return flavorList;
  }

  public void setFlavorList(String flavorList) {
    this.flavorList = flavorList;
  }

  public boolean getLocalDiskFlag() {
    return Boolean.parseBoolean(localDiskFlag);
  }

  public void setLocalDiskFlag(String localDiskFlag) {
    this.localDiskFlag = localDiskFlag;
  }

  public String getDiskSize() {
    return diskSize;
  }

  public void setDiskSize(String diskSize) {
    this.diskSize = diskSize;
  }

  public boolean getCustomizeMachineType() {
    return customizeMachineType;
  }

  public void setCustomizeMachineType(boolean customizeMachineType) {
    this.customizeMachineType = customizeMachineType;
  }

  public boolean getVsiBilling() {
    return vsiBilling;
  }

  public void setVsiBilling(boolean vsiBilling) {
    this.vsiBilling = vsiBilling;
  }

  public long getMaxCores() {
    return Long.parseLong(maxCores);
  }

  public void setMaxCores(String maxCores) {
    this.maxCores = maxCores;
  }

  public long getMaxMemory() {
    return Long.parseLong(maxMemory);
  }

  public void setMaxMemory(String maxMemory) {
    this.maxMemory = maxMemory;
  }

  public long getMaxInstances() {
    return maxInstances;
  };

  public void setMaxInstances(long maxInstances) {
    this.maxInstances = maxInstances;
  }

  public int getAgentPoolId() {
    return agentPoolId;
  }

  public void setAgentPoolId(int agentPoolId) {
    this.agentPoolId = agentPoolId;
  }

  @Override
  public String toString() {
    String str = "Image Details: {\nProfile Id:" + profileId + ",\nNetwork:"
      + network + ",\nSource Id:" + sourceId + ",\nAgentName:" + agentName
      + ",\nDatacenter:" + datacenter + ",\nDiskType:" + localDiskFlag
      + ",\nDiskSize:" + diskSize + ",\nVSI Template:" + vsiTemplate
      + ",\nMax Cores:" + maxCores + ",\nMax Memory:" + maxMemory + ",\nDomain:"
      + domainName + ",\nAgent Pool Id:" + Integer.toString(agentPoolId)
      + ",\nBilling :" + Boolean.toString(vsiBilling) + ",\nMax Instances :"
      + Long.toString(maxInstances) + "\n}";
    return str;
  }
}
