<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/include.jsp"%>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout"%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms"%>
<%@ taglib prefix="util" uri="/WEB-INF/functions/util"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="intprop" uri="/WEB-INF/functions/intprop"%>
<script type="text/javascript">
	$j.getScript("<c:url value="${teamcityPluginResourcesPath}softlayer-cloud-settings.js"/>")
</script>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />
<jsp:useBean id="cons" class="ibm.buildServer.clouds.softlayer.SoftlayerCloudConstants"/>
</table>

<table class="runnerFormTable addImageContainerTable">

    <!-- Username and API key -->
	<tr>
		<th><label for="${cons.username}">Username:<l:star /></label></th>
		<td><props:textProperty name="${cons.username}"
				className="settings longField" /></td>
	</tr>

	<tr>
		<th><label for="secure:${cons.apiKey}">API Key:<l:star /></label></th>
		<td><props:passwordProperty name="secure:${cons.apiKey}"
				className="settings longField" /></td>
	</tr>

	<!-- Image List row -->
	<tr>
		<th><label for="${cons.imageList}">Image List:<l:star /></label></th>
		<td><select id="${cons.imageList}" class="longField"
			data-id="${cons.imageList}" name="prop:${cons.imageList}" required>
				<props:option value="">Select Image...</props:option>
				<c:forEach var="image" items="${imageList}">
					<props:option
						selected="${image.key eq propertiesBean.properties[cons.imageList]}"
						value="${image.key}">
						<c:out value="${image.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- Datacenter list row -->
	<tr>
		<th><label for="${cons.datacenterName}">Datacenter List:<l:star /></label></th>
		<td><select id="${cons.datacenterName}" class="longField"
			data-id="${cons.datacenterName}" name="prop:${cons.datacenterName}"
			required>
				<props:option value="">Select Datacenter...</props:option>
				<c:forEach var="datacenter" items="${datacenterList}">
					<props:option
						selected="${datacenter.key eq propertiesBean.properties[cons.datacenterName]}"
						value="${datacenter.key}">
						<c:out value="${datacenter.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- Agent row -->
	<tr>
		<th><label for="${cons.agentName}">Agent Name:<l:star /></label></th>
		<td><input name="prop:${cons.agentName}" id="${cons.agentName}"
			value="${propertiesBean.properties[cons.agentName]}"
			class="longField" type="text" required></td>
	</tr>

	<!-- Number of Instances -->
	<tr>
		<th><label for="${cons.instanceNumber}">Maximum Instances Count:</label></th>
		<td><props:textProperty name="${cons.instanceNumber}" className="longField"/></td>
	</tr>

	<!-- Machine type: RAM, CORES -->
	<tr>
		<th><label for="${cons.maxMemory}">RAM:<l:star /></label></th>
		<td><select id="${cons.maxMemory}" data-id="${cons.maxMemory}"
			name="prop:${cons.maxMemory}" required>
				<props:option value="">Select RAM...</props:option>
				<c:forEach var="ram" items="${ramList}">
					<props:option
						selected="${ram.key eq propertiesBean.properties[cons.maxMemory]}"
						value="${ram.key}">
						<c:out value="${ram.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- CPU -->
	<tr>
		<th><label for="${cons.maxCores}">CPU:<l:star /></label></th>
		<td><select id="${cons.maxCores}" data-id="${cons.maxCores}"
			name="prop:${cons.maxCores}" required>
				<props:option value="">Select Cores...</props:option>
				<c:forEach var="core" items="${coreList}">
					<props:option
						selected="${core.key eq propertiesBean.properties[cons.maxCores]}"
						value="${core.key}">
						<c:out value="${core.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- Disk Type -->
	<tr>
		<th><label for="${cons.diskType}">DISK:<l:star /></label></th>
		<td><select id="${cons.diskType}" data-id="${cons.diskType}"
			name="prop:${cons.diskType}" required>
				<props:option value="">Select Disk Type...</props:option>
				<c:forEach var="diskType" items="${diskTypeList}">
					<props:option
						selected="${diskType.key eq propertiesBean.properties[cons.diskType]}"
						value="${diskType.key}">
						<c:out value="${diskType.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- Network Type -->
	<tr>
		<th><label for="${cons.network}">Network:<l:star /></label></th>
		<td><select id="${cons.network}" data-id="${cons.network}"
			name="prop:${cons.network}" required>
				<props:option value="">Select Network...</props:option>
				<c:forEach var="network" items="${networkList}">
					<props:option
						selected="${network.key eq propertiesBean.properties[cons.network]}"
						value="${network.key}">
						<c:out value="${network.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>
</table>
<table class="runnerFormTable">