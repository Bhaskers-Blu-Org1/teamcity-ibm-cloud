<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/include.jsp"%>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout"%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms"%>
<%@ taglib prefix="util" uri="/WEB-INF/functions/util"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="intprop" uri="/WEB-INF/functions/intprop"%>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />
<jsp:useBean id="cons" class="ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants"/>
<jsp:useBean id="agentPools" scope="request" type="java.util.Collection<jetbrains.buildServer.serverSide.agentPools.AgentPool>"/>
</table>

<script type="text/javascript">
    BS.LoadStyleSheetDynamically("<c:url value='${teamcityPluginResourcesPath}softlayer-cloud-settings.css'/>");
</script>

<table class="runnerFormTable">

    <!-- Username and API key -->
	<tr>
		<th><label for="${cons.username}">Username:<l:star /></label></th>
		<td><props:textProperty name="${cons.username}" className="settings longField" />
		<span id="error_${cons.username}" class="error"></span>
		</td>
	</tr>

	<tr>
		<th><label for="secure:${cons.apiKey}">API Key:<l:star /></label></th>
		<td><props:passwordProperty name="secure:${cons.apiKey}" className="settings longField" />
		<span id="error_secure:${cons.apiKey}" class="error"></span>
		</td>
	</tr>
	
	<!-- Number of Instances -->
	<tr class="hidden">
		<th><label for="${cons.instanceNumber}">Maximum Instances Count:</label></th>
		<td><props:textProperty name="${cons.instanceNumber}" className="longField"/></td>
	</tr>
	
</table>

<div class="buttonsWrapper">
  <span id="error_fetch_options" class="error"></span>
  <div class="hidden options-loader"><i class="icon-refresh icon-spin"></i>&nbsp;Fetching parameter values from Softlayer...</div>
  <div>
    <forms:button id="softlayerFetchOptionsButton" onclick="BS.IBMSoftlayer.ProfileSettingsForm.checkConnection();">Check connection / Fetch parameter values</forms:button>
  </div>
</div>

<h2 class="noBorder section-header">Agent images</h2>

<div class="buttonsWrapper">
    <div class="imagesTableWrapper hidden">
        <table id="softlayerImagesTable" class="settings imagesTable hidden">
            <tbody>
            <tr>
            		<th class="name">VSI Template</th>
            		<th class="name">Datacenter</th>
            		<th class="name">Agent Name</th>
            		<th class="name">Domain Name</th>
            		<th class="name">RAM</th>
            		<th class="name">CPU</th>
            		<th class="name">Disk</th>
                <th class="name">Network</th>
                <th class="name">Billing</th>
                <th class="name" colspan="2"></th>
            </tr>
            </tbody>
        </table>
         <c:set var="sourceImagesJson" value="${propertiesBean.properties['source_images_json']}"/>
        <input type="hidden" class="jsonParam" name="prop:source_images_json" id="source_images_json" value="<c:out value='${sourceImagesJson}'/>"/>
        <input type="hidden" id="initial_images_list"/>
    </div>
    <forms:addButton title="Add image" id="showAddImageDialogButton">Add image</forms:addButton>
</div>

<bs:dialog dialogId="softlayerImageDialog" title="Add IBM Softlayer Cloud Image" closeCommand="BS.IBMSoftlayer.ImageDialog.close()"
           dialogClass="softlayerImageDialog" titleId="softlayerImageDialogTitle">
    <table class="runnerFormTable paramsTable">
	    	
	    	<!-- Image List row -->
		<tr>
			<th><label for="${cons.vsiTemplateList}">Image List:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.vsiTemplateList}" class="longField" data-id="${cons.vsiTemplateList}">
							<props:option value="">Select Image...</props:option>
					</select>
				</div>
			    <span class="error option-error option-error_${cons.vsiTemplateList}"></span>
			</td>
		</tr>
		
		<!-- Datacenter list row -->
		<tr>
			<th><label for="${cons.datacenterList}">Datacenter List:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.datacenterList}" class="longField" data-id="${cons.datacenterList}">
							<props:option value="">Select Datacenter...</props:option>
					</select>
				</div>
				<span class="error option-error option-error_${cons.datacenterList}"></span>
			</td>
		</tr>
	    		
	    <!-- Agent row -->
		<tr>
			<th><label for="${cons.agentName}">Agent Name:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<input data-id="${cons.agentName}" id="${cons.agentName}" value="${propertiesBean.properties[cons.agentName]}" class="longField" type="text">
				</div>
				<span class="error option-error option-error_${cons.agentName}"></span>
				<div class="smallNoteAttention">
					<ul class="orderedListNote">
	 					<li>Must begin/end with an alphanumeric character.</li>
						<li>Must contain at least one alphabetic character.</li>
 						<li>Can contain non-consecutive '-' dash.</li>
	 					<li>Length between 1-63.</li>
					</ul>
				</div>
			</td>
		</tr>		
		
	    <!-- Domain row -->
		<tr>
			<th><label for="${cons.domainName}">Domain Name:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<input data-id="${cons.domainName}" id="${cons.domainName}" value="${propertiesBean.properties[cons.domainName]}" class="longField" type="text">
				</div>
				<span class="error option-error option-error_${cons.domainName}"></span>
				<div class="smallNoteAttention">
					<ul class="orderedListNote">
	 					<li>Must contain at least 2 labels. A label is an alphanumeric string separated by a period '.'</li>
						<li>Labels must begin/end with an alphanumeric character. Can contain non-consecutive '-' dash.</li>
 						<li>Last label must contain at least one alphabetic character.</li>
	 					<li>Labels must have length between 1-63 and last label length between 2-23.</li>
					</ul>
				</div>
				
			</td>
		</tr>		
		
		<!-- Machine type: RAM, CORES -->
		<tr>
			<th><label for="${cons.maxMemory}">RAM:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.maxMemory}" data-id="${cons.maxMemory}" data-id="${cons.maxMemory}">
							<props:option value="">Select RAM...</props:option>
							<c:forEach var="ram" items="${ramList}">
								<props:option selected="${ram.key eq propertiesBean.properties[cons.maxMemory]}" value="${ram.key}">
									<c:out value="${ram.value}" />
								</props:option>
							</c:forEach>
					</select>
				</div>
				<span class="error option-error option-error_${cons.maxMemory}"></span>
			</td>
		</tr>
	
		<!-- CPU -->
		<tr>
			<th><label for="${cons.maxCores}">CPU:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.maxCores}" data-id="${cons.maxCores}" data-id="${cons.maxCores}">
							<props:option value="">Select Cores...</props:option>
							<c:forEach var="core" items="${coreList}">
								<props:option selected="${core.key eq propertiesBean.properties[cons.maxCores]}" value="${core.key}">
									<c:out value="${core.value}" />
								</props:option>
							</c:forEach>
					</select>
				</div>
				<span class="error option-error option-error_${cons.maxCores}"></span>
			</td>
		</tr>
	
		<!-- Disk Type -->
		<tr>
			<th><label for="${cons.diskType}">Disk:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.diskType}" data-id="${cons.diskType}" data-id="${cons.diskType}">
							<option value="">Select Disk Type...</option>
							<c:forEach var="diskType" items="${diskTypeList}">
								<option value='{"type":"${diskType.value}", "value":"${diskType.key}"}'>${diskType.value} </option>
							</c:forEach>
					</select>
				</div>
				<span class="error option-error option-error_${cons.diskType}"></span>
			</td>
		</tr>	
    		
    		 <tr>
            <th>Network:<l:star/></th>
            <td>
                <div style="white-space: nowrap">
                    <select id="${cons.network}" data-id="${cons.network}" class="longField configParam">
                        <props:option value=""><c:out value="<Please select launch type>"/></props:option>
                        <c:forEach var="network" items="${networkList}">
                            <props:option selected="${network.key eq propertiesBean.properties[cons.network]}" value="${network.key}"><c:out value="${network.value}"/></props:option>
                        </c:forEach>
                    </select>
                </div>
                <span class="error option-error option-error_${cons.network}"></span>
            </td>
        </tr>
        
        	<!-- VSI Billing Type -->
		<tr>
			<th><label for="${cons.vsiBillingType}">Billing:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.vsiBillingType}" data-id="${cons.vsiBillingType}">
							<option value="">Select Billing Type...</option>
							<c:forEach var="billingType" items="${billingTypeList}">
								<option value='{"type":"${billingType.value}", "value":"${billingType.key}"}'>${billingType.value} </option>
							</c:forEach>
					</select>
				</div>
				<span class="error option-error option-error_${cons.vsiBillingType}"></span>
			</td>
		</tr>	
		
				<!-- Maximum Instances row -->
		<tr>
			<th><label for="${cons.maximumInstances}">Maximum Instances:</label></th>
			<td>
				<div style="white-space: nowrap">
					<input data-id="${cons.maximumInstances}" id="${cons.maximumInstances}" value="${propertiesBean.properties[cons.maximumInstances]}" type="text">
				</div>
				<span class="error option-error option-error_${cons.maximumInstances}"></span>
				
			</td>
		</tr>
    	
    		<!-- Agent pool row -->
		<tr>
            	<th><label for="${cons.agentPoolIdField}">Agent pool:&nbsp;<l:star/></label></th>
            	<td>
                	<select id="${cons.agentPoolIdField}" data-id="${cons.agentPoolIdField}">
                    		<props:option value=""><c:out value="<Please select agent pool>"/></props:option>
                    		<c:forEach var="ap" items="${agentPools}">
                        		<props:option selected="${ap.agentPoolId eq propertiesBean.properties['agent_pool_id']}"  value="${ap.agentPoolId}">
                        			<c:out value="${ap.name}"/>
                        		</props:option>
                    		</c:forEach>
                	</select>
                	<span class="error option-error option-error_${cons.agentPoolIdField}"></span>
            	</td>
       	</tr>
	</table>
	
    <admin:showHideAdvancedOpts containerId="softlayerImageDialog" optsKey="softlayerImageSettings"/>
    <admin:highlightChangedFields containerId="softlayerImageDialog"/>
	
	<div class="icon_before icon16 attentionComment clearfix hidden" id="imageChangeMesssage">
		<b>Save the changes.</b></br>
		<i class="greyFont"></i>
	</div>
	
    <div class="popupSaveButtonsBlock">
        <forms:submit label="Add" type="button" id="softlayerAddImageButton"/>
        <forms:button title="Cancel" id="softlayerCancelAddImageButton">Cancel</forms:button>
    </div>
    
</bs:dialog>

<bs:dialog dialogId="softlayerDeleteImageDialog" title="Delete IBM Softlayer Cloud Image" closeCommand="BS.IBMSoftlayer.DeleteImageDialog.close()"
           dialogClass="softlayerDeleteImageDialog" titleId="softlayerDeleteImageDialogTitle">

    <div id="softlayerDeleteImageDialogBody"></div>

    <div class="popupSaveButtonsBlock">
        <forms:submit label="Delete" type="button" id="softlayerDeleteImageButton"/>
        <forms:button title="Cancel" id="softlayerCancelDeleteImageButton">Cancel</forms:button>
        <span class="hidden delete-loader"><i class="icon-refresh icon-spin"></i></span>
    </div>
    
</bs:dialog>

<script type="text/javascript">
	$j.ajax({
				url : "<c:url value="${teamcityPluginResourcesPath}softlayer-cloud-settings.js"/>",
				dataType : "script",
				success : function() {

					BS.IBMSoftlayer.ProfileSettingsForm.checkConnectionUrl = '<c:url value="${softlayerCheckConnectionController}"/>';
					BS.IBMSoftlayer.ProfileSettingsForm.propertiesBeanVsiTemplate = '<c:out value="${propertiesBean.properties[cons.vsiTemplateList]}" />';
					BS.IBMSoftlayer.ProfileSettingsForm.propertiesBeanDatacenter = '<c:out value="${propertiesBean.properties[cons.datacenterList]}" />';
					BS.IBMSoftlayer.DeleteImageDialog.url = '<c:url value="${deleteImageUrl}"/>';
					BS.IBMSoftlayer.ProfileSettingsForm.initialize();
					BS.IBMSoftlayer.ProfileSettingsForm.checkConnection();

				},
				cache : true
			});
</script>

<table class="runnerFormTable hidden">