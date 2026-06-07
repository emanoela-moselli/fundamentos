package com.moselli.fundamentos.service;

import com.moselli.fundamentos.client.StatusInvestApiItem;
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
        return statusInvestClient.fetchData()
                .flatMapMany(response -> Flux.fromIterable(response.getList()))
                .map(FundamentosService::toEntity)
                .flatMap(statusInvestDataRepository::save)
                .doOnError(e -> log.severe("Failed to reload StatusInvest data: " + e.getMessage()))
                .onErrorResume(e -> Flux.empty())
                .doOnComplete(() -> log.info("Reload complete"))
                .then();
    }

    public Flux<StatusInvestData> findAll() {
        return statusInvestDataRepository.findAll();
    }

    private static StatusInvestData toEntity(StatusInvestApiItem item) {
        StatusInvestData e = new StatusInvestData();
        e.setTicker(item.getTicker());
        e.setCompanyId(item.getCompanyId());
        e.setCompanyName(item.getCompanyName());
        e.setPrice(item.getPrice());
        e.setDy(item.getDy() != null ? item.getDy() : 0.0);
        e.setPl(item.getPl());
        e.setPVp(item.getPVp());
        e.setPAtivo(item.getPAtivo());
        e.setPEbit(item.getPEbit());
        e.setEvEbit(item.getEvEbit());
        e.setMargemBruta(item.getMargemBruta() != null ? item.getMargemBruta() : 0.0);
        e.setMargemEbit(item.getMargemEbit() != null ? item.getMargemEbit() : 0.0);
        e.setMargemLiquida(item.getMargemLiquida() != null ? item.getMargemLiquida() : 0.0);
        e.setPSr(item.getPSr() != null ? item.getPSr() : 0.0);
        e.setPCapitalGiro(item.getPCapitalGiro() != null ? item.getPCapitalGiro() : 0.0);
        e.setPAtivoCirculante(item.getPAtivoCirculante());
        e.setGiroAtivos(item.getGiroAtivos());
        e.setRoe(item.getRoe());
        e.setRoa(item.getRoa());
        e.setRoic(item.getRoic() != null ? item.getRoic() : 0.0);
        e.setDividaLiquidaPatrimonioLiquido(item.getDividaLiquidaPatrimonioLiquido() != null ? item.getDividaLiquidaPatrimonioLiquido() : 0.0);
        e.setDividaLiquidaEbit(item.getDividaLiquidaEbit() != null ? item.getDividaLiquidaEbit() : 0.0);
        e.setPlAtivo(item.getPlAtivo());
        e.setPassivoAtivo(item.getPassivoAtivo());
        e.setLiquidezCorrente(item.getLiquidezCorrente() != null ? item.getLiquidezCorrente() : 0.0);
        e.setPegRatio(item.getPegRatio() != null ? item.getPegRatio() : 0.0);
        e.setReceitasCagr5(item.getReceitasCagr5() != null ? item.getReceitasCagr5() : 0.0);
        e.setLucrosCagr5(item.getLucrosCagr5() != null ? item.getLucrosCagr5() : 0.0);
        e.setLiquidezMediaDiaria(item.getLiquidezMediaDiaria() != null ? item.getLiquidezMediaDiaria() : 0.0);
        e.setVpa(item.getVpa());
        e.setLpa(item.getLpa());
        e.setValorMercado(item.getValorMercado());
        return e;
    }
}
