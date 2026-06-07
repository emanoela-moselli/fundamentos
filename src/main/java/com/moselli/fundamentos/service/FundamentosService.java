package com.moselli.fundamentos.service;

import com.moselli.fundamentos.client.StatusInvestClient;
import com.moselli.fundamentos.entity.StatusInvestData;
import com.moselli.fundamentos.repository.StatusInvestDataRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Singleton
public class FundamentosService {

    @Inject
    private StatusInvestDataRepository statusInvestDataRepository;

    @Inject
    private StatusInvestClient statusInvestClient;

    public Mono<Void> updateStatusInvestData() {
        return Flux.from(statusInvestClient.fetchData())
                .log()
                .flatMap(statusInvestDataRepository::update)
                .doOnError(e -> log.severe("Failed to reload StatusInvest data: " + e.getMessage()))
                .onErrorResume(e -> Flux.empty())
                .doOnComplete(() -> log.info("Reload complete"))
                .then();
    }

    public Flux<StatusInvestData> findAll() {
        return statusInvestDataRepository.findAll();
    }

}
