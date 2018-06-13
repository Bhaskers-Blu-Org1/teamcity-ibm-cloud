<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/include.jsp"%>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout"%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms"%>
<%@ taglib prefix="util" uri="/WEB-INF/functions/util"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="intprop" uri="/WEB-INF/functions/intprop"%>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />
<jsp:useBean id="cons" class="ibm.buildServer.clouds.ibm.IBMCloudConstants"/>
<jsp:useBean id="agentPools" scope="request" type="java.util.Collection<jetbrains.buildServer.serverSide.agentPools.AgentPool>"/>
</table>

<script type="text/javascript">
    BS.LoadStyleSheetDynamically("<c:url value='${teamcityPluginResourcesPath}ibm-cloud-settings.css'/>");
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
  <div class="hidden options-loader"><i class="icon-refresh icon-spin"></i>&nbsp;Fetching parameter values from IBM Cloud...</div>
  <div>
    <forms:button id="ibmFetchOptionsButton" onclick="BS.IBMCloud.ProfileSettingsForm.checkConnection();">Check connection / Fetch parameter values</forms:button>
  </div>
</div>

<h2 class="noBorder section-header">Agent images</h2>

<div class="buttonsWrapper">
    <div class="imagesTableWrapper hidden">
        <table id="ibmImagesTable" class="settings imagesTable hidden">
            <tbody>
            <tr>
            		<th class="name">VSI Template</th>
            		<th class="name">Datacenter</th>
            		<th class="name">Agent Name</th>
            		<th class="name">Domain Name</th>
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

<bs:dialog dialogId="ibmImageDialog" title="Add IBM Cloud Image" closeCommand="BS.IBMCloud.ImageDialog.close()"
           dialogClass="ibmImageDialog" titleId="ibmImageDialogTitle">
    <table class="runnerFormTable paramsTable">
	    	
	    	<!-- Image List row -->
		<tr>
			<th><label for="${cons.vsiTemplateList}">Image List:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.vsiTemplateList}" class="longField" data-id="${cons.vsiTemplateList}" name="${cons.vsiTemplateList}">
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
					<select id="${cons.datacenterList}" class="longField" data-id="${cons.datacenterList}" name="${cons.datacenterList}">
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
					<input data-id="${cons.agentName}" id="${cons.agentName}" name="${cons.agentName}" value="${propertiesBean.properties[cons.agentName]}" class="longField" type="text">
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
					<input data-id="${cons.domainName}" id="${cons.domainName}" name="${cons.domainName}" value="${propertiesBean.properties[cons.domainName]}" class="longField" type="text">
				</div>
				<span class="error option-error option-error_${cons.domainName}"></span>
				<div class="smallNoteAttention">
					<ul class="orderedListNote">
	 					<li>Must contain at least 2 labels. A label is an alphanumeric string separated by a period '.'</li>
						<li>Labels must begin/end with an alphanumeric character. Can contain non-consecutive '-' dash.</li>
 						<li>Last label must only contain alphabets.</li>
	 					<li>Labels must have length between 1-63 and last label length between 2-23.</li>
					</ul>
				</div>
				
			</td>
		</tr>	
		
		<tr>
			<th>Machine type:</label></th>
			<td>
				<input type="checkbox" id="${cons.customizeMachineType}" data-id="${cons.customizeMachineType}" name="${cons.customizeMachineType}" value=""/>
                 <label for="${cons.customizeMachineType}">Customize machine type</label><br/>
			</td>
		</tr>
		
		<!-- Machine type: Flavor List -->
		<tr class="flavor">
			<th><label for="${cons.flavorList}">Flavor List:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.flavorList}" data-id="${cons.flavorList}" class="mediumField configParam" name="${cons.flavorList}">
							<props:option value="">Select Flavor...</props:option>
							<props:option value="B1_1X2X25">Balanced: 1 CPU, 2 RAM & 25GB HD</props:option>
							<props:option value="B1_1X2X100">Balanced: 1 CPU, 2 RAM & 100GB HD</props:option>
							<props:option value="B1_1X4X25">Balanced: 1 CPU, 4 RAM & 25GB HD</props:option>
							<props:option value="B1_1X4X100">Balanced: 1 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="B1_2X4X25">Balanced: 2 CPU, 4 RAM & 25GB HD</props:option>
							<props:option value="B1_2X4X100">Balanced: 2 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="B1_2X8X25">Balanced: 2 CPU, 8 RAM & 25GB HD</props:option>
							<props:option value="B1_2X8X100">Balanced: 2 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="B1_1X2X25">Balanced: 4 CPU, 8 RAM & 25GB HD</props:option>
							<props:option value="B1_1X2X100">Balanced: 4 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="B1_4X16X25">Balanced: 4 CPU, 4 RAM & 25GB HD</props:option>
							<props:option value="B1_4X16X100">Balanced: 4 CPU, 4 RAM & 25GB HD</props:option>
							<props:option value="B1_8X16X25">Balanced: 8 CPU, 16 RAM & 25GB HD</props:option>
							<props:option value="B1_8X16X100">Balanced: 8 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="B1_8X32X25">Balanced: 8 CPU, 32 RAM & 25GB HD</props:option>
							<props:option value="B1_8X32X100">Balanced: 8 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="B1_16X32X25">Balanced: 16 CPU, 32 RAM & 25GB HD</props:option>
							<props:option value="B1_16X32X100">Balanced: 16 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="B1_16X64X25">Balanced: 16 CPU, 64 RAM & 25GB HD</props:option>
							<props:option value="B1_16X64X100">Balanced: 16 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="B1_32X64X25">Balanced: 32 CPU, 64 RAM & 25GB HD</props:option>
							<props:option value="B1_32X64X100">Balanced: 32 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="B1_32X128X25">Balanced: 32 CPU, 128 RAM & 25GB HD</props:option>
							<props:option value="B1_32X128X100">Balanced: 32 CPU, 128 RAM & 100GB HD</props:option>
							<props:option value="B1_48X192X25">Balanced: 48 CPU, 192 RAM & 25GB HD</props:option>
							<props:option value="B1_48X192X100">Balanced: 48 CPU, 192 RAM & 100GB HD</props:option>
							<props:option value="BL1_1X2X100">Balanced Local HDD: 1 CPU, 2 RAM & 100GB HD</props:option>
							<props:option value="BL1_1X4X100">Balanced Local HDD: 1 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="BL1_2X4X100">Balanced Local HDD: 2 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="BL1_2X8X100">Balanced Local HDD: 2 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="BL1_4X8X100">Balanced Local HDD: 4 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="BL1_4X16X100">Balanced Local HDD: 4 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="BL1_8X16X100">Balanced Local HDD: 8 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="BL1_8X32X100">Balanced Local HDD: 8 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="BL1_16X32X100">Balanced Local HDD: 16 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="BL1_16X64X100">Balanced Local HDD: 16 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="BL1_32X64X100">Balanced Local HDD: 32 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="BL1_32X128X100">Balanced Local HDD: 32 CPU, 128 RAM & 100GB HD</props:option>
							<props:option value="BL1_56X242X100">Balanced Local HDD: 56 CPU, 242 RAM & 100GB HD</props:option>
							<props:option value="BL2_1X2X100">Balanced Local SSD: 1 CPU, 2 RAM & 100GB HD</props:option>
							<props:option value="BL2_1X4X100">Balanced Local SSD: 1 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="BL2_2X4X100">Balanced Local SSD: 2 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="BL2_2X8X100">Balanced Local SSD: 2 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="BL2_4X8X100">Balanced Local SSD: 4 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="BL2_4X16X100">Balanced Local SSD: 4 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="BL2_8X16X100">Balanced Local SSD: 8 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="BL2_8X32X100">Balanced Local SSD: 8 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="BL2_16X32X100">Balanced Local SSD: 16 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="BL2_16X64X100">Balanced Local SSD: 16 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="BL2_32X64X100">Balanced Local SSD: 32 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="BL2_32X128X100">Balanced Local SSD: 32 CPU, 128 RAM & 100GB HD</props:option>
							<props:option value="BL2_56X242X100">Balanced Local SSD: 56 CPU, 242 RAM & 100GB HD</props:option>
							<props:option value="C1_1X1X25">Compute: 1 CPU, 1 RAM & 25GB HD</props:option>
							<props:option value="C1_1X1X100">Compute: 1 CPU, 1 RAM & 100GB HD</props:option>
							<props:option value="C1_2X2X25">Compute: 2 CPU, 2 RAM & 25GB HD</props:option>
							<props:option value="C1_2X2X100">Compute: 2 CPU, 2 RAM & 100GB HD</props:option>
							<props:option value="C1_4X4X25">Compute: 4 CPU, 4 RAM & 25GB HD</props:option>
							<props:option value="C1_4X4X100">Compute: 4 CPU, 4 RAM & 100GB HD</props:option>
							<props:option value="C1_8X8X25">Compute: 8 CPU, 8 RAM & 25GB HD</props:option>
							<props:option value="C1_8X8X100">Compute: 8 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="C1_16X16X25">Compute: 16 CPU, 16 RAM & 25GB HD</props:option>
							<props:option value="C1_16X16X100">Compute: 16 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="C1_32X32X25">Compute: 32 CPU, 32 RAM & 25GB HD</props:option>
							<props:option value="C1_32X32X100">Compute: 32 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="M1_1X8X25">Memory: 1 CPU, 8 RAM & 25GB HD</props:option>
							<props:option value="M1_1X8X100">Memory: 1 CPU, 8 RAM & 100GB HD</props:option>
							<props:option value="M1_2X16X25">Memory: 2 CPU, 16 RAM & 25GB HD</props:option>
							<props:option value="M1_2X16X100">Memory: 2 CPU, 16 RAM & 100GB HD</props:option>
							<props:option value="M1_4X32X25">Memory: 4 CPU, 32 RAM & 25GB HD</props:option>
							<props:option value="M1_4X32X100">Memory: 4 CPU, 32 RAM & 100GB HD</props:option>
							<props:option value="M1_8X64X25">Memory: 8 CPU, 64 RAM & 25GB HD</props:option>
							<props:option value="M1_8X64X100">Memory: 8 CPU, 64 RAM & 100GB HD</props:option>
							<props:option value="M1_16X128X25">Memory: 16 CPU, 128 RAM & 25GB HD</props:option>
							<props:option value="M1_16X128X100">Memory: 16 CPU, 128 RAM & 100GB HD</props:option>
							<props:option value="M1_30X240X25">Memory: 30 CPU, 240 RAM & 25GB HD</props:option>
							<props:option value="M1_30X240X100">Memory: 30 CPU, 240 RAM & 100GB HD</props:option>
							<props:option value="M1_48X384X25">Memory: 48 CPU, 384 RAM & 25GB HD</props:option>
							<props:option value="M1_48X384X100">Memory: 48 CPU, 384 RAM & 100GB HD</props:option>
							<props:option value="M1_56X448X25">Memory: 56 CPU, 448 RAM & 25GB HD</props:option>
							<props:option value="M1_56X448X100">Memory: 56 CPU, 448 RAM & 100GB HD</props:option>
							<props:option value="M1_64X512X25">Memory: 64 CPU, 512 RAM & 25GB HD</props:option>
							<props:option value="M1_64X512X100">Memory: 64 CPU, 512 RAM & 100GB HD</props:option>
							<props:option value="AC1_8X60X25">GPU: 8 CPU, 60 RAM & 25GB HD</props:option>
							<props:option value="AC1_8X60X100">GPU: 8 CPU, 60 RAM & 100GB HD</props:option>
							<props:option value="AC1_16X120X25">GPU: 16 CPU, 120 RAM & 25GB HD</props:option>
							<props:option value="AC1_16X120X100">GPU: 16 CPU, 120 RAM & 100GB HD</props:option>
							<props:option value="ACL1_8X60X100">GPU: 8 CPU, 60 RAM & 100GB HD</props:option>
							<props:option value="ACL1_16X120X100">GPU: 16 CPU, 120 RAM & 100GB HD</props:option>
					</select>
				</div>
				<span class="error option-error option-error_${cons.flavorList}"></span>
			</td>
		</tr>	
		
		<!-- Machine type: RAM, CORES -->
		<tr class="customizeMachine hidden">
			<th><label for="${cons.maxMemory}">RAM:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.maxMemory}" data-id="${cons.maxMemory}" class="mediumField configParam" name="${cons.maxMemory}">
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
		<tr class="customizeMachine hidden">
			<th><label for="${cons.maxCores}">CPU:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.maxCores}" data-id="${cons.maxCores}" class="mediumField configParam" name="${cons.maxCores}">
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
		<tr class="customizeMachine hidden">
			<th><label for="${cons.diskType}">Disk:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.diskType}" data-id="${cons.diskType}" class="mediumField configParam" name="${cons.diskType}">
							<option value="">Select Disk Type...</option>
							<c:forEach var="diskType" items="${diskTypeList}">
								<option value=${diskType.key}">${diskType.value} </option>
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
                    <select id="${cons.network}" data-id="${cons.network}" class="mediumField configParam" name="${cons.network}">
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
					<select id="${cons.vsiBillingType}" data-id="${cons.vsiBillingType}" class="mediumField configParam" name="${cons.vsiBillingType}">
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
					<input data-id="${cons.maximumInstances}" id="${cons.maximumInstances}" value="${propertiesBean.properties[cons.maximumInstances]}" type="text" name="${cons.maximumInstances}">
				</div>
				<span class="error option-error option-error_${cons.maximumInstances}"></span>
				
			</td>
		</tr>
    	
    		<!-- Agent pool row -->
		<tr>
            	<th><label for="${cons.agentPoolIdField}">Agent pool:&nbsp;<l:star/></label></th>
            	<td>
                	<select id="${cons.agentPoolIdField}" data-id="${cons.agentPoolIdField}" class="mediumField configParam" name="${cons.agentPoolIdField}">
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
	
    <admin:showHideAdvancedOpts containerId="ibmImageDialog" optsKey="ibmImageSettings"/>
    <admin:highlightChangedFields containerId="ibmImageDialog"/>
	
	<div class="icon_before icon16 attentionComment clearfix hidden" id="imageChangeMesssage">
		<b>Save the changes.</b></br>
		<i class="greyFont"></i>
	</div>
	
    <div class="popupSaveButtonsBlock">
        <forms:submit label="Add" type="button" id="ibmAddImageButton"/>
        <forms:button title="Cancel" id="ibmCancelAddImageButton">Cancel</forms:button>
    </div>
    
</bs:dialog>

<bs:dialog dialogId="ibmDeleteImageDialog" title="Delete IBM Cloud Image" closeCommand="BS.IBMCloud.DeleteImageDialog.close()"
           dialogClass="ibmDeleteImageDialog" titleId="ibmDeleteImageDialogTitle">

    <div id="ibmDeleteImageDialogBody"></div>

    <div class="popupSaveButtonsBlock">
        <forms:submit label="Delete" type="button" id="ibmDeleteImageButton"/>
        <forms:button title="Cancel" id="ibmCancelDeleteImageButton">Cancel</forms:button>
        <span class="hidden delete-loader"><i class="icon-refresh icon-spin"></i></span>
    </div>
    
</bs:dialog>

<script type="text/javascript">
	$j.ajax({
				url : "<c:url value="${teamcityPluginResourcesPath}ibm-cloud-settings.js"/>",
				dataType : "script",
				success : function() {

					BS.IBMCloud.ProfileSettingsForm.checkConnectionUrl = '<c:url value="${ibmCheckConnectionController}"/>';
					BS.IBMCloud.ProfileSettingsForm.propertiesBeanVsiTemplate = '<c:out value="${propertiesBean.properties[cons.vsiTemplateList]}" />';
					BS.IBMCloud.ProfileSettingsForm.propertiesBeanDatacenter = '<c:out value="${propertiesBean.properties[cons.datacenterList]}" />';
					BS.IBMCloud.DeleteImageDialog.url = '<c:url value="${deleteImageUrl}"/>';
					BS.IBMCloud.ProfileSettingsForm.initialize();
					BS.IBMCloud.ProfileSettingsForm.checkConnection();

				},
				cache : true
			});
</script>

<table class="runnerFormTable hidden">
