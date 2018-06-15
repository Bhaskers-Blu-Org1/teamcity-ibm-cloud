/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.server.CloudManagerBase;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jetbrains.buildServer.log.Loggers;
import com.intellij.openapi.diagnostic.Logger;

public class IBMCloudClientFactory implements CloudClientFactory {
  private PluginDescriptor pluginDescriptor;
  private final String settingPagePath;
  @NotNull private final CloudManagerBase myCloudManager;
  private final static Logger LOG = Loggers.SERVER;
  //use hashmap to keep track of the created clients
  private final Map<String, IBMCloudClient> clients =
		    new ConcurrentHashMap<>();

  public IBMCloudClientFactory(
      @NotNull CloudRegistrar cloudRegistrar,
      @NotNull final CloudManagerBase cloudManager,
      @NotNull final PluginDescriptor pluginDescriptor) {
    // Registering plugin with cloud.
    cloudRegistrar.registerCloudFactory(this);
    // Set path to plugin's settings page.
    settingPagePath = pluginDescriptor.getPluginResourcesPath(IBMCloudConstants.SETTINGS_HTML_PAGE);
    myCloudManager = cloudManager;
  }

  @NotNull
  public IBMCloudClient createNewClient(
      @NotNull CloudState state, @NotNull CloudClientParameters params) {
	  String clientId = state.getProjectId() + state.getProfileId();
	  IBMCloudClient client;
	  boolean createdNewClient = false;
      boolean clientHasImage = false;
	  if (clients.containsKey(clientId)) {
		  client = clients.get(clientId);
	  } else {
		  client = new IBMCloudClient(params);
		  clients.put(clientId, client);
		  createdNewClient = true;
	  }
    for(IBMCloudImageDetails imageDetails : parseImageData(params)) {
      // Print to the screen during test; logging has not been implemented in automated
      // unit tests.
      System.out.println("creating image for " + params.getParameter(IBMCloudConstants.USER_NAME));
      IBMCloudImage image = new IBMCloudImage(imageDetails);
      image.setCredentials(params.getParameter(IBMCloudConstants.USER_NAME), 
    		  params.getParameter(IBMCloudConstants.API_KEY));
      client.addImage(image);
      clientHasImage = true;
    }
    if(createdNewClient) {
      // Only retrieve instances when client is created for the first time.
      if(clientHasImage) {
        LOG.info("Checking for running instances on each image for " + clientId);
        client.retrieveRunningInstances();
      }
      client.start();
    } else {
      //The updateInstancesTask stops working when user updates images, so needs to restart.
      client.restartUpdateInstancesTask(params);
    }
    return client;
  }

  public Collection<IBMCloudImageDetails> parseImageData(@NotNull final CloudClientParameters params) {
	return params.getCloudImages().stream().map(IBMCloudImageDetails::new).collect(Collectors.toList());
  }

  @NotNull
  public String getCloudCode() {
    return IBMCloudConstants.CLOUD_CODE;
  }

  /**
  * Description to be shown on the web pages
  * @return display name to be shown to the user
  */
  @NotNull
  public String getDisplayName() {
	return IBMCloudConstants.DISPLAY_NAME;
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
    return new IBMCloudPropertiesProcessor(myCloudManager);
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
	return description.getConfigurationParameters().containsKey("INSTANCE_NAME");
  }
}
