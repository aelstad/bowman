package uk.co.blackpepper.halclient;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface RestTemplateFactory {

	RestTemplate create(ObjectMapper objectMapper);
}