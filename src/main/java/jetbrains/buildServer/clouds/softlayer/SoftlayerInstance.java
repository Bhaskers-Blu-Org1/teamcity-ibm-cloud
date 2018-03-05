package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.base.connector.AbstractInstance;
import java.util.Date;
import jetbrains.buildServer.clouds.InstanceStatus;

public class SoftlayerInstance extends AbstractInstance
{
    @Override
    public boolean isInitialized()
    {
        return false;
    }

    @Override
    public Date getStartDate()
    {
        //Return current date.
        return new Date();
    }

    @Override
    public String getIpAddress()
    {
        return "softlayer ip address";
    }

    @Override
    public InstanceStatus getInstanceStatus()
    {
        return InstanceStatus.UNKNOWN;
    }

    @Override
    public String getProperty(String name)
    {
        return null;
    }
}
