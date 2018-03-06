package jetbrains.buildServer.clouds.softlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import jetbrains.buildServer.clouds.base.connector.AbstractInstance;
import jetbrains.buildServer.clouds.base.connector.CloudApiConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jetbrains.buildServer.clouds.base.errors.TypedCloudErrorInfo;
import jetbrains.buildServer.clouds.InstanceStatus;

public class SoftlayerApiConnector
    implements CloudApiConnector<SoftlayerCloudImage, SoftlayerCloudInstance>
{
    public SoftlayerApiConnector()
    {
        System.out.println("new connector");
    }

    public void test() throws SoftlayerCheckedCloudException
    {
        System.out.println("this is a test");
    }

    @NotNull
    public InstanceStatus getInstanceStatus(
            @NotNull final SoftlayerCloudInstance instance)
    {
        return InstanceStatus.UNKNOWN;
    }
/*
    @NotNull
    @Override
    public String getKey()
    {
        //The key is meant "to represent the same username and server url/region/instance".
        return "key";
    }

    @Override
    @NotNull
    public <R extends AbstractInstance> Map<String, R> fetchInstances(
            @NotNull final SoftlayerCloudImage image) 
            throws SoftlayerCheckedCloudException
    {
        Map<SoftlayerCloudImage, Map<String, R>> imageMap
            = fetchInstances(Collections.singleton(image));
        Map<String, R> res = imageMap.get(image);
        return res == null ? Collections.emptyMap() : res;
    }

    @Override
    @NotNull
    public <R extends AbstractInstance> Map<SoftlayerCloudImage, Map<String, R>>
            fetchInstances(@NotNull final Collection<SoftlayerCloudImage> images)
            throws SoftlayerCheckedCloudException
    {
        Map<SoftlayerCloudImage, Map<String, R>> result = new HashMap<>();
        return result;
    }
*/
    @NotNull
    public TypedCloudErrorInfo[] checkImage(@NotNull final SoftlayerCloudImage image)
    {
        return new TypedCloudErrorInfo[1];
    }
/* 
    @Override
    public Map<SoftlayerCloudImage, TypedCloudErrorInfo[]> checkImages(
            @NotNull final Collection<SoftlayerCloudImage> images)
    {
        final Map<SoftlayerCloudImage, TypedCloudErrorInfo[]> retval 
            = new HashMap<>();
        return retval;
    }
*/
    @NotNull
    public TypedCloudErrorInfo[] checkInstance(
            @NotNull final SoftlayerCloudInstance instance)
    {
        return new TypedCloudErrorInfo[1];
    }

    @NotNull
    public Map<String, SoftlayerInstance> listImageInstances(
            @NotNull final SoftlayerCloudImage image)
            throws SoftlayerCheckedCloudException
    {
        return Collections.singletonMap("image name", new SoftlayerInstance());
    }
}
