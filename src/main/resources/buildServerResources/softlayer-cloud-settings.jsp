<%@ page import="ibm.buildServer.clouds.softlayer.ImageList" %>
<%@ page import="java.util.*" %>
<h2> SoftLayer Cloud Access Information </h2>
<table class="runnerFormTable">
  <tr>
    <th><label for="">Image List:</label></th>
    <td>
    <select>
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
</table>