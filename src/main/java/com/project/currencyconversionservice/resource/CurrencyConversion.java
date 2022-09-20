package com.project.currencyconversionservice.resource;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
public class CurrencyConversion {

	private String fromCurrencyCode;
	private String toCurrencyCode;
	private BigDecimal exchangeRate;
	private BigDecimal quantity;
	private BigDecimal total;
}
