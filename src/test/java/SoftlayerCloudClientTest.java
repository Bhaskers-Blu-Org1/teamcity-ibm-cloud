import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudClient;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImage;
import ibm.buildServer.clouds.softlayer.SoftlayerCloudImageDetails;

import jetbrains.buildServer.clouds.*;

class SoftlayerCloudClientTest {
  private CloudClientParameters parameters;
  private SoftlayerCloudClient client;
  private SoftlayerCloudImage image;
  private SoftlayerCloudImageDetails details;

  @BeforeAll
  public void setUp() {
    parameters = new FakeParameters();
  }

  @BeforeEach
  public void assignClientWithImage() {
    SoftlayerCloudClient client = new SoftlayerCloudClient(parameters);
    details = new SoftlayerCloudImageDetails(client.getCloudImages().get(0));
    image = new SoftlayerCloudImage(details);
    client.addImage(image);
  }

  @Test
  @DisplayName("Test if we can add & retrieve images")
  public void addImageTest() {
    Assertions.assertTrue(client.getImages().size() == 1);
  }

  @Test
  @DisplayName("Test findImageById")
  public void findImageByIdTest() {
    String sourceId = details.getSourceId();
    Assertions.assertTrue(client.findImageById(sourceId) == image);
  }

  @Test
  @DisplayName("canStartNewInstance should return true because there are no running instances")
  public void testCanStartNewInstance() {
    Assertions.assertTrue(client.canStartNewInstance(image));
  }
