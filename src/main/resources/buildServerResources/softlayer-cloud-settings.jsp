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
	<tr>
		<th><label for="${cons.instanceNumber}">Maximum Instances Count:</label></th>
		<td><props:textProperty name="${cons.instanceNumber}" className="longField"/></td>
	</tr>
</table>


<div class="buttonsWrapper">
  <span id="error_fetch_options" class="error"></span>
  <div class="hidden options-loader"><i class="icon-refresh icon-spin"></i>&nbsp;Fetching parameter values from Softlayer...</div>
  <div>
    <forms:button id="vmwareFetchOptionsButton" onclick="BS.IBMSoftlayer.ProfileSettingsForm.checkConnection();">Check connection / Fetch parameter values</forms:button>
  </div>
</div>

<table class="runnerFormTable paramsTable">

	<%-- <!-- Image List row -->
	<tr>
		<th><label for="${cons.vsiTemplateList}">Image List:<l:star /></label></th>
		<td><select id="${cons.vsiTemplateList}" class="longField"
			data-id="${cons.vsiTemplateList}" name="prop:${cons.vsiTemplateList}">
				<props:option value="">Select Image...</props:option>
				
			 	<c:forEach var="image" items="${imageList}">
					<props:option
						selected="${image.key eq propertiesBean.properties[cons.imageList]}"
						value="${image.key}">
						<c:out value="${image.value}" />
					</props:option>
				</c:forEach> 
		</select>
		<span id="error_${cons.vsiTemplateList}" class="error"></span>
		</td>
	</tr>
 --%>
	<%-- <!-- Datacenter list row -->
	<tr>
		<th><label for="${cons.datacenterList}">Datacenter List:<l:star /></label></th>
		<td><select id="${cons.datacenterList}" class="longField"
			data-id="${cons.datacenterList}" name="prop:${cons.datacenterList}">
				<props:option value="">Select Datacenter...</props:option>
				<c:forEach var="datacenter" items="${datacenterList}">
					<props:option
						selected="${datacenter.key eq propertiesBean.properties[cons.datacenterName]}"
						value="${datacenter.key}">
						<c:out value="${datacenter.value}" />
					</props:option>
				</c:forEach>
		</select>
		<span id="error_${cons.datacenterList}" class="error"></span>
		</td>
	</tr> --%>

	<%-- <!-- Agent row -->
	<tr>
		<th><label for="${cons.agentName}">Agent Name:<l:star /></label></th>
		<td><input name="prop:${cons.agentName}" id="${cons.agentName}"
			value="${propertiesBean.properties[cons.agentName]}"
			class="longField" type="text">
			<span id="error_${cons.agentName}" class="error"></span>
		</td>
	</tr> --%>


<%-- 	<!-- Machine type: RAM, CORES -->
	<tr>
		<th><label for="${cons.maxMemory}">RAM:<l:star /></label></th>
		<td><select id="${cons.maxMemory}" data-id="${cons.maxMemory}"
			name="prop:${cons.maxMemory}">
				<props:option value="">Select RAM...</props:option>
				<c:forEach var="ram" items="${ramList}">
					<props:option
						selected="${ram.key eq propertiesBean.properties[cons.maxMemory]}"
						value="${ram.key}">
						<c:out value="${ram.value}" />
					</props:option>
				</c:forEach>
		</select>
		<span id="error_${cons.maxMemory}" class="error"></span>
		</td>
	</tr>

	<!-- CPU -->
	<tr>
		<th><label for="${cons.maxCores}">CPU:<l:star /></label></th>
		<td><select id="${cons.maxCores}" data-id="${cons.maxCores}"
			name="prop:${cons.maxCores}">
				<props:option value="">Select Cores...</props:option>
				<c:forEach var="core" items="${coreList}">
					<props:option
						selected="${core.key eq propertiesBean.properties[cons.maxCores]}"
						value="${core.key}">
						<c:out value="${core.value}" />
					</props:option>
				</c:forEach>
		</select>
		<span id="error_${cons.maxCores}" class="error"></span>
		</td>
	</tr>

	<!-- Disk Type -->
	<tr>
		<th><label for="${cons.diskType}">DISK:<l:star /></label></th>
		<td><select id="${cons.diskType}" data-id="${cons.diskType}"
			name="prop:${cons.diskType}">
				<props:option value="">Select Disk Type...</props:option>
				<c:forEach var="diskType" items="${diskTypeList}">
					<props:option
						selected="${diskType.key eq propertiesBean.properties[cons.diskType]}"
						value="${diskType.key}">
						<c:out value="${diskType.value}" />
					</props:option>
				</c:forEach>
		</select>
		<span id="error_${cons.diskType}" class="error"></span>
		</td>
	</tr> --%>

	<%-- <!-- Network Type -->
	<tr>
		<th><label for="${cons.network}">Network:<l:star /></label></th>
		<td><select id="${cons.network}" data-id="${cons.network}"
			name="prop:${cons.network}">
				<props:option value="">Select Network...</props:option>
				<c:forEach var="network" items="${networkList}">
					<props:option
						selected="${network.key eq propertiesBean.properties[cons.network]}"
						value="${network.key}">
						<c:out value="${network.value}" />
					</props:option>
				</c:forEach>
		</select>
		<span id="error_${cons.network}" class="error"></span>
		</td>
	</tr> --%>
</table>











<h2 class="noBorder section-header">Agent images</h2>

<div class="buttonsWrapper">
    <div class="imagesTableWrapper hidden">
        <table id="ecsImagesTable" class="settings imagesTable hidden">
            <tbody>
            <tr>
            		<th class="name">VSI Template</th>
            		<th class="name">Datacenter</th>
            		<th class="name">Agent Name</th>
            		<th class="name">RAM</th>
            		<th class="name">CPU</th>
            		<th class="name">Disk</th>
                <th class="name">Network</th>
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

<bs:dialog dialogId="EcsImageDialog" title="Add Amazon Elastic Container Service Cloud Image" closeCommand="BS.IBMSoftlayer.ImageDialog.close()"
           dialogClass="EcsImageDialog" titleId="EcsImageDialogTitle">
    <table class="runnerFormTable paramsTable">
    
    		
	    		
	    		<!-- Image List row -->
		<tr>
			<th><label for="${cons.vsiTemplateList}">Image List:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.vsiTemplateList}" class="longField"
						data-id="${cons.vsiTemplateList}" name="prop:${cons.vsiTemplateList}">
							<props:option value="">Select Image...</props:option>
							<%-- 
						 	<c:forEach var="image" items="${imageList}">
								<props:option
									selected="${image.key eq propertiesBean.properties[cons.imageList]}"
									value="${image.key}">
									<c:out value="${image.value}" />
								</props:option>
							</c:forEach>  --%>
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
					<select id="${cons.datacenterList}" class="longField"
						data-id="${cons.datacenterList}" name="prop:${cons.datacenterList}">
							<props:option value="">Select Datacenter...</props:option>
							<%-- <c:forEach var="datacenter" items="${datacenterList}">
								<props:option
									selected="${datacenter.key eq propertiesBean.properties[cons.datacenterName]}"
									value="${datacenter.key}">
									<c:out value="${datacenter.value}" />
								</props:option>
							</c:forEach> --%>
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
					<input name="prop:${cons.agentName}" id="${cons.agentName}"
					value="${propertiesBean.properties[cons.agentName]}"
					class="longField" type="text">
				</div>
				<div class="smallNoteAttention">Space and Special character are not allowed.</div>
				<span class="error option-error option-error_${cons.agentName}"></span>
			</td>
		</tr>		
		
		<!-- Machine type: RAM, CORES -->
		<tr>
			<th><label for="${cons.maxMemory}">RAM:<l:star /></label></th>
			<td>
				<div style="white-space: nowrap">
					<select id="${cons.maxMemory}" data-id="${cons.maxMemory}"
						name="prop:${cons.maxMemory}">
							<props:option value="">Select RAM...</props:option>
							<c:forEach var="ram" items="${ramList}">
								<props:option
									selected="${ram.key eq propertiesBean.properties[cons.maxMemory]}"
									value="${ram.key}">
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
					<select id="${cons.maxCores}" data-id="${cons.maxCores}"
						name="prop:${cons.maxCores}">
							<props:option value="">Select Cores...</props:option>
							<c:forEach var="core" items="${coreList}">
								<props:option
									selected="${core.key eq propertiesBean.properties[cons.maxCores]}"
									value="${core.key}">
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
					<select id="${cons.diskType}" data-id="${cons.diskType}"
						name="prop:${cons.diskType}">
							<props:option value="">Select Disk Type...</props:option>
							<c:forEach var="diskType" items="${diskTypeList}">
								<props:option
									selected="${diskType.key eq propertiesBean.properties[cons.diskType]}"
									value="${diskType.key}">
									<c:out value="${diskType.value}" />
								</props:option>
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
        
	</table>

    <admin:showHideAdvancedOpts containerId="EcsImageDialog" optsKey="ecsImageSettings"/>
    <admin:highlightChangedFields containerId="EcsImageDialog"/>

    <div class="popupSaveButtonsBlock">
        <forms:submit label="Add" type="button" id="ecsAddImageButton"/>
        <forms:button title="Cancel" id="ecsCancelAddImageButton">Cancel</forms:button>
    </div>
</bs:dialog>

<bs:dialog dialogId="EcsDeleteImageDialog" title="Delete Amazon ECS Cloud Image" closeCommand="BS.IBMSoftlayer.DeleteImageDialog.close()"
           dialogClass="EcsDeleteImageDialog" titleId="EcsDeleteImageDialogTitle">

    <div id="ecsDeleteImageDialogBody"></div>

    <div class="popupSaveButtonsBlock">
        <forms:submit label="Delete" type="button" id="ecsDeleteImageButton"/>
        <forms:button title="Cancel" id="ecsCancelDeleteImageButton">Cancel</forms:button>
    </div>
</bs:dialog>













<script type="text/javascript">
	$j.ajax({
				url : "<c:url value="${teamcityPluginResourcesPath}softlayer-cloud-settings.js"/>",
				dataType : "script",
				success : function() {

					/*  BS.IBMSoftlayer.ProfileSettingsForm.checkConnectionUrl = '<c:url value="${softlayerCheckConnectionController}"/>';
					 BS.IBMSoftlayer.ProfileSettingsForm.propertiesBeanVsiTemplate = '<c:out value="${propertiesBean.properties[cons.vsiTemplateList]}" />';
					 BS.IBMSoftlayer.ProfileSettingsForm.	propertiesBeanDatacenter = '<c:out value="${propertiesBean.properties[cons.datacenterList]}" />';
					 BS.IBMSoftlayer.ProfileSettingsForm.initialize();
					 BS.IBMSoftlayer.ProfileSettingsForm.checkConnection(); */

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
<table class="runnerFormTable">