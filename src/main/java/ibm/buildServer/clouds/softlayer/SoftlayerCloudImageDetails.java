package ibm.buildServer.clouds.softlayer;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.clouds.CloudImageParameters;

public class SoftlayerCloudImageDetails{
	
	private  String sourceId = null;
	public  String network;
	//private  int myMaxInstances;
	public  String profileId;
	public String agentName;
	

	public String datacenter;
	public String localDiskFlag;
	public String vsiTemplate;
	public long maxCores;
	public long maxMemory;
	public String domainName;
	public int agentPoolId;
	
	
	public SoftlayerCloudImageDetails(@NotNull final CloudImageParameters imageParameters){
		agentPoolId = imageParameters.getAgentPoolId();
		agentName = imageParameters.getParameter(SoftlayerCloudConstants.AGENT_NAME);
		datacenter = imageParameters.getParameter(SoftlayerCloudConstants.DATACENTER_LIST);
		localDiskFlag = imageParameters.getParameter(SoftlayerCloudConstants.DISK_TYPE);
		domainName = imageParameters.getParameter(SoftlayerCloudConstants.DOMAIN_NAME);
		maxCores = Long.parseLong(imageParameters.getParameter(SoftlayerCloudConstants.MAX_CORES));
		maxMemory = Long.parseLong(imageParameters.getParameter(SoftlayerCloudConstants.MAX_MEMORY));
		network = imageParameters.getParameter(SoftlayerCloudConstants.NETWORK);
		vsiTemplate = imageParameters.getParameter(SoftlayerCloudConstants.VSI_TEMPLATE_LIST);
		profileId = imageParameters.getParameter(SoftlayerCloudConstants.PROFILE_ID);
		sourceId = imageParameters.getParameter(SoftlayerCloudConstants.SOURCE_ID);
	}

	public int getAgentPoolId() {
		return agentPoolId;
	}

	public void setAgentPoolId(int agentPoolId) {
		this.agentPoolId = agentPoolId;
	}
	
	public String getSourceId() {
		return sourceId;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}

	public String getLocalDiskFlag() {
		return localDiskFlag;
	}

	public void setLocalDiskFlag(String localDiskFlag) {
		this.localDiskFlag = localDiskFlag;
	}

	public String getVsiTemplate() {
		return vsiTemplate;
	}

	public void setVsiTemplate(String vsiTemplate) {
		this.vsiTemplate = vsiTemplate;
	}

	public long getMaxCores() {
		return maxCores;
	}

	public void setMaxCores(long maxCores) {
		this.maxCores = maxCores;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	//int getMaxInstances();
	
	 @Override
	  public String toString() {
		  String str = "Image Details: {\nProfile Id:"+ profileId+
				  		",\nNetwork:"+network+",\nSource Id:"+sourceId+
				  		",\nAgentName:"+agentName+",\nDatacenter:"+datacenter+
				  		",\nDiskType:"+localDiskFlag+",\nVSI Template:"+vsiTemplate+
				  		",\nMax Cores:"+maxCores+",\nMax Memory:"+maxMemory+
				  		",\nDomain:"+domainName+
				  		",\nAgent Pool Id:"+Integer.toString(agentPoolId)+
				        "\n}";
		  return str;
	  }
	 
	 
	
}
