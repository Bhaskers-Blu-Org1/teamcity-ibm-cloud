package jetbrains.buildServer.clouds.softlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jetbrains.buildServer.clouds.CloudErrorInfo;
import jetbrains.buildServer.clouds.CloudImage;
import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.base.beans.CloudImageDetails;
import jetbrains.buildServer.clouds.base.connector.AbstractInstance;
import jetbrains.buildServer.clouds.base.errors.CloudErrorMap;
import jetbrains.buildServer.clouds.base.errors.TypedCloudErrorInfo;
import jetbrains.buildServer.clouds.base.errors.UpdatableCloudErrorProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
