package org.activiti.cloud.connectors.sfdc;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.PreDestroy;
import org.activiti.cloud.connectors.starter.configuration.EnableActivitiCloudConnector;
import org.activiti.cloud.services.api.commands.StartProcessInstanceCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.LoginHelper;
import com.salesforce.emp.connector.TopicSubscription;

@SpringBootApplication
@EnableActivitiCloudConnector
@ComponentScan({ "org.activiti.cloud.connectors.starter", "org.activiti.cloud.connectors.sfdc" })
@EnableScheduling
public class SfdcEventListenerCloudConnectorApp implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SfdcEventListenerCloudConnectorApp.class);

	@Autowired
	private MessageChannel runtimeCmdProducer;

	@Autowired
	private ProcessMappingsController processMappingsController;

	@Value("${sfdc.username}")
	private String sfdcUsername;

	@Value("${sfdc.password}")
	private String sfdcPassword;

	@Value("${sfdc.loginurl}")
	private String sfdcLoginUrl;

	@Value("${sfdc.campaignTopic}")
	private String sfdcCampaignTopic;

	@Value("${activiti.processkey.campaignApproval}")
	private String campaignApprovalProcessKey;

	TopicSubscription subscription;
	EmpConnector connector;

	public static void main(String[] args) {
		SpringApplication.run(SfdcEventListenerCloudConnectorApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		long replayFrom = EmpConnector.REPLAY_FROM_TIP;

		BayeuxParameters params = LoginHelper.login(new URL(sfdcLoginUrl), sfdcUsername, sfdcPassword);

		// The event consumer
		Consumer<Map<String, Object>> consumer = event -> startNewProcessInstance(event);

		connector = new EmpConnector(params);

		// Wait for handshake with Streaming API
		connector.start().get(5, TimeUnit.SECONDS);

		// Subscribe to a topic, block and wait for the subscription to succeed
		// for 5 seconds
		subscription = connector.subscribe(sfdcCampaignTopic, replayFrom, consumer).get(5, TimeUnit.SECONDS);

		LOGGER.info(String.format("Subscribed: %s", subscription));
	}

	private void startNewProcessInstance(Map<String, Object> message) {
		LOGGER.info("Received message on channel " + subscription.getTopic() + ": " + message.toString());
		if (((Map<String, Object>) message.get("event")).get("type").equals("created")) {
			StartProcessInstanceCmd startProcessInstanceCmd = new StartProcessInstanceCmd(
					processMappingsController.getProcessId(),
					(Map<String, Object>) message.get("sobject"));
			runtimeCmdProducer.send(MessageBuilder.withPayload(startProcessInstanceCmd).build());
		}

	}

	@PreDestroy
	public void onExit() {
		subscription.cancel();
		connector.stop();
	}
}
