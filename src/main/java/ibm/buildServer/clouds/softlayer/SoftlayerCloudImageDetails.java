package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudImageParameters;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftlayerCloudImageDetails{
	
	private  String sourceId = null;
	public  String network;
	//private  int myMaxInstances;
	public  String profileId;
	
	public SoftlayerCloudImageDetails(@NotNull final CloudImageParameters imageParameters){
		network = imageParameters.getParameter(SoftlayerCloudConstants.NETWORK);
		profileId = imageParameters.getParameter(SoftlayerCloudConstants.PROFILE_ID);
		sourceId = imageParameters.getParameter(SoftlayerCloudConstants.SOURCE_ID);
	}
	
	String getSourceId() {
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
	
	//int getMaxInstances();
	
	 @Override
	  public String toString() {
		  String str = "Image Details: {\nProfile Id:"+ profileId+",\nNetwork:"+network+",\nSource Id:"+sourceId+
				        "\n}";
		  return str;
	  }
	
}
