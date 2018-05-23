# Build Agent information

## Base Template

###### ID: 1937377  

###### Name:TC_BuildAgent_Latest

The vsis built from this image register to the TC server register with the TC server hardcoded in the buildagent properties file with the line 

###### serverUrl=http://169.44.192.41:8111/

The agent properties and parameters are in the file located in the agent

###### Directory:   /root/BuildAgent/conf
###### Properties file : buildAgent.properties

Once the agent comes up and registers with the Server there is a token exchange and after that the agent is automatically recognized by the agent every time it boots up.
The other properties are the auto_authorize which enables automatic authorization on the TC server and the hostname.
Example of one of the agents below

###### name=IBMTC-buildAgent.ibmwd.com-addr:169.62.68.69
###### auto_authorize=true

These are configurable per user’s need.


