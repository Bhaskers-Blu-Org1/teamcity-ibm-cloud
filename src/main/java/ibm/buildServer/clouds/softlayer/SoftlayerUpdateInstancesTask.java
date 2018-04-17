package ibm.buildServer.clouds.softlayer;

import jetbrains.buildServer.log.Loggers;
import com.intellij.openapi.diagnostic.Logger;

import java.util.*;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import jetbrains.buildServer.clouds.InstanceStatus;

public class SoftlayerUpdateInstancesTask implements Runnable {
  private SoftlayerCloudClient client;

  public SoftlayerUpdateInstancesTask(SoftlayerCloudClient client) {
    this.client = client;
  }

  public void run() {
    Logger LOG = Loggers.SERVER;
    Map<InstanceStatus, List<String>> instancesByStatus =
      = new HashMap<InstancesStatus, List<String>>();
    try {
      List<SoftlayerCloudImage> goodImages = new ArrayList<>();
      Collection<SoftlayerCloudImage> images = client.getImages();

