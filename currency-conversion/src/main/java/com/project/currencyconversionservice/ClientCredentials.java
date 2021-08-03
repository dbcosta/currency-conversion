package com.project.currencyconversionservice;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ClientCredentials {

	private String userName;
	private String password;
}
