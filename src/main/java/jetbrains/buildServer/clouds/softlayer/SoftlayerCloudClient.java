package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.base.AbstractCloudClient;
import jetbrains.buildServer.clouds.base.tasks.UpdateInstancesTask;
import jetbrains.buildServer.serverSide.AgentDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftlayerCloudClient extends AbstractCloudClient<
        SoftlayerCloudInstance, SoftlayerCloudImage, SoftlayerCloudImageDetails>
{
    @Nullable
    public SoftlayerCloudInstance findInstanceByAgent(@NotNull final 
            AgentDescription agentDescription)
    {
        SoftlayerCloudImageDetails imageDetails = new SoftlayerCloudImageDetails();
        return new SoftlayerCloudInstance(checkAndCreateImage(imageDetails));
    }

    @Override
    protected SoftlayerCloudImage checkAndCreateImage(
            @NotNull final SoftlayerCloudImageDetails imageDetails)
    {
        return new SoftlayerCloudImage();
    }

    @NotNull
    @Override
    protected UpdateInstancesTask<SoftlayerCloudInstance, SoftlayerCloudImage,
              SoftlayerCloudClient> createUpdateInstancesTask()
    {
        SoftlayerUpdateTaskManager manager = new SoftlayerUpdateTaskManager();
        return manager.createUpdateTask(new SoftlayerApiConnector(), this);
    }
}
