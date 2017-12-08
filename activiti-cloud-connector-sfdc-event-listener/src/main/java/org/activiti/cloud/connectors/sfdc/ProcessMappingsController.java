package org.activiti.cloud.connectors.sfdc;

import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessMappingsController {

    // Set Process Id
    private static String processId;

    public static String getProcessId() {
		return processId;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/process-id")
    public String getProcessId(@PathParam("processKey") String processKey) {
        return processId;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/process-id")
    public void setProcessId(@RequestBody String processDefId) {
    	processId = processDefId;
    }

}