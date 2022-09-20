package com.project.currencyconversionservice.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyConversion {

	private String fromCurrencyCode;
	private String toCurrencyCode;
	private BigDecimal exchangeRate;
	private BigDecimal quantity;
	private BigDecimal total;
}
