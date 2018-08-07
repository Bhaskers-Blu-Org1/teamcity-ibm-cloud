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


/**
 * Called by Spring. Retrieve metadata, and use it to set agent name, url in build agent configuration.
 */
public class IBMMetadataReader {
  private Logger LOG = Loggers.AGENT;
  private BuildAgentConfigurationEx configuration;

  public IBMMetadataReader(EventDispatcher<AgentLifeCycleListener> events,
      BuildAgentConfigurationEx configuration) {
    this.configuration = configuration;
    events.addListener(new AgentLifeCycleAdapter() {
      @Override
      public void afterAgentConfigurationLoaded(BuildAgent agent) {
        LOG.info("IBM Cloud loading configuration");
        fetchConfiguration();
      }
    });
  }

  /**
   * Retrieves metadata from <a href="https://github.com/softlayer/softlayer-java/blob/master/src/main/java/com/softlayer/api/RestApiClient.java">RestApiClient</a> and passes it to #updateConfiguration(String) to deserialize.
   * @see <a href="https://softlayer.github.io/reference/services/SoftLayer_Resource_Metadata/getUserMetadata/">getUserMetadata()</a>
   */
  private void fetchConfiguration() {
    try {
      String privateEndPoint = "https://api.service.softlayer.com/rest/v3/";
      ApiClient client = new RestApiClient(privateEndPoint);
      Metadata.Service metadataService = Metadata.service(client);
      String metadata = metadataService.getUserMetadata();
      updateConfiguration(metadata, metadataService);
    } catch (Exception e) {
      LOG.warn("IBMMetadataReader error in fetchConfiguration(): " + e);
    }
  }

  /**
   * Retrieve agent name, image name, url from deserialized CloudInstanceUserData, 
   * and set those values in build agent configuration.
   * We have even set some instance's parameters to agent's system properties section.
   * 
   * @param metadata The instance metadata as a serialized string.
   */
  private void updateConfiguration(String metadata, Metadata.Service metadataService) {
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
      String agentName = data.getAgentConfigurationParameter("ibm.instance.name");
      String imageName = data.getAgentConfigurationParameter("ibm.image.id");
      String systemConfig = data.getAgentConfigurationParameter("ibm.system.config");
      String vsiTemplateGlobalId = data.getAgentConfigurationParameter("ibm.image.guid");
      String localHostname = metadataService.getHostname();
      String publicHostname = localHostname + "." + metadataService.getDomain();
      String instanceId = metadataService.getId().toString();
      String publicIp = metadataService.getPrimaryIpAddress();
      String localIp = metadataService.getPrimaryBackendIpAddress();
      
      configuration.setServerUrl(data.getServerAddress());
      configuration.setName(agentName);
      configuration.addConfigurationParameter("ibm.instance.name", agentName);
      configuration.addConfigurationParameter("ibm.image.id", imageName);
      configuration.addSystemProperty("ibm.image.guid", vsiTemplateGlobalId);
      configuration.addSystemProperty("ibm.instance.id", instanceId);
      configuration.addSystemProperty("ibm.local.hostname", localHostname);
      configuration.addSystemProperty("ibm.local.ipv4", localIp);
      configuration.addSystemProperty("ibm.public.hostname", publicHostname);
      configuration.addSystemProperty("ibm.public.ipv4", publicIp);
      configuration.addSystemProperty("ibm.system.config", systemConfig);
      
    } catch (Exception e) {
      LOG.warn("IBMMetadataReader error in updateConfiguration(): " + e);
    }
  }
}
