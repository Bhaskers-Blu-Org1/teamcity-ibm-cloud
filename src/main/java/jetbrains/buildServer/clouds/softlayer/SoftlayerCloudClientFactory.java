package jetbrains.buildServer.clouds.softlayer

import jetbrains.buildServer.clouds.*
import jetbrains.buildServer.clouds.base.AbstractCloudClientFactory
import jetbrains.buildServer.clouds.base.errors.TypedCloudErrorInfo
import jetbrains.buildServer.serverSide.AgentDescription
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.ServerSettings
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.util.*

class SoftlayerCloudClientFactory 
    extends AbstractCloudClientFactory<SoftlayerCloudImageDetails,
                                       SoftlayerCloudClient>
{
    @NotNull
    @Override
    public SoftlayerCloudClient createNewClient(@NotNull final CloudState state,
            @NotNull final Collection<SoftlayerCloudImageDetails> images,
            @NotNull final CloudClientParameters params)
    {
        return new SoftlayerCloudClient();
    }

    @NotNull
    @Override
    public SoftlayerCloudClient createNewClient(@NotNull final CloudState state,
            @NotNull final CloudClientParameters params,
            final TypedCloudErrorInfo[] profileErrors);
    {
        return new SoftlayerCloudClient();
    }

    @NotNull
    @Override
    public Collection<SoftlayerCloudImageDetails> parseImageData(
            CloudClientParameters params)
    {
        Collection<SoftlayerCloudImageDetails> output
            = new ArrayList<SoftlayerCloudImageDetails>;
        output.add(new SoftlayerCloudImageDetails());
        return output;
    }

    @NotNull
    public String getDisplayName()
    {
        return "SoftLayer";
    }

    @Nullable
    @Override
    protected TypedCloudErrorInfo[] checkClientParams(
            @NotNull final CloudClientParameters params)
    {
        return new TypedCloudErrorInfo[0];
    }
}
