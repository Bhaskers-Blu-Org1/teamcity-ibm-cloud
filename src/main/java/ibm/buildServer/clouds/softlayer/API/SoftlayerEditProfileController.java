/*Author: vidhi.shah@ibm.com
 * */
package ibm.buildServer.clouds.softlayer.API;

import static ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants.*;

import ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.TreeMap;
import java.util.List;

import com.softlayer.api.*;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.*;
import com.softlayer.api.service.Location;

//Servlet which will handle requests to and from cloud settings page.
public class SoftlayerEditProfileController extends BaseFormXmlController {

	private PluginDescriptor myDescriptor;
	private ApiClient client;

	public SoftlayerEditProfileController(@NotNull final SBuildServer server,
			@NotNull final WebControllerManager manager, @NotNull final PluginDescriptor descriptor) {
		super(server);
		manager.registerController(descriptor.getPluginResourcesPath(SETTINGS_HTML_PAGE), this);
		myDescriptor = descriptor;
		client = new RestApiClient().withCredentials("vidhi.shah@ibm.com", "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda");
	}

	@Override
	protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		final ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath(SETTINGS_JSP_PAGE));
		mv.getModel().put("imageList", getPrivateImageTemplate());

		return mv;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
		// TODO Auto-generated method stub

	}

	// Get softlayer image template list.
	public TreeMap<Long, String> getPrivateImageTemplate() {
		
		List<Group> imageList = Account.service(client).getPrivateBlockDeviceTemplateGroups();
		TreeMap<Long, String> imageMap = new TreeMap<Long, String>();
		for (Group group : imageList) {
			imageMap.put(group.getId(), group.getName());
		}
		return imageMap;
	}
}