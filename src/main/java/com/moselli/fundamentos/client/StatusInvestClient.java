package com.moselli.fundamentos.client;

import com.moselli.fundamentos.config.StatusInvestConfig;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import static io.micronaut.http.HttpHeaders.ACCEPT;

@Client("status-invest")
@Header(name = ACCEPT, value = "application/json")
public interface StatusInvestClient {

    @Get(StatusInvestConfig.STATUS_INVEST_API_URL_ENDPOINT + StatusInvestConfig.STATUS_INVEST_STOCK_API_URL_FILTERS)
    Mono<StatusInvestResponse> fetchStocksData();

    @Get(StatusInvestConfig.STATUS_INVEST_API_URL_ENDPOINT + StatusInvestConfig.STATUS_INVEST_FII_API_URL_FILTERS)
    Mono<StatusInvestResponse> fetchFiiData();
}
