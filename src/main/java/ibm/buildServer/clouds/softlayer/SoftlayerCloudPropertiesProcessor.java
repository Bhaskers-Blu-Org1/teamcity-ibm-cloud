package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.jetbrains.annotations.NotNull;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.clouds.server.CloudManagerBase;

import java.util.*;


public class SoftlayerCloudPropertiesProcessor implements PropertiesProcessor {
	@NotNull private final CloudManagerBase myCloudManager;
	
	public SoftlayerCloudPropertiesProcessor(@NotNull final CloudManagerBase cloudManager){
	   myCloudManager = cloudManager;
	  }

	@NotNull
	  public Collection<InvalidProperty> process(final Map<String, String> properties) {
	    List<InvalidProperty> list = new ArrayList<InvalidProperty>();
	    
	    
	    notEmpty(properties,SoftlayerCloudConstants.USER_NAME, list);
	    notEmpty(properties,SoftlayerCloudConstants.SECURE_API_KEY, list);
	    //notEmpty(properties,SoftlayerCloudConstants.VSI_TEMPLATE_LIST, list);
	    //notEmpty(properties,SoftlayerCloudConstants.DATACENTER_LIST, list);
	    //notEmpty(properties,SoftlayerCloudConstants.AGENT_NAME, list);
	    //notEmpty(properties,SoftlayerCloudConstants.INSTANCE_NUMBER, list);
	    //notEmpty(properties,SoftlayerCloudConstants.MAX_MEMORY, list);
	    //notEmpty(properties,SoftlayerCloudConstants.MAX_CORES, list);
	    //notEmpty(properties,SoftlayerCloudConstants.DISK_TYPE, list);
	   // notEmpty(properties,SoftlayerCloudConstants.NETWORK, list);
	  //  notEmpty(properties,SoftlayerCloudConstants.DOMAIN, list);
	    
	    
	    return list;
	}
	
	private void notEmpty(@NotNull final Map<String, String> props, @NotNull final String key,
			@NotNull final Collection<InvalidProperty> col) {
		 if (!props.containsKey(key) || StringUtil.isEmptyOrSpaces(props.get(key))) {
		      col.add(new InvalidProperty(key, "Value should be set"));
		    }
		
	}
}