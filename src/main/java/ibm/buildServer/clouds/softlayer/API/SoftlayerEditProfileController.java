/*
 * Author: vidhi.shah@ibm.com
 * 
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
		mv.getModel().put("vsiTemplateList", getVsiPrivateTemplate());
		mv.getModel().put("datacenterList", getDatacenters());
		mv.getModel().put("ramList", getMaximumMemory());
		mv.getModel().put("coreList", getMaximumCores());
		mv.getModel().put("diskTypeList", getDiskType());
		mv.getModel().put("networkList", getNetwork());
		

		return mv;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
		// TODO Auto-generated method stub

	}
	
	
	/*
	 * Softlayer API calls
	 * 
	 */
	// Get softlayer image template list.
	public TreeMap<String, String> getVsiPrivateTemplate() {

		List<Group> imageList = Account.service(client).getPrivateBlockDeviceTemplateGroups();
		TreeMap<String, String> imageMap = new TreeMap<String, String>();
		for (Group group : imageList) {
			imageMap.put(group.getGlobalIdentifier(), group.getName());
		}
		return imageMap;
	}

	// Get softlayer datacenter list
	public TreeMap<String, String> getDatacenters() {

		List<Location> datacenterList = Location.service(client).getDatacenters();
		TreeMap<String, String> datacenterMap = new TreeMap<String, String>();
		for (Location datacenter : datacenterList) {
			datacenterMap.put(datacenter.getName(), datacenter.getLongName());
		}
		return datacenterMap;
	}

	// Get softlayer RAM list.
	public TreeMap<Integer, String> getMaximumMemory() {

		TreeMap<Integer, String> maxMemoryMap = new TreeMap<Integer, String>();
		maxMemoryMap.put(1024, "1 GB");
		maxMemoryMap.put(2048, "2 GB");
		maxMemoryMap.put(4096, "4 GB");
		maxMemoryMap.put(8192, "8 GB");
		maxMemoryMap.put(16384, "16 GB");
		maxMemoryMap.put(32768, "32 GB");
		maxMemoryMap.put(65536, "64 GB");

		return maxMemoryMap;
	}

	// Get softlayer Cores list.
	public TreeMap<Integer, String> getMaximumCores() {

		TreeMap<Integer, String> maxCoresMap = new TreeMap<Integer, String>();
		maxCoresMap.put(1, "1 vCPU");
		maxCoresMap.put(2, "2 vCPU");
		maxCoresMap.put(4, "4 vCPU");
		maxCoresMap.put(8, "8 vCPU");
		maxCoresMap.put(16, "16 vCPU");
		maxCoresMap.put(32, "32 vCPU");
		maxCoresMap.put(64, "64 vCPU");

		return maxCoresMap;
	}

	// Get softlayer Disk type list.
	public TreeMap<Boolean, String> getDiskType() {

		TreeMap<Boolean, String> diskType = new TreeMap<Boolean, String>();
		diskType.put(true, "LOCAL");
		diskType.put(false, "SAN");

		return diskType;
	}

	// Get softlayer Network list.
	public TreeMap<Integer, String> getNetwork() {

		TreeMap<Integer, String> network = new TreeMap<Integer, String>();
		network.put(10, "10 Mbps");
		network.put(100, "100 Mbps");
		network.put(1000, "1 Gbps");

		return network;
	}

}