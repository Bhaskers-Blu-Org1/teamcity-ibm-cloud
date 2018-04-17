package ibm.buildServer.clouds.softlayer;

//imports from jetbrains teamcity api
import jetbrains.buildServer.clouds.CloudClientEx;
import jetbrains.buildServer.clouds.CloudClientFactory;
import jetbrains.buildServer.clouds.CloudClientParameters;
import jetbrains.buildServer.clouds.CloudState;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.clouds.CloudRegistrar;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.clouds.server.CloudManagerBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//imports from java api
import java.util.*;

class SoftlayerCloudClientFactory implements CloudClientFactory {
  private PluginDescriptor pluginDescriptor;
  private final String settingPagePath;
  @NotNull private final CloudManagerBase myCloudManager;

  public SoftlayerCloudClientFactory(
      @NotNull CloudRegistrar cloudRegistrar,
      @NotNull final CloudManagerBase cloudManager,
      @NotNull final PluginDescriptor pluginDescriptor) {
    // Registering plugin with cloud.
    cloudRegistrar.registerCloudFactory(this);
    // Set path to plugin's settings page.
    settingPagePath = pluginDescriptor.getPluginResourcesPath(
        SoftlayerCloudConstants.SETTINGS_HTML_PAGE);
    myCloudManager = cloudManager;
  }

  @NotNull
  public CloudClientEx createNewClient(
      @NotNull CloudState state, @NotNull CloudClientParameters params) {
    SoftLayerCloudClient client = new SoftlayerCloudClient(params);
    SoftlayerCloudImage image = new SoftlayerCloudImage(
        new SoftlayerCloudImageDetails(params));
    image.setCredentials(
        params.getParameter(SoftlayerCloudConstants.USER_NAME),
        params.getParameter(SoftlayerCloudConstants.API_KEY));
    client.getImages().put(image.getName(), image);
    client.start();
    return client;
  }

  @NotNull
  public String getCloudCode() {
    return SoftlayerCloudConstants.CLOUD_CODE;
  }

  /**
  * Description to be shown on the web pages
  * @return display name to be shown to the user
  */
  @NotNull
  public String getDisplayName() {
	return SoftlayerCloudConstants.DISPLAY_NAME;
  }

  /**
  * Properties editor jsp
  * @return properties editor JSP file
  * @since 5.1
  */
  @Nullable
  public String getEditProfileUrl() {
	return settingPagePath;
  }

  /**
  * Return initial values for form parameters.
  * @return map of initial values
  */
  @NotNull
  public Map<String,String> getInitialParameterValues() {
	return Collections.emptyMap();
  }

  /**
  * Returns the properties processor instance (validator).
  *
  * @return properties processor
  */
  @NotNull
  public PropertiesProcessor getPropertiesProcessor() {
    return new SoftlayerCloudPropertiesProcessor(myCloudManager);
  }

  /**
  * Checks it the agent could be an instance of one of the running profiles.
  * This method is called to check weather it is needed to open connection
  * for a cloud profiles of that type to check if the agent is started
  * from a cloud profiles of that type.
  * @param description agent info to check
  * @return true if this agent could be an instance of that cloud type
  */
  public boolean canBeAgentOfType(@NotNull AgentDescription description) {
	return true;
  }
}
