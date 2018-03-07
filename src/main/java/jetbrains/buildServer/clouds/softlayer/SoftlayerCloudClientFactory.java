package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudClientFactory;
import jetbrains.buildServer.clouds.CloudRegistrar;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SoftlayerCloudClientFactory extends SoftlayerCloudType 
    implements CloudClientFactory
{
    public SoftlayerCloudClientFactory(CloudRegistrar cloudRegistrar,
            PluginDescriptor pluginDescriptor)
    {
        super(pluginDescriptor);
        cloudRegistrar.registerCloudFactory(this);
    }

    public SoftlayerCloudClient createNewClient(CloudState state,
            CloudClientParameters params)
    {
        return new SoftlayerCloudClient(params);
    }
}
