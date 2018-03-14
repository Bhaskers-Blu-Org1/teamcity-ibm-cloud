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
import com.softlayer.api.service.virtual.guest.block.device.template.Group;

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
    		mv.getModel().put("MaxMemory", httpServletRequest.getParameter("MaxMemory"));
    		mv.getModel().put("MaxCores", httpServletRequest.getParameter("MaxCores"));
    		mv.getModel().put("DiskType", httpServletRequest.getParameter("DiskType"));
    		mv.getModel().put("network", httpServletRequest.getParameter("network"));
    		
    		String username = "vidhi.shah@ibm.com";
    		String apiKey = "c0e5a3602aa6eb56bce8a575aa975d2cf2b2c40893308bfb736831b1c741beda";
    		ApiClient client = new RestApiClient().withCredentials(username, apiKey);

    		Guest guest = new Guest();
    		guest.setHostname(httpServletRequest.getParameter("agentName"));
    		guest.setDomain("ibmwdtest.com");
    		guest.setStartCpus(Long.parseLong(httpServletRequest.getParameter("MaxCores")));
    		guest.setMaxMemory(Long.parseLong(httpServletRequest.getParameter("MaxMemory")));
    		guest.setHourlyBillingFlag(true);
    		Group blockDevice = new Group();
    		blockDevice.setGlobalIdentifier("955998a7-f5b8-43fa-be8e-75bdae5bf0f5");
    		guest.setBlockDeviceTemplateGroup(blockDevice);
    		//guest.setOperatingSystemReferenceCode("CENTOS_7_64");
    		guest.setLocalDiskFlag(Boolean.valueOf(httpServletRequest.getParameter("DiskType")));
    		guest.setDatacenter(new Location());
    		guest.getDatacenter().setName(httpServletRequest.getParameter("datacenterName"));
    		try{
    			guest = Guest.service(client).createObject(guest);
    			System.out.println(guest.getId());
    		} catch (Exception e)
    	    {
    	        System.out.println("Error: " + e);
    	    }    		
    				
    		//System.out.println("Virtual server ordered with ID: " + guest.getId());
    		mv.getModel().put("vsiId", guest.getId());
    		
    		
        return mv;
    }
}