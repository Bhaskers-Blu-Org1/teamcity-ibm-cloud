package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.base.connector.AbstractInstance;
import jetbrains.buildServer.clouds.base.AbstractCloudImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class SoftlayerCloudImage extends AbstractCloudImage<
        SoftlayerCloudInstance, SoftlayerCloudImageDetails>
{
    SoftlayerCloudImage image = new SoftlayerCloudImage("image name", "image id");

    public boolean canStartNewInstance()
    {
        return false;
    }

    public void terminateInstance(@NotNull final SoftlayerCloudInstance instance)
    {
        System.out.println("terminate instance");
    }

    public void restartInstance(@NotNull final SoftlayerCloudInstance instance)
    {
        System.out.println("restart instance");
    }

    public SoftlayerCloudInstance startNewInstance(
            @NotNull final CloudInstanceUserData tag)
    {
        return new SoftlayerCloudInstance(image);
    }

    public SoftlayerCloudImageDetails getImageDetails()
    {
        return new SoftlayerCloudImageDetails();
    }

    protected SoftlayerCloudInstance createInstanceFromReal(
            final AbstractInstance realInstance)
    {
        return new SoftlayerCloudInstance(image);
    }

    public void detectNewInstances(final Map<String,? extends AbstractInstance> realInstances)
    {
        System.out.println("detect new instances");
    }
}
