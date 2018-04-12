package ibm.buildServer.clouds.softlayer;

public class SoftlayerCloudConstants {
	// SoftlayerCloudClientFactory.java file constants
	public static final String DISPLAY_NAME = "IBM SoftLayer";
	public static final String CLOUD_CODE = "IBMSL";

	// Settings page html/js file name constants
	public static final String SETTINGS_JSP_PAGE = "softlayer-cloud-settings.jsp";
	public static final String SETTINGS_HTML_PAGE = "softlayer-cloud-settings.html";
	public static final String CHECK_CONNECTION_HTML_PAGE = "softlayer-check-connection.html";

	// Settings page field name constants
	public static final String USER_NAME = "IBMSL_username";
	public static final String API_KEY = "IBMSL_apiKey";
	public static final String VSI_TEMPLATE_LIST = "IBMSL_vsiTemplate";
	public static final String DATACENTER_LIST = "IBMSL_datacenter";
	public static final String AGENT_NAME = "IBMSL_agentName";
	public static final String INSTANCE_NUMBER = "IBMSL_instanceNumber";
	public static final String MAX_MEMORY = "IBMSL_MaxMemory";
	public static final String MAX_CORES = "IBMSL_MaxCores";
	public static final String DISK_TYPE = "IBMSL_DiskType";
	public static final String NETWORK = "IBMSL_network";
	public static final String DOMAIN = "IBMSL_Domain";
	
	public static final String SECURE_API_KEY = "secure:"+API_KEY;

	
	public String getUsername() {
		return USER_NAME;
	}

	public String getApiKey() {
		return API_KEY;
	}

	public String getVsiTemplateList() {
		return VSI_TEMPLATE_LIST;
	}

	public String getDatacenterList() {
		return DATACENTER_LIST;
	}

	public String getAgentName() {
		return AGENT_NAME;
	}

	public String getInstanceNumber() {
		return INSTANCE_NUMBER;
	}

	public String getMaxMemory() {
		return MAX_MEMORY;
	}

	public String getMaxCores() {
		return MAX_CORES;
	}

	public String getDiskType() {
		return DISK_TYPE;
	}

	public String getNetwork() {
		return NETWORK;
	}

	public String getDomain() {
		return DOMAIN;
	}

}
