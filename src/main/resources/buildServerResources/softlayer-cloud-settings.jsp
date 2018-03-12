<h2> SoftLayer Cloud Access Information </h2>
<table class="runnerFormTable">
  <tr>
    <th><label for="vcp_server_url">Softlayer URL: <l:star/></label></th>
    <td>
    	  <input type="text" name="vcp_server_url" />
      <span id="error_vcp_server_url" class="error"></span>
    </td>
  </tr>

  <tr>
    <th><label for="vcp_username">Username: <l:star/></label></th>
    <td>
     <input type="text" name="vcp_username" />
      <span id="error_vcp_username" class="error"></span>
    </td>
  </tr>

  <tr>
    <th><label for="secure:vcp_password">Password: <l:star/></label></th>
    <td>
      <input type="password" name="vcp_password" />
      
      <span id="error_secure:vcp_password" class="error"></span>
    </td>
  </tr>
  <tr>
    <th><label for="">Maximum instances count:</label></th>
    <td>
      <props:textProperty name="" />
      <span id="error_" class="error"></span>
      <span class="smallNote">Maximum number of instances that can be started. Use blank to have no limit</span>
    </td>
  </tr>
</table>