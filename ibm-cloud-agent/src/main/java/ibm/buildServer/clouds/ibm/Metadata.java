/*
* @author: Albert Camacho
* bqecamac@us.ibm.com
**/

package ibm.buildServer.clouds.ibm;

import com.softlayer.api.ApiClient;
import com.softlayer.api.ResponseHandler;
import com.softlayer.api.annotation.ApiMethod;
import com.softlayer.api.annotation.ApiType;
import com.softlayer.api.service.Entity;
import com.softlayer.api.service.container.resource.metadata.ServiceResource;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The Resource Metadata service enables the user to obtain information regarding the resource from which the request originates. Due to the requirement that the request originate from the backend network of the resource, no API key is necessary to perform the request. <br />
 * <br />
 * The primary use of this service is to allow self-discovery for newly provisioned resources, enabling further automated setup by the user. 
 *
 * @see <a href="http://sldn.softlayer.com/reference/datatypes/SoftLayer_Resource_Metadata">SoftLayer_Resource_Metadata</a>
 */
@ApiType("SoftLayer_Resource_Metadata")
public class Metadata extends Entity {

    public static Service service(ApiClient client) {
        return client.createService(Service.class, null);
    }

    /**
     * The Resource Metadata service enables the user to obtain information regarding the resource from which the request originates. Due to the requirement that the request originate from the backend network of the resource, no API key is necessary to perform the request. <br />
     * <br />
     * The primary use of this service is to allow self-discovery for newly provisioned resources, enabling further automated setup by the user. 
     *
     * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata">SoftLayer_Resource_Metadata</a>
     */
    @com.softlayer.api.annotation.ApiService("SoftLayer_Resource_Metadata")
    public static interface Service extends com.softlayer.api.Service {

        public ServiceAsync asAsync();
        public Mask withNewMask();
        public Mask withMask();
        public void setMask(Mask mask);

        /**
         * The getBackendMacAddresses method retrieves a list of backend MAC addresses for the resource
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getBackendMacAddresses">SoftLayer_Resource_Metadata::getBackendMacAddresses</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<String> getBackendMacAddresses();

        /**
         * The getDatacenter method retrieves the name of the datacenter in which the resource is located.
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getDatacenter">SoftLayer_Resource_Metadata::getDatacenter</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getDatacenter();

        /**
         * The getDatacenterId retrieves the ID for the datacenter in which the resource is located.
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getDatacenterId">SoftLayer_Resource_Metadata::getDatacenterId</a>
         */
        @ApiMethod(instanceRequired = false)
        public Long getDatacenterId();

        /**
         * The getDomain method retrieves the hostname for the resource.
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getDomain">SoftLayer_Resource_Metadata::getDomain</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getDomain();

        /**
         * The getFrontendMacAddresses method retrieves a list of frontend MAC addresses for the resource
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getFrontendMacAddresses">SoftLayer_Resource_Metadata::getFrontendMacAddresses</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<String> getFrontendMacAddresses();

        /**
         * The getFullyQualifiedDomainName method provides the user with a combined return which includes the hostname and domain for the resource. Because this method returns multiple pieces of information, it avoids the need to use multiple methods to return the desired information. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getFullyQualifiedDomainName">SoftLayer_Resource_Metadata::getFullyQualifiedDomainName</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getFullyQualifiedDomainName();

        /**
         * The getId getGlobalIdentifier retrieves the globalIdentifier for the resource
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getGlobalIdentifier">SoftLayer_Resource_Metadata::getGlobalIdentifier</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getGlobalIdentifier();

        /**
         * The getHostname method retrieves the hostname for the resource.
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getHostname">SoftLayer_Resource_Metadata::getHostname</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getHostname();

        /**
         * The getId method retrieves the ID for the resource
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getId">SoftLayer_Resource_Metadata::getId</a>
         */
        @ApiMethod(instanceRequired = false)
        public Long getId();

        /**
         * The getPrimaryBackendIpAddress method retrieves the primary backend IP address for the resource
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getPrimaryBackendIpAddress">SoftLayer_Resource_Metadata::getPrimaryBackendIpAddress</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getPrimaryBackendIpAddress();

        /**
         * The getPrimaryIpAddress method retrieves the primary IP address for the resource. For resources with a frontend network, the frontend IP address will be returned. For resources that have been provisioned with only a backend network, the backend IP address will be returned, as a frontend address will not exist. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getPrimaryIpAddress">SoftLayer_Resource_Metadata::getPrimaryIpAddress</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getPrimaryIpAddress();

        /**
         * The getProvisionState method retrieves the provision state of the resource. The provision state may be used to determine when it is considered safe to perform additional setup operations. The method returns 'PROCESSING' to indicate the provision is in progress and 'COMPLETE' when the provision is complete. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getProvisionState">SoftLayer_Resource_Metadata::getProvisionState</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getProvisionState();

        /**
         * The getRouter method will return the router associated with a network component. When the router is redundant, the hostname of the redundant group will be returned, rather than the router hostname. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getRouter">SoftLayer_Resource_Metadata::getRouter</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getRouter(String macAddress);

        /**
         * The getServiceResource method retrieves a specific service resource associated with the resource. Service resources are additional resources that may be used by this resource. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getServiceResource">SoftLayer_Resource_Metadata::getServiceResource</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getServiceResource(String serviceName, Long index);

        /**
         * The getServiceResources method retrieves all service resources associated with the resource. Service resources are additional resources that may be used by this resource. The output format is <type>=<address> for each service resource. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getServiceResources">SoftLayer_Resource_Metadata::getServiceResources</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<ServiceResource> getServiceResources();

        /**
         * The getTags method retrieves all tags associated with the resource. Tags are single keywords assigned to a resource that assist the user in identifying the resource and its roles when performing a simple search. Tags are assigned by any user with access to the resource. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getTags">SoftLayer_Resource_Metadata::getTags</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<String> getTags();

        /**
         * The getUserMetadata method retrieves metadata completed by users who interact with the resource. Metadata gathered using this method is unique to parameters set using the '''setUserMetadata''' method, which must be executed prior to completing this method. User metadata may also be provided while placing an order for a resource. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getUserMetadata">SoftLayer_Resource_Metadata::getUserMetadata</a>
         */
        @ApiMethod(instanceRequired = false)
        public String getUserMetadata();

        /**
         * The getVlanIds method returns a list of VLAN IDs for the network component matching the provided MAC address associated with the resource. For each return, the native VLAN will appear first, followed by any trunked VLANs associated with the network component. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getVlanIds">SoftLayer_Resource_Metadata::getVlanIds</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<Long> getVlanIds(String macAddress);

        /**
         * The getVlans method returns a list of VLAN numbers for the network component matching the provided MAC address associated with the resource. For each return, the native VLAN will appear first, followed by any trunked VLANs associated with the network component. 
         *
         * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Resource_Metadata/getVlans">SoftLayer_Resource_Metadata::getVlans</a>
         */
        @ApiMethod(instanceRequired = false)
        public List<Long> getVlans(String macAddress);

    }

    public static interface ServiceAsync extends com.softlayer.api.ServiceAsync {

        public Mask withNewMask();
        public Mask withMask();
        public void setMask(Mask mask);

        /**
         * Async version of {@link Service#getBackendMacAddresses}
         */
        public Future<List<String>> getBackendMacAddresses();

        public Future<?> getBackendMacAddresses(ResponseHandler<List<String>> callback);

        /**
         * Async version of {@link Service#getDatacenter}
         */
        public Future<String> getDatacenter();

        public Future<?> getDatacenter(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getDatacenterId}
         */
        public Future<Long> getDatacenterId();

        public Future<?> getDatacenterId(ResponseHandler<Long> callback);

        /**
         * Async version of {@link Service#getDomain}
         */
        public Future<String> getDomain();

        public Future<?> getDomain(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getFrontendMacAddresses}
         */
        public Future<List<String>> getFrontendMacAddresses();

        public Future<?> getFrontendMacAddresses(ResponseHandler<List<String>> callback);

        /**
         * Async version of {@link Service#getFullyQualifiedDomainName}
         */
        public Future<String> getFullyQualifiedDomainName();

        public Future<?> getFullyQualifiedDomainName(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getGlobalIdentifier}
         */
        public Future<String> getGlobalIdentifier();

        public Future<?> getGlobalIdentifier(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getHostname}
         */
        public Future<String> getHostname();

        public Future<?> getHostname(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getId}
         */
        public Future<Long> getId();

        public Future<?> getId(ResponseHandler<Long> callback);

        /**
         * Async version of {@link Service#getPrimaryBackendIpAddress}
         */
        public Future<String> getPrimaryBackendIpAddress();

        public Future<?> getPrimaryBackendIpAddress(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getPrimaryIpAddress}
         */
        public Future<String> getPrimaryIpAddress();

        public Future<?> getPrimaryIpAddress(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getProvisionState}
         */
        public Future<String> getProvisionState();

        public Future<?> getProvisionState(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getRouter}
         */
        public Future<String> getRouter(String macAddress);

        public Future<?> getRouter(String macAddress, ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getServiceResource}
         */
        public Future<String> getServiceResource(String serviceName, Long index);

        public Future<?> getServiceResource(String serviceName, Long index, ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getServiceResources}
         */
        public Future<List<ServiceResource>> getServiceResources();

        public Future<?> getServiceResources(ResponseHandler<List<ServiceResource>> callback);

        /**
         * Async version of {@link Service#getTags}
         */
        public Future<List<String>> getTags();

        public Future<?> getTags(ResponseHandler<List<String>> callback);

        /**
         * Async version of {@link Service#getUserMetadata}
         */
        public Future<String> getUserMetadata();

        public Future<?> getUserMetadata(ResponseHandler<String> callback);

        /**
         * Async version of {@link Service#getVlanIds}
         */
        public Future<List<Long>> getVlanIds(String macAddress);

        public Future<?> getVlanIds(String macAddress, ResponseHandler<List<Long>> callback);

        /**
         * Async version of {@link Service#getVlans}
         */
        public Future<List<Long>> getVlans(String macAddress);

        public Future<?> getVlans(String macAddress, ResponseHandler<List<Long>> callback);

    }

    public static class Mask extends com.softlayer.api.service.Entity.Mask {

    }

}
