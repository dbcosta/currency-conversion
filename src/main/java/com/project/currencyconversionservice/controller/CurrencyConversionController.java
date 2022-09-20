package com.project.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.project.currencyconversionservice.resource.CurrencyExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.currencyconversionservice.resource.CurrencyConversion;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Slf4j
@RestController
public class CurrencyConversionController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Value("${currency.exchange.url}")
    private String currencyExchangeUrl;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public Mono<CurrencyConversion> retrieveCurrencyConversion(@PathVariable String from,
                                                               @PathVariable String to,
                                                               @PathVariable BigDecimal quantity) {
        log.info("Fetching currency from {} to {} with quantity {}", from, to, quantity);
        Map<String, Object> uriMap = new HashMap<>();
        uriMap.put("from", from);
        uriMap.put("to", to);
        return reactiveCircuitBreakerFactory.create("currency-conversion")
                                            .run(webClientBuilder.build().get()
                                                                 .uri(currencyExchangeUrl, uriMap)
                                                                 .retrieve()
                                                                 .bodyToMono(CurrencyExchange.class)
                                                                 .flatMap(currencyExchange -> {
                                                                     CurrencyConversion currencyConversion =
                                                                             new CurrencyConversion();
                                                                     currencyConversion.setFromCurrencyCode(
                                                                             currencyExchange.getFrom());
                                                                     currencyConversion.setToCurrencyCode(
                                                                             currencyExchange.getTo());
                                                                     currencyConversion.setExchangeRate(
                                                                             currencyExchange.getConversionMultiple());
                                                                     currencyConversion.setQuantity(quantity);
                                                                     currencyConversion.setTotal(
                                                                             currencyExchange.getConversionMultiple()
                                                                                             .multiply(quantity));
                                                                     log.info(
                                                                             "Currency {} to {} with quantity {} and rate {} totals to {}",
                                                                             currencyConversion.getFromCurrencyCode(),
                                                                             currencyConversion.getToCurrencyCode(),
                                                                             currencyConversion.getQuantity(),
                                                                             currencyConversion.getExchangeRate(),
                                                                             currencyConversion.getTotal());
                                                                     return Mono.just(currencyConversion);
                                                                 }),throwable -> Mono.just(new CurrencyConversion()));
    }

}
