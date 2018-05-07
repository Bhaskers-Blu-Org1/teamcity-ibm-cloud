/*
 * Author: Vidhi.Shah@ibm.com
 * */

package ibm.buildServer.clouds.softlayer.API;

import static ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants.*;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.clouds.CloudClientEx;
import jetbrains.buildServer.clouds.CloudImage;
import jetbrains.buildServer.clouds.CloudInstance;
import jetbrains.buildServer.clouds.server.CloudManagerBase;

// Controller handles delete request for cloud images of cloud profile
public class SoftlayerDeleteCloudImageController extends BaseController {

	private PluginDescriptor myDescriptor;
	private CloudManagerBase myCloudManager;

	public SoftlayerDeleteCloudImageController( @NotNull final WebControllerManager manager, @NotNull final PluginDescriptor descriptor, @NotNull final CloudManagerBase cloudManager) {
		
		myDescriptor = descriptor;
		myCloudManager = cloudManager;
		manager.registerController(myDescriptor.getPluginResourcesPath(DELETE_IMAGE_HTML_PAGE), this);
	}

	@Override
	protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String projectId = request.getParameter("projectId");
		String profileId = request.getParameter("profileId");
		String imageId = request.getParameter("imageId");

		if (StringUtil.isEmpty(imageId))
			return null;
		CloudClientEx client = myCloudManager.getClientIfExistsByProjectExtId(projectId, profileId);
		CloudImage image = client.findImageById(imageId);

		/* 
		 * GET/POST ajax request comming from delete.jsp.
		 * GET: Clicking delete button on cloud image.
		 * POST: Confirming delete of cloud image on delete dialog box.
		 * */
		if (BaseController.isGet(request)) {
			
			// registers model and view to delete jsp page
			ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath(DELETE_IMAGE_JSP_PAGE));
			mv.getModel().put("instances", (image == null) ? Collections.emptyList() : image.getInstances());
			return mv;
			
		} else if (isPost(request) && image != null) {

			Collection<? extends CloudInstance> list = image.getInstances();
			for (CloudInstance instance : list) {
				
				client.terminateInstance(instance);
			}
		}
		return null;
	}
}
