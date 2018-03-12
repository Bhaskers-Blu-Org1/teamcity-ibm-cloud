package ibm.buildServer.clouds.softlayer;

//imports from jetbrains teamcity api
import jetbrains.buildServer.clouds.CloudClientEx;
import jetbrains.buildServer.clouds.CloudClientFactory;
import jetbrains.buildServer.clouds.CloudClientParameters;
import jetbrains.buildServer.clouds.CloudState;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.clouds.CloudRegistrar;
import jetbrains.buildServer.serverSide.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//imports from java api
import java.util.*;

class SoftlayerCloudClientFactory implements CloudClientFactory, SoftlayerCloudConstants
{

	private PluginDescriptor pluginDescriptor;
	private final String settingPagePath;
	
	public SoftlayerCloudClientFactory(@NotNull CloudRegistrar cloudRegistrar, @NotNull final PluginDescriptor pluginDescriptor){
		
		//Registering plugin with cloud.
		cloudRegistrar.registerCloudFactory(this);
		//plugin's settings page
		settingPagePath = pluginDescriptor.getPluginResourcesPath("softlayer-cloud-settings.jsp");
	}
	
	
	@NotNull
	  public CloudClientEx createNewClient(@NotNull CloudState state, @NotNull CloudClientParameters params)
	  {
		return null;
	  }
	
	 @NotNull
	  public String getCloudCode()
	  {
		 return CLOUD_CODE;
	  }

	  /**
	   * Description to be shown on the web pages
	   * @return display name to be shown to the user
	   */
	  @NotNull
	  public String getDisplayName()
	  {
		  return DISPLAY_NAME;
	  }


	  /**
	   * Properties editor jsp
	   * @return properties editor JSP file
	   * @since 5.1
	   */
	  @Nullable
	  public String getEditProfileUrl()
	  {
		  return settingPagePath;
	  }

	  /**
	   * Return initial values for form parameters.
	   * @return map of initial values
	   */
	  @NotNull
	  public Map<String,String> getInitialParameterValues()
	  {
		  return Collections.emptyMap();
	  }

	  /**
	   * Returns the properties processor instance (validator).
	   *
	   * @return properties processor
	   */
	  @NotNull
	  public PropertiesProcessor getPropertiesProcessor()
	  {
		  return null;
	  }

	  /**
	   * Checks it the agent could be an instance of one of the running profiles.
	   * This method is called to check weather it is needed to open connection
	   * for a cloud profiles of that type to check if the agent is started
	   * from a cloud profiles of that type.
	   * @param description agent info to check
	   * @return true if this agent could be an instance of that cloud type
	   */
	  public boolean canBeAgentOfType(@NotNull AgentDescription description)
	  {
		  return true;
	  }
}
