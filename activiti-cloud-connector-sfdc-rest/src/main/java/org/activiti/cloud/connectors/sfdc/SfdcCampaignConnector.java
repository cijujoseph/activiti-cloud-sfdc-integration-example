package org.activiti.cloud.connectors.sfdc;

import java.util.HashMap;
import java.util.Map;
import org.activiti.cloud.connectors.starter.channels.CloudConnectorChannels;
import org.activiti.cloud.connectors.starter.model.IntegrationRequestEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class SfdcCampaignConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(SfdcCampaignConnector.class);

	@Autowired
	private MessageChannel integrationResultsProducer;

	@Autowired
	private SfdcRestClient sfdcRestClient;

	@StreamListener(value = CloudConnectorChannels.INTEGRATION_EVENT_CONSUMER, condition = "headers['connectorType']=='Set Campaign Inactive and Set Status Null'")
	public synchronized void setStatusInactive(IntegrationRequestEvent event) throws InterruptedException {

		Map<String, Object> variables = event.getVariables();
		
		Map<String, Object> campaignUpdateRequest = new HashMap<String, Object>();
		campaignUpdateRequest.put("Status", null);
		campaignUpdateRequest.put("IsActive", false);

		try {
			sfdcRestClient.updateCampaign(campaignUpdateRequest, (String) variables.get("Id"));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.toString());
		}

		Map<String, Object> results = new HashMap<>();
		results.put("sfdcCurrentCampaignStatus", null);
		IntegrationResultEvent ire = new IntegrationResultEvent(event.getExecutionId(), results);

		integrationResultsProducer.send(MessageBuilder.withPayload(ire).build());
	}
	
	@StreamListener(value = CloudConnectorChannels.INTEGRATION_EVENT_CONSUMER, condition = "headers['connectorType']=='Set Campaign Inactive and Set Status Aborted'")
	public synchronized void setStatusAborted(IntegrationRequestEvent event) throws InterruptedException {

		Map<String, Object> variables = event.getVariables();
		
		Map<String, Object> campaignUpdateRequest = new HashMap<String, Object>();
		campaignUpdateRequest.put("Status", "Aborted");
		campaignUpdateRequest.put("IsActive", false);

		try {
			sfdcRestClient.updateCampaign(campaignUpdateRequest, (String) variables.get("Id"));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.toString());
		}

		Map<String, Object> results = new HashMap<>();
		results.put("sfdcCurrentCampaignStatus", "Aborted");
		IntegrationResultEvent ire = new IntegrationResultEvent(event.getExecutionId(), results);

		integrationResultsProducer.send(MessageBuilder.withPayload(ire).build());
	}
	
	@StreamListener(value = CloudConnectorChannels.INTEGRATION_EVENT_CONSUMER, condition = "headers['connectorType']=='Update Campaign and Set Status Planned'")
	public synchronized void setStatusPlanned(IntegrationRequestEvent event) throws InterruptedException {

		Map<String, Object> variables = event.getVariables();
		
		Map<String, Object> campaignUpdateRequest = new HashMap<String, Object>();
		campaignUpdateRequest.put("Status", "Planned");
		campaignUpdateRequest.put("IsActive", true);

		try {
			sfdcRestClient.updateCampaign(campaignUpdateRequest, (String) variables.get("Id"));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.toString());
		}

		Map<String, Object> results = new HashMap<>();
		results.put("sfdcCurrentCampaignStatus", "Planned");
		IntegrationResultEvent ire = new IntegrationResultEvent(event.getExecutionId(), results);

		integrationResultsProducer.send(MessageBuilder.withPayload(ire).build());
	}

	
	@StreamListener(value = CloudConnectorChannels.INTEGRATION_EVENT_CONSUMER, condition = "headers['connectorType']=='Set Campaign Status In-Progress'")
	public synchronized void setStatusInProgress(IntegrationRequestEvent event) throws InterruptedException {

		Map<String, Object> variables = event.getVariables();
		
		Map<String, Object> campaignUpdateRequest = new HashMap<String, Object>();
		campaignUpdateRequest.put("Status", "In Progress");
		campaignUpdateRequest.put("IsActive", true);

		try {
			sfdcRestClient.updateCampaign(campaignUpdateRequest, (String) variables.get("Id"));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.toString());
		}

		Map<String, Object> results = new HashMap<>();
		results.put("sfdcCurrentCampaignStatus", "In Progress");
		IntegrationResultEvent ire = new IntegrationResultEvent(event.getExecutionId(), results);

		integrationResultsProducer.send(MessageBuilder.withPayload(ire).build());
	}

}