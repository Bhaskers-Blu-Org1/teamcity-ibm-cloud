package jetbrains.buildServer.clouds.softlayer;

import org.jetbrains.annotations.NotNull;

public interface SoftlayerCloudConstants
{
  @NotNull String TYPE = "SoftLayer";
  @NotNull String IMAGES_PROFILE_SETTING = "images";
  @NotNull String IMAGE_ID_PARAM_NAME = "cloud.softlayer.image.id";
  @NotNull String INSTANCE_ID_PARAM_NAME = "cloud.softlayer.instance.id";
}
