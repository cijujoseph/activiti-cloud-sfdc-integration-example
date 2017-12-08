package org.activiti.cloud.connectors.sfdc;

import org.activiti.cloud.connectors.starter.configuration.EnableActivitiCloudConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableActivitiCloudConnector
@ComponentScan({ "org.activiti.cloud.connectors.starter", "org.activiti.cloud.connectors.sfdc" })
@EnableScheduling
public class SfdcCloudConnectorApp implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SfdcCloudConnectorApp.class);
	
	@Autowired
    private SfdcRestClient sfdcRestClient;

	public static void main(String[] args) {
		SpringApplication.run(SfdcCloudConnectorApp.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		sfdcRestClient.setAccessToken();
	}
	
}
