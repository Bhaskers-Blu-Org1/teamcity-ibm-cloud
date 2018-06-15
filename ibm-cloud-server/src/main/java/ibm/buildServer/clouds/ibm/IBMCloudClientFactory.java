/*
* @author: Scott Wyman Neagle, scottwn@ibm.com.
* 
* Purpose of file:
* - TC server will invoke 'IBMCloudClientFactory' which has CloudClientFacoty.
* - Everytime TC server will restart, new 'IBMCloudClientFactory' object will be created.
* - 'IBMCloudClientFactory' provides details on cloud_code, display_name, 
* registering plugin as cloud plugin and setting plugin's setting's page url.
**/

package ibm.buildServer.clouds.ibm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.server.CloudManagerBase;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.log.Loggers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;

public class IBMCloudClientFactory implements CloudClientFactory {
	
  /* 1) pluginDescriptor: USed for setting plugin resource paths like: settings page path. 
   * 2) LOG: TC server log. "<TC root path>/logs/teamcity-server.log" 
   * 3) clients: HashMap to keep track of the created cloud profiles, HashMap Key = ProjectId + ProfileId.
   */
  private PluginDescriptor pluginDescriptor;
  private final String settingPagePath;
  @NotNull
  private final CloudManagerBase myCloudManager;
  private final static Logger LOG = Loggers.SERVER;
  private final Map<String, IBMCloudClient> clients = new ConcurrentHashMap<>();

  /* Constructor. 
   * 1) cloudRegistrar: Used to register plugin in cloud using,'IBMCloudClientFactory' object. 
   * 2) settingPagePath: Set path to plugin's settings page using pluginDescriptor.
   */
  public IBMCloudClientFactory (@NotNull CloudRegistrar cloudRegistrar,
      @NotNull final CloudManagerBase cloudManager, @NotNull final PluginDescriptor pluginDescriptor) {

    cloudRegistrar.registerCloudFactory(this);
    settingPagePath = pluginDescriptor.getPluginResourcesPath(IBMCloudConstants.SETTINGS_HTML_PAGE);
    myCloudManager = cloudManager;
  }

  /* 1) createNewClient(): called when you create/edit new cloud profile. 
   * And, when cloud profile is created/edited, this function will be called for all existing cloud profiles too. 
   * 2) [line: 69] Check HashMap to match existing cloud profiles and add new cloud profiles to HashMap.
   * 3) [line: 83] Adding all images to their cloud profile object 'client'. 
   * 
   * @return just created new client instance.
   */
  @NotNull
  public IBMCloudClient createNewClient(@NotNull CloudState state, 
      @NotNull CloudClientParameters params) {
    
    String clientId = state.getProjectId() + state.getProfileId();
    IBMCloudClient client;
    boolean createdNewClient = false;
    boolean clientHasImage = false;
    
    if (clients.containsKey(clientId)) client = clients.get(clientId); 
    else {
      client = new IBMCloudClient(params);
      clients.put(clientId, client);
      createdNewClient = true;
    }
    
    for (IBMCloudImageDetails imageDetails : parseImageData(params)) {
      
      // Unit Test: Print to the screen during test.
      System.out.println("creating image for " + params.getParameter(IBMCloudConstants.USER_NAME)); 
      IBMCloudImage image = new IBMCloudImage(imageDetails);
      image.setCredentials(params.getParameter(IBMCloudConstants.USER_NAME),
          params.getParameter(IBMCloudConstants.API_KEY));
      client.addImage(image);
      clientHasImage = true;
    }
    
    if (clientHasImage) {
      client.retrieveRunningInstances();
    }
    
    if (createdNewClient) {
      client.start();
    } else {                                                            
      // Restart updateInstancesTask when user updates images.
      client.restartUpdateInstancesTask(params);
    }
    return client;
  }

  /* Gets list of all cloud images defined in corresponding cloud profile.
   * */
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
  public Map<String, String> getInitialParameterValues() {
    return Collections.emptyMap();
  }

  /**
   * Returns the properties processor instance (validator).
   * @return properties processor
   */
  @NotNull
  public PropertiesProcessor getPropertiesProcessor() {
    return new IBMCloudPropertiesProcessor(myCloudManager);
  }

  /**
   * Checks it the agent could be an instance of one of the running profiles. This
   * method is called to check weather it is needed to open connection for a cloud
   * profiles of that type to check if the agent is started from a cloud profiles
   * of that type.
   * @param description agent info to check
   * @return true if this agent could be an instance of that cloud type
   */
  public boolean canBeAgentOfType(@NotNull AgentDescription description) {
    return description.getConfigurationParameters().containsKey("INSTANCE_NAME");
  }
}
