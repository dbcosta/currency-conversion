package com.project.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.currencyconversionservice.ClientCredentials;
import com.project.currencyconversionservice.resource.CurrencyConversion;

@RestController
public class CurrencyConversionController {	
	
	@Value("${currency.exchange.url}")
	private String currencyExchangeUrl;
	
	@Value("${authentication.url}")
	private String authenticationUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ClientCredentials clientCredentials;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public ResponseEntity<CurrencyConversion> convertCurrency(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity){
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer "+getAccessToken(clientCredentials));
		HttpEntity<ClientCredentials> httpEntity = new HttpEntity<ClientCredentials>(httpHeaders);
		Map<String,String> uriVariables = new HashMap<>();
		uriVariables.put("from_currency_code", from);
		uriVariables.put("to_currency_code",to);
		ResponseEntity<CurrencyConversion> response = restTemplate.exchange(currencyExchangeUrl, HttpMethod.GET, httpEntity, CurrencyConversion.class, uriVariables);		
		CurrencyConversion currencyConversion = response.getBody();
		currencyConversion.setQuantity(quantity);
		currencyConversion.setTotal(quantity.multiply(currencyConversion.getExchangeRate()));
		return new ResponseEntity<CurrencyConversion>(currencyConversion,HttpStatus.OK);
	}
	
	private String getAccessToken(ClientCredentials clientCredentials) {
		clientCredentials.setUserName("user1");
		clientCredentials.setPassword("user1");
		HttpEntity<ClientCredentials> httpEntity = new HttpEntity<>(clientCredentials);
		ResponseEntity<String> accessToken = restTemplate.exchange(authenticationUrl, HttpMethod.POST,httpEntity, String.class);
		return accessToken.getBody();
	}
}
