package org.activiti.cloud.connectors.sfdc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SfdcRestClient {

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${sfdc.username}")
	private String sfdcUsername;

	@Value("${sfdc.password}")
	private String sfdcPassword;
	
	@Value("${sfdc.clientId}")
	private String sfdcClientId;
	
	@Value("${sfdc.clientSecret}")
	private String sfdcClientSecret;
	
	@Value("${sfdc.grantType}")
	private String sfdcGrantType;
	
	@Value("${sfdc.oauthTokenUrl}")
	private String sfdcOauthTokenUrl;
	
	@Value("${sfdc.sfdcRestBaseUri}")
	private String sfdcRestBaseUri;
	
	@Value("${sfdc.sfdcDataApiUri}")
	private String sfdcDataApiUri;
	
	private String authorizationToken;

	public String getAuthorizationToken() {
		return this.authorizationToken;
	}

	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		RestTemplate template = builder.build();
		return template;
	}

	public void setAccessToken() {

		HttpHeaders authReqHeaders = new HttpHeaders();
		authReqHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
		requestMap.add("client_id", sfdcClientId);
		requestMap.add("client_secret", sfdcClientSecret);
		requestMap.add("grant_type", sfdcGrantType);
		requestMap.add("username", sfdcUsername);
		requestMap.add("password", sfdcPassword);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestMap,
				authReqHeaders);
		
		JsonNode tokenResponse = restTemplate.postForObject(sfdcOauthTokenUrl, request, JsonNode.class);
		this.authorizationToken = tokenResponse.findValue("access_token").asText();

	}

	public void updateCampaign(Map<String, Object> campaignUpdateRequest, String Id) throws JsonProcessingException {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Bearer " + authorizationToken);
		requestHeaders.set("Content-Type", "application/json");
		requestHeaders.set("Accept", "application/json");

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(sfdcRestBaseUri + sfdcDataApiUri + "/Campaign/" + Id)
				.queryParam("_HttpMethod", "PATCH");
		ObjectMapper objectMapper = new ObjectMapper();
		HttpEntity<String> request = new HttpEntity<String>(objectMapper.writeValueAsString(campaignUpdateRequest),
				requestHeaders);

		restTemplate.postForObject(uriBuilder.build().toUri(), request, JsonNode.class);
	}
}
