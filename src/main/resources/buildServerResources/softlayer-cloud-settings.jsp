<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ page import="ibm.buildServer.clouds.softlayer.API.ImageList" %>
<%@ page import="java.util.*" %>
</table>
<script type="text/javascript">
    BS.LoadStyleSheetDynamically("<c:url value=' ${teamcityPluginResourcesPath}softlayer-cloud-settings.css'/>");
</script>
<script type="text/javascript">
    $j.ajax({
        url: "<c:url value="${teamcityPluginResourcesPath}softlayer-cloud-settings.js"/>",
        dataType: "script",
        success: function () {
        },
        cache: true
    });
</script>

<!-- Add Image button -->
<forms:addButton title="Add image" id="softlayerAddImage">Add image</forms:addButton>
<div id="addImageContainer" class="addImageContainer">
  <div class="addImageContainer-content">
  	<div class="headerContainer">
	  	<span class="header">Add Image</span>
	  	<span class="close">&times;</span>
	</div>
  	<table class="runnerFormTable addImageContainerTable">
  	<!-- Image list row -->
		<tr>
			<th><label for="">Image List:</label></th>
		     <td>
			     <select name="imageName">
			    		<% 
			    			HashMap<Long,String> groupMap = ImageList.getPrivateImageTemplate(); 
			    			for (Map.Entry<Long, String> group : groupMap.entrySet()) 
			    			{
				    		    Long key = group.getKey();
				    		    String value = group.getValue();
			    		%>
			    		<option  value="<%=key %>"><%=value %></option>
			    		<%
			    			}
			    		%>
			    </select>
		     </td>
		   </tr>
		   <!-- Datacenter list row -->
		   <tr>
		   <th><label for="">Datacenter List:</label></th>
		    <td>
			     <select name="datacenterName">
			    		<option  value="dal10">DALLAS 10</option>
			    		<option  value="dal12">"DALLAS 12"</option>
			    		<option  value="dal13">DALLAS 13</option>
			    		
			    </select>
		     </td>
		   </tr>
		   <!-- Agent row -->
		   <tr>
		   <th><label for="">Agent Name:</label></th>
		    <td>
			   <input type="text" name="agentName" value="" />
		     </td>
		   </tr>
		    <!-- Number of Instances -->
		   <tr>
		   <th><label for="">Number of Instances:</label></th>
		    <td>
			   <input type="text" name="instanceNumber" value="" />
		     </td>
		   </tr>
		    <!-- Machine type: RAM, CORES, DISK -->
		   <tr>
		   <th>
		   <label for="">Machine Type:</label>
		   </th>
		    <td>
			   <select name="machineType">
			    		<option  value="203977,204145,PRESET215">B1.1x2x25  [vcpu:1 	ram:2 GB 	first disk:25 GB (SAN)]</option>	
			   </select>
		     </td>
		   </tr>
		   <!-- Disk Type -->
		   <tr>
		   <th><label for="">Disk Type:</label></th>
		    <td>
			   <select name="diskType">
			    		<option  value="SAN">SAN</option>
			    </select>
		     </td>
		   </tr>
		    <!-- Network Type -->
		   <tr>
		   <th><label for="">Network:</label></th>
		    <td>
			   <select name="network">
			    		<option  value="10">10</option>
			    		<option  value="100">100</option>
			    </select>
		     </td>
		   </tr>  
	 </table> 
	 
    
	</div>
 </div>

<br>
<br>
