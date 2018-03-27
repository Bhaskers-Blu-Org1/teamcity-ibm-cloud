/*Author: vidhi.shah@ibm.com
 * */
package ibm.buildServer.clouds.softlayer;

import ibm.buildServer.clouds.softlayer.API.*;

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

import com.softlayer.api.*;
import com.softlayer.api.ApiClient;
import com.softlayer.api.service.Location;
import com.softlayer.api.service.container.virtual.guest.Configuration;
import com.softlayer.api.service.container.virtual.guest.configuration.Option;
import com.softlayer.api.service.software.component.Password;
import com.softlayer.api.service.virtual.Guest;
import com.softlayer.api.service.virtual.guest.block.Device;

//Servlet which will handle requests to and from cloud settings page.
public class SoftlayerEditProfileController  extends BaseFormXmlController {
    
	private PluginDescriptor myDescriptor;
 
    public SoftlayerEditProfileController (@NotNull final SBuildServer server,@NotNull final WebControllerManager manager,@NotNull final PluginDescriptor descriptor) {
    	 	super(server);
        manager.registerController(descriptor.getPluginResourcesPath("softlayer-cloud-settings.html"),this);
        myDescriptor=descriptor;
    }
 
	@Override
	protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		final ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("softlayer-cloud-settings.jsp"));
		mv.getModel().put("imageList", ImageList.getPrivateImageTemplate());
		
	
    return mv;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
		// TODO Auto-generated method stub
		
	}
}