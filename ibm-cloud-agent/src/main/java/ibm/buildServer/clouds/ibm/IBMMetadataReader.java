/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.ibm;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfigurationEx;
import jetbrains.buildServer.clouds.CloudInstanceUserData;

import com.softlayer.api.*;

public class IBMMetadataReader {
  private Logger LOG = Loggers.AGENT;
  private BuildAgentConfigurationEx configuration;

  public IBMMetadataReader(EventDispatcher<AgentLifeCycleListener> events,
      BuildAgentConfigurationEx configuration) {
    LOG.info("IBM Cloud plugin initializing... via MetadataReader");
    this.configuration = configuration;
    events.addListener(new AgentLifeCycleAdapter() {
      @Override
      public void afterAgentConfigurationLoaded(BuildAgent agent) {
        LOG.info("IBM Cloud loading configuration");
        fetchConfiguration();
      }
    });
  }

  private void fetchConfiguration() {
    try {
      String privateEndPoint = "https://api.service.softlayer.com/rest/v3/";
      ApiClient client = new RestApiClient(privateEndPoint);
      Metadata.Service metadataService = Metadata.service(client);
      String metadata = metadataService.getUserMetadata();
      updateConfiguration(metadata);
    } catch (Exception e) {
      LOG.warn("IBMMetadataReader error in fetchConfiguration(): " + e);
    }
  }

  private void updateConfiguration(String metadata) {
    try {
      CloudInstanceUserData data = CloudInstanceUserData.deserialize(metadata);
      if(data == null) {
        LOG.info("IBM Cloud integration is not available; no TeamCity data");
        LOG.debug(metadata);
        return;
      }
      LOG.info(
          "IBM Cloud integration is available; will register " 
          + data.getAgentName()
          + " on server URL "
          + data.getServerAddress());
      String agentName = data.getAgentConfigurationParameter("instance.name");
      String imageName = data.getAgentConfigurationParameter("image.name");
      configuration.setServerUrl(data.getServerAddress());
      configuration.setName(agentName);
      configuration.addConfigurationParameter("name", agentName);
      configuration.addConfigurationParameter("instance.name", agentName);
      configuration.addConfigurationParameter("image.name", imageName);
    } catch (Exception e) {
      LOG.warn("IBMMetadataReader error in updateConfiguration(): " + e);
    }
  }
}
