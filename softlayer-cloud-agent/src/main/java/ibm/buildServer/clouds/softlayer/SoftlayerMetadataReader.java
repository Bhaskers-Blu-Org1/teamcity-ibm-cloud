/*
* @author: Scott Wyman Neagle 
* scottwn@ibm.com
**/

package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.BuildAgent;

import com.softlayer.api.*;
import com.softlayer.api.service.resource.Metadata;

public class SoftlayerMetadataReader {
  public SoftlayerMetadataReader(EventDispatcher<AgentLifeCycleListener> events) {
    events.addListener(new AgentLifeCycleAdapter() {
      @Override
      public void afterAgentConfigurationLoaded(BuildAgent agent) {
        fetchConfiguration();
      }
    })
  }

  private void fetchConfiguration() {
    ApiClient client = new RestApiClient();
    Metadata.Service metadataService = Metadata.service(client);
    String metadata = metadataService.getUserMetadata();
    updateConfiguration(metadata);
  }

