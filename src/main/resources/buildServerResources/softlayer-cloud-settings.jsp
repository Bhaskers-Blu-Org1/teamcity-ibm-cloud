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
	<tr>
		<th><label for="${cons.imageList}">Image List:<l:star /></label></th>
		<td><select id="${cons.imageList}" data-id="${cons.imageList}"
			name="prop:${cons.imageList}">
				<c:forEach var="image" items="${imageList}">
					<props:option selected="${image.key eq propertiesBean.properties[cons.imageList]}" value="${image.key}">
						<c:out value="${image.value}" />
					</props:option>
				</c:forEach>
		</select></td>
	</tr>

	<!-- Datacenter list row -->
	<tr>
		<th><label for="${cons.datacenterName}">Datacenter List:</label></th>
		<td><select name="prop:${cons.datacenterName}">
				<option selected="${'dal10' eq propertiesBean.properties[cons.datacenterName]}" value="dal10">DALLAS 10</option>
				<option selected="${'dal12' eq propertiesBean.properties[cons.datacenterName]}" value="dal12">DALLAS 12</option>
				<option selected="${'dal13' eq propertiesBean.properties[cons.datacenterName]}" value="dal13">DALLAS 13</option>

		</select></td>
	</tr>
	<!-- Agent row -->
	<tr>
		<th><label for="IBMSL_agentName">Agent Name:</label></th>
		<td><input type="text" name="IBMSL_agentName" value="" /></td>
	</tr>
	<!-- Number of Instances -->
	<tr>
		<th><label for="IBMSL_instanceNumber">Number of
				Instances:</label></th>
		<td><input type="text" name="IBMSL_instanceNumber" value="" /></td>
	</tr>
	<!-- Machine type: RAM, CORES -->
	<tr>
		<th><label for="IBMSL_MaxMemory">RAM:</label></th>
		<td><select name="IBMSL_MaxMemory">
				<option value="1024">1 GB</option>
				<option value="2048">2 GB</option>
				<option value="4096">4 GB</option>
				<option value="8192">8 GB</option>
				<option value="16384">16 GB</option>
				<option value="32768">32 GB</option>
				<option value="65536">64 GB</option>
		</select></td>
	</tr>
	<tr>
		<th><label for="IBMSL_MaxCores">CPU:</label></th>
		<td><select name="IBMSL_MaxCores">
				<option value="1">1 vCPU</option>
				<option value="2">2 vCPU</option>
				<option value="4">4 vCPU</option>
				<option value="8">8 vCPU</option>
				<option value="16">16 vCPU</option>
				<option value="32">32 vCPU</option>
				<option value="48">64 vCPU</option>
		</select></td>
	</tr>
	<!-- Disk Type -->
	<tr>
		<th><label for="IBMSL_DiskType">DISK:</label></th>
		<td><select name="IBMSL_DiskType">
				<option value="true">LOCAL</option>
				<option value="false">SAN</option>
		</select></td>
	</tr>
	<!-- Network Type -->
	<tr>
		<th><label for="IBMSL_network">Network:</label></th>
		<td><select name="IBMSL_network">
				<option value="10">10 Mbps</option>
				<option value="100">100 Mbps</option>
				<option value="1000">1 Gbps</option>
		</select></td>
	</tr>
</table>
<table class="runnerFormTable">