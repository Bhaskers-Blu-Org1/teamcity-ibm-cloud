/*
 * Author: vidhi.shah@ibm.com
 * 
 * */

package ibm.buildServer.clouds.softlayer.API;

import static ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants.*;

import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.controllers.admin.projects.PluginPropertiesUtil;
import jetbrains.buildServer.controllers.ActionErrors;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import com.softlayer.api.*;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.*;
import com.softlayer.api.service.Location;

// Controller handles requests to and from cloud settings page.
public class SoftlayerEditProfileController extends BaseFormXmlController {

	private PluginDescriptor myDescriptor;
	private ApiClient client;

	public SoftlayerEditProfileController(@NotNull final SBuildServer server, @NotNull final WebControllerManager manager, @NotNull final PluginDescriptor descriptor) {
		
		super(server);
		myDescriptor = descriptor;
		manager.registerController(myDescriptor.getPluginResourcesPath(SETTINGS_HTML_PAGE), this);
	}

	@Override
	protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {

		final ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath(SETTINGS_JSP_PAGE));
		mv.getModel().put("softlayerCheckConnectionController", myDescriptor.getPluginResourcesPath(SETTINGS_HTML_PAGE));
		mv.getModel().put("deleteImageUrl", myDescriptor.getPluginResourcesPath(DELETE_IMAGE_HTML_PAGE));
		mv.getModel().put("ramList", getMaximumMemory());
		mv.getModel().put("coreList", getMaximumCores());
		mv.getModel().put("diskTypeList", getDiskType());
		mv.getModel().put("networkList", getNetwork());
		return mv;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
		// Check connection ajax request is handled here. 
		
		final ActionErrors errors = new ActionErrors();
		final BasePropertiesBean propsBean = new BasePropertiesBean(null);
		PluginPropertiesUtil.bindPropertiesFromRequest(request, propsBean, true);
		final Map<String, String> props = propsBean.getProperties();
		
		//Fetch parameters from settings page ajax request
		String username = props.get("IBMSL_username");
		String apiKey = props.get("secure:IBMSL_apiKey");
		client = new RestApiClient().withCredentials(username, apiKey);
		
		try
		{
			// Vsi Private template lists in xml response
			xmlResponse.addContent(getVsiTemplatesAsElement());

			// Datacenter lists in xml response
			xmlResponse.addContent(getDatacenters());
		}
		catch(Exception e)
		{
		    errors.addError("errorFetchResults", e.toString());
		}
		writeErrors(xmlResponse, errors);

	}
	
	
	/*
	 * Softlayer API calls
	 * 
	 */
	// Get softlayer image template list.
	public Element getVsiTemplatesAsElement() {

		List<Group> imageList = Account.service(client).getPrivateBlockDeviceTemplateGroups();
		final Element vsiPrivateTemplateListElement = new Element("VsiPrivateTemplateList");
		for (Group group : imageList) {
			Element vsiPrivateTemplateElement = new Element("VsiPrivateTemplate");
			String id = group.getGlobalIdentifier();
			String name = group.getName();
			vsiPrivateTemplateElement.setAttribute("id", id);
			vsiPrivateTemplateElement.setAttribute("name", name);
			vsiPrivateTemplateListElement.addContent(vsiPrivateTemplateElement);
		}
		return vsiPrivateTemplateListElement;
	}

	// Get softlayer datacenter list
	public Element getDatacenters() {

		List<Location> datacenterList = Location.service(client).getDatacenters();
		final Element DatacenterListElement = new Element("DatacenterList");
		for (Location datacenter : datacenterList) {
			Element datacenterElement = new Element("Datacenter");
			String name = datacenter.getName();
			String longName = datacenter.getLongName();
			datacenterElement.setAttribute("name", name);
			datacenterElement.setAttribute("longName", longName);
			DatacenterListElement.addContent(datacenterElement);
		}
		return DatacenterListElement;
		
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