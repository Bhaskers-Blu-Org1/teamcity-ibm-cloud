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
	    notEmpty(properties,"IBMSL_name", list);
	    notEmpty(properties,"IBMSL_imageName", list);
	    notEmpty(properties,"IBMSL_datacenterName", list);
	    
	    return list;
	}
	private void notEmpty(@NotNull final Map<String, String> props, @NotNull final String key, @NotNull final Collection<InvalidProperty> col) {
		
	}
}