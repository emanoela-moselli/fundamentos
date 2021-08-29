package com.moselli.fundamentos.service;

import com.moselli.fundamentos.client.StatusInvestClient;
import com.moselli.fundamentos.entity.StatusInvestData;
import com.moselli.fundamentos.repository.StatusInvestDataRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log
@Singleton
public class FundamentosService {

    @Inject
    private StatusInvestDataRepository statusInvestDataRepository;

    @Inject
    private StatusInvestClient statusInvestClient;

    public Mono<Void> updateStatusInvestData(){
        return Flux.from(statusInvestClient.fetchData()).log().map(statusInvestDataRepository::update).doOnComplete(() -> log.info("Reload complete")).then();
    }

    public List<StatusInvestData> findAll(){
        return StreamSupport
                .stream(statusInvestDataRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

}
