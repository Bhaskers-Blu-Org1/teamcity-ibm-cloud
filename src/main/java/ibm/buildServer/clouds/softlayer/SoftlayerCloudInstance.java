package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.CloudErrorInfo;
import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.serverSide.AgentDescription;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

public class SoftlayerCloudInstance implements CloudInstance
{
    private InstanceStatus myStatus;
    private ScheduledExecutorService executor;
    private String id;
    private String name;

    public SoftlayerCloudInstance()
    {
        myStatus = InstanceStatus.UNKNOWN;
        executor = Executors.newSingleThreadScheduledExecutor();
        id = "id";
        name = "name";
    }

    public SoftlayerCloudImage getImage()
    {
        return new SoftlayerCloudImage(id, name, "path", executor);
    }

    public String getImageId()
    {
        return id;
    }

    public String getInstanceId()
    {
        return "instance id";
    }

    public String getName()
    {
        return name;
    }
    
    public String getNetworkIdentity()
    {
        return "dns name";
    }

    public Date getStartedTime()
    {
        return new Date();
    }

    public InstanceStatus getStatus()
    {
        return myStatus;
    }

    public boolean containsAgent(AgentDescription agent)
    {
        return false;
    }

    public CloudErrorInfo getErrorInfo()
    {
        return null;
    }
}