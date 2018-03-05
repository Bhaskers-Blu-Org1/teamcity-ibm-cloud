package jetbrains.buildServer.clouds.softlayer;

import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.clouds.CloudConstants;
import jetbrains.buildServer.clouds.CloudInstanceUserData;
import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.clouds.base.connector.AbstractInstance;
import jetbrains.buildServer.clouds.base.connector.CloudApiConnector;
import jetbrains.buildServer.clouds.vmware.VmwareCloudImage;
import jetbrains.buildServer.clouds.vmware.VmwareCloudInstance;
import jetbrains.buildServer.clouds.vmware.connector.beans.FolderBean;
import jetbrains.buildServer.clouds.vmware.connector.beans.ResourcePoolBean;
import jetbrains.buildServer.clouds.vmware.errors.VmwareCheckedCloudException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SoftlayerApiConnector
    extends CloudApiConnector<SoftlayerCloudImage, SoftlayerCloudInstance>
{
    public SoftlayerApiConnector()
    {
    }

    public void test() throws SoftlayerCheckedCloudException;
    {
    }

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

    @NotNull
    @Deprecated
    TypedCloudErrorInfo[] checkImage(@NotNull final SoftlayerCloudImage image)
    {
        return new TypedCloudErrorInfo[1];
    }
 
    @Override
    public Map<SoftlayerCloudImage, TypedCloudErrorInfo[]> checkImages(
            @NotNull final Collection<SoftlayerCloudImage> images)
    {
        final Map<SoftlayerCloudImage, TypedCloudErrorInfo[]> retval 
            = new HashMap<>();
        return retval;
    }

    @NotNull
    public TypedCloudErrorInfo[] checkInstance(
            @NotNull final SoftlayerCloudInstance instance)
    {
        return new TypedCloudErrorInfo[1];
    }
