package com.project.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.currencyconversionservice.resource.CurrencyConversion;

@RestController
public class CurrencyConversionController {

	/*
	 * @Autowired private CurrencyExchangeProxy currencyExchangeProxy;
	 */
	
	/*
	 * @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	 * public ResponseEntity<CurrencyConversion> convertCurrency(@PathVariable
	 * String from,@PathVariable String to,@PathVariable BigDecimal quantity){
	 * CurrencyConversion currencyConversion =
	 * currencyExchangeProxy.retrieveCurrencyExchange(from, to);
	 * currencyConversion.setTotal(quantity.multiply(currencyConversion.getRate()));
	 * return new
	 * ResponseEntity<CurrencyConversion>(currencyConversion,HttpStatus.OK); }
	 */
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public ResponseEntity<CurrencyConversion> convertCurrency(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity){
		RestTemplate restTemplate = new RestTemplate();
		Map<String,String> uriVariables = new HashMap<>();
		uriVariables.put("from_currency_code", from);
		uriVariables.put("to_currency_code",to);
		ResponseEntity<CurrencyConversion> response = restTemplate.getForEntity("http://localhost:9111/currency-exchange/from_currency_code/{from_currency_code}/to_currency_code/{to_currency_code}", CurrencyConversion.class, uriVariables);
		//CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveCurrencyExchange(from, to);
		CurrencyConversion currencyConversion = response.getBody();
		currencyConversion.setQuantity(quantity);
		currencyConversion.setTotal(quantity.multiply(currencyConversion.getExchangeRate()));
		return new ResponseEntity<CurrencyConversion>(currencyConversion,HttpStatus.OK);
	}
}
