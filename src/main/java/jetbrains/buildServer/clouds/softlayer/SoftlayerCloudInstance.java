package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.InstanceStatus;

public class SoftlayerCloudInstance implements CloudInstance
{
    private InstanceStatus myStatus;

    public SoftlayerCloudInstance()
    {
        myStatus = InstanceStatus.UNKNOWN;
    }

    public InstanceStatus getStatus()
    {
        return myStatus;
    }
}
