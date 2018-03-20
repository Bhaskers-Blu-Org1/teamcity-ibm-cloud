package ibm.buildServer.clouds.softlayer.API;
 
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
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

public class CreateVSI extends BaseController {
    private PluginDescriptor myDescriptor;
 
    public CreateVSI (WebControllerManager manager, PluginDescriptor descriptor) {
        manager.registerController("/softlayerCreateVSI.html",this);
        myDescriptor=descriptor;
    }
 
    @Nullable
    @Override
    protected ModelAndView doHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    	
    		
    		final ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("CreateVSI.jsp"));
    		mv.getModel().put("imageName", httpServletRequest.getParameter("imageName"));
    		mv.getModel().put("datacenterName", httpServletRequest.getParameter("datacenterName"));
    		mv.getModel().put("agentName", httpServletRequest.getParameter("agentName"));
    		mv.getModel().put("instanceNumber", httpServletRequest.getParameter("instanceNumber"));
    		mv.getModel().put("machineType", httpServletRequest.getParameter("machineType"));
    		mv.getModel().put("diskType", httpServletRequest.getParameter("diskType"));
    		mv.getModel().put("network", httpServletRequest.getParameter("network"));
    		
    		
    		

    		ApiClient client = new RestApiClient().withCredentials("vidhi.shah@ibm.com", "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda");

    		Guest guest = new Guest();
    		guest.setHostname(httpServletRequest.getParameter("agentName"));
    		guest.setDomain("ibmwdtest.com");
    		guest.setStartCpus(new Long(1));
    		guest.setMaxMemory(new Long(1024));
    		guest.setHourlyBillingFlag(true);
    		guest.setOperatingSystemReferenceCode("CENTOS_7_64");
    		guest.setLocalDiskFlag(false);
    		guest.setDatacenter(new Location());
    		guest.getDatacenter().setName(httpServletRequest.getParameter("datacenterName"));
    		guest = Guest.service(client).createObject(guest);
    		//System.out.println("Virtual server ordered with ID: " + guest.getId());
    		mv.getModel().put("vsiId", guest.getId());
    		
    		
        return mv;
    }
}