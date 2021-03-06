package org.activiti.cloud.connectors.sfdc;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessMappingsController {

    // Process Def Id, a hack until start by key is supported
	@Value("${activiti.defaultProcessDefId}")
    private String processId;

    public String getProcessId() {
		return this.processId;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/process-id")
    public String getProcessId(@PathParam("processKey") String processKey) {
        return this.processId;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/process-id")
    public void setProcessId(@RequestBody String processDefId) {
    	this.processId = processDefId;
    }

}
