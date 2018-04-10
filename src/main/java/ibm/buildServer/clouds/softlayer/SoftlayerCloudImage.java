package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.CloudImage;
import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.CloudErrorInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.io.File;

public class SoftlayerCloudImage implements CloudImage
{
    private boolean myIsReusable;
    private boolean myIsEternalStarting; 
    private File myAgentHomeDir;

    public SoftlayerCloudImage(String imageId, String imageName,
        String agentHomePath, ScheduledExecutorService executor)
    {
        myAgentHomeDir = new File(agentHomePath);
    }

    public boolean isReusable() {
        return false;
    }

    public void setIsReusable(boolean isReusable)
    {
        myIsReusable = isReusable;
    }

    public boolean isEternalStarting()
    {
        return false;
    }

    public void setIsEternalStarting(boolean isEternalStarting)
    {
        myIsEternalStarting = isEternalStarting;
    }

    public void addExtraProperty(@NotNull final String name,
        @NotNull final String value)
    {
        
    }

    @NotNull
    public Map<String, String> getExtraProperties()
    {
        return new HashMap<String, String>();
    }

    @NotNull
    public String getId()
    {
        return "id";
    }

    @NotNull
    public String getName()
    {
        return "name";
    }

    @NotNull
    public File getAgentHomeDir()
    {
        return myAgentHomeDir;
    }

    @NotNull
    public Collection<? extends CloudInstance> getInstances()
    {
        return Collections.emptyList();
    }

    @Nullable
    public SoftlayerCloudInstance findInstanceById(
        @NotNull final String instanceId)
    {
        return null;
    }

    @Nullable
    @Override
    public Integer getAgentPoolId()
    {
        return null;
    }

    @Nullable
    public CloudErrorInfo getErrorInfo()
    {
        return null;
    }

    @NotNull
    public synchronized SoftlayerCloudInstance startNewInstance(
        @NotNull final CloudInstanceUserData data)
    {
        return new SoftlayerCloudInstance();
    }

    protected SoftlayerCloudInstance createInstance(String instanceId)
    {
        return null;
    }


    void forgetInstance(@NotNull final SoftlayerCloudInstance instance)
    {
        
    }

    void dispose()
    {
        
    }
}