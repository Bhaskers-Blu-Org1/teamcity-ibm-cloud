package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.*;
import jetbrains.buildServer.serverSide.AgentDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;

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
        return null;
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
    		return ((SoftlayerCloudImage)image).startNewInstance(data);
    }
    
    @Nullable
    public SoftlayerCloudInstance findInstanceByAgent(@NotNull final 
            AgentDescription agentDescription)
    {
        return null;
    }
    
    public void restartInstance(@NotNull final CloudInstance instance)
    {
        
    }

    public void terminateInstance(@NotNull final CloudInstance instance)
    {
        
    }
    
    public void dispose()
    {
        
    }

}