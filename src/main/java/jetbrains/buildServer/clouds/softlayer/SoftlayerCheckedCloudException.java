package jetbrains.buildServer.clouds.softlayer;

import jetbrains.buildServer.clouds.base.errors.CheckedCloudException;

public class SoftlayerCheckedCloudException extends CheckedCloudException
{
    public SoftlayerCheckedCloudException(final Throwable cause)
    {
        super(cause.getMessage(), cause);
    }

    public SoftlayerCheckedCloudException(final String message)
    {
        super(message);
    }

    public SoftlayerCheckedCloudException(final String message,
            final Throwable cause)
    {
        super(message, cause);
    }
}
