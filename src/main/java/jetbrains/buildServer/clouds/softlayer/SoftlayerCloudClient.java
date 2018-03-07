package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.clouds.base.AbstractCloudClient;
import jetbrains.buildServer.clouds.base.tasks.UpdateInstancesTask;
import jetbrains.buildServer.serverSide.AgentDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftlayerCloudClient implements CloudClientEx
{
    public SoftlayerCloudClient(CloudClientParameters params)
    {
        System.out.println("new client");
    }

    public boolean isInitialized()
    {
        return false;
    }

    public SoftlayerCloudImage findImageById(String imageId)
        throws CloudException
    {
        return null;
    }

    public Collection<? extends CloudImage> getImages() throws CloudException
    {
        return Collections.emptyList();
    }

    public CloudErrorInfo getErrorInfo()
    {
        return new CloudErrorInfo("test");
    }

    public boolean canStartNewInstance(CloudImage image)
    {
        return false;
    }

    public String generateAgentName(AgentDescription agentDescription)
    {
        return "agent name";
    }

    public String generateAgentName(SoftlayerCloudImage image, String instanceId)
    {
        return "name for " + instanceId;
    }

    public CloudInstance startNewInstance(CloudImage image,
        CloudInstanceUserData data)
    {
        return ((LocalCloudImage)image).startNewInstance(data);
    }

    @Nullable
    public SoftlayerCloudInstance findInstanceByAgent(@NotNull final 
            AgentDescription agentDescription)
    {
        SoftlayerCloudImageDetails imageDetails = new SoftlayerCloudImageDetails();
        return new SoftlayerCloudInstance(checkAndCreateImage(imageDetails));
    }

    public void restartInstance(@NotNull final CloudInstance instance)
    {
        ((SoftlayerCloudInstance) instance).restart();
    }

    public void terminateInstance(@NotNull final CloudInstance instance)
    {
        ((LocalCloudInstance) instance).terminate();
    }

    public void dispose()
    {
        System.out.println("dispose");
    }

    @Nullable
    private SoftlayerCloudImage findImage(
            @NotNull final AgentDescription agentDescription)
    {
        final String imageId
            = agentDescription.getConfigurationParameters().get(
                    LocalCloudConstants.IMAGE_ID_PARAM_NAME);
        return imageId == null ? null : findImageById(imageId);
    }

    @Nullable
    private String findInstanceId(
            @NotNull final AgentDescription agentDescription)
    {
        return agentDescription.getConfigurationParameters().get(
                LocalCloudConstants.INSTANCE_ID_PARAM_NAME);
    }
}
