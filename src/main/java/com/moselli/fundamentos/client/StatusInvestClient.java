package com.moselli.fundamentos.client;

import com.moselli.fundamentos.config.StatusInvestConfig;
import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

import static io.micronaut.http.HttpHeaders.ACCEPT;

@Client(StatusInvestConfig.STATUS_INVEST_API_URL_PREFIX)
@Header(name = ACCEPT, value = "application/json")
public interface StatusInvestClient {

    @Get(StatusInvestConfig.STATUS_INVEST_API_URL_ENDPOINT+StatusInvestConfig.STATUS_INVEST_API_URL_FILTERS)
    Publisher<StatusInvestData> fetchData();
}
