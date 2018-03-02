package jetbrains.buildServer.clouds.vmware;

import jetbrains.buildServer.clouds.CloudImageParameters;
import jetbrains.buildServer.clouds.base.beans.CloudImageDetails;
import jetbrains.buildServer.clouds.base.types.CloneBehaviour;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftlayerCloudImageDetails implements CloudImageDetails
{

    public SoftlayerCloudImageDetails()
    {
    }

    @NotNull
    public String getSourceId() 
    {
        return "Source ID should be a nickname for this image";
    }

    public int getMaxInstances()
    {
        //This should return the number of instances we're prepared to support.
        return 1;
    }

    public CLoneBehaviour getBehaviour()
    {
        //This is a dummy function for now; eventually it should return 
        //something we can use to clone the image, possibly Packer related.
        return null;
    }
}
