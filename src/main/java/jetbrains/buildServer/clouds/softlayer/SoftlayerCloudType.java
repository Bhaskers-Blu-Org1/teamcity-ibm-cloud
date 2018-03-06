package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudType;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.AgentDescription;
import java.util.Collection;
import java.util.Collections;

public class SoftlayerCloudType implements CloudType
{
    private String id;
    private String displayName;
    private String jspPath;

    public SoftlayerCloudType(PluginDescriptor pluginDescriptor)
    {
        id = "test";
        displayName = "SoftLayer";
        jspPath = pluginDescriptor.getPluginResourcesPath("settings.jsp");
    }

    public String getCloudCode()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public getEditProfileUrl()
    {
        return jspPath;
    }

    public Map<String, String> getInitialParameterValues()
    {
        return Collections.emptyMap();
    }

    public PropertiesProcessor getPropertiesProcessor()
    {
        return new PropertiesProcessor()
        {
            public Collection<InvalidProperty> process(
                    final Map<String, String> properties)
            {
                return Collections.emptyList();
            }
        };
    }

    public boolean canBeAgentOfType(final AgentDescription agentDescription)
    {
        final Map<String, String> configParams
            = agentDescription.getConfigurationParameters();
        return configParams.containsKey(LocalCloudConstants.IMAGE_ID_PARAM_NAME)
            && configParams.containsKey(
                LocalCloudConstants.INSTANCE_ID_PARAM_NAME);
  }
}
