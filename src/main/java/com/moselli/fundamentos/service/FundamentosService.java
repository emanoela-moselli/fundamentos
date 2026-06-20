package com.moselli.fundamentos.service;

import com.moselli.fundamentos.client.StatusInvestApiItem;
import com.moselli.fundamentos.client.StatusInvestClient;
import com.moselli.fundamentos.entity.FIIData;
import com.moselli.fundamentos.entity.Stock;
import com.moselli.fundamentos.repository.FIIDataRepository;
import com.moselli.fundamentos.repository.StockRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Singleton
public class FundamentosService {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private FIIDataRepository fiiDataRepository;

    @Inject
    private StatusInvestClient statusInvestClient;

    public Mono<Void> updateStocks() {
        return stockRepository.deleteAll()
                .then(statusInvestClient.fetchStocksData())
                .flatMapMany(response -> Flux.fromIterable(response.getList()))
                .map(FundamentosService::toStock)
                .flatMap(stockRepository::save)
                .doOnError(e -> log.severe("Failed to reload stocks data: " + e.getMessage()))
                .onErrorResume(e -> Flux.empty())
                .doOnComplete(() -> log.info("Stocks reload complete"))
                .then();
    }

    public Mono<Void> updateFIIData() {
        return fiiDataRepository.deleteAll()
                .then(statusInvestClient.fetchFiiData())
                .flatMapMany(response -> Flux.fromIterable(response.getList()))
                .map(FundamentosService::toFIIEntity)
                .flatMap(fiiDataRepository::save)
                .doOnError(e -> log.severe("Failed to reload FII data: " + e.getMessage()))
                .onErrorResume(e -> Flux.empty())
                .doOnComplete(() -> log.info("FII reload complete"))
                .then();
    }

    public Flux<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Flux<FIIData> findAllFII() {
        return fiiDataRepository.findAll();
    }

    private static Stock toStock(StatusInvestApiItem item) {
        Stock e = new Stock();
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

    private static FIIData toFIIEntity(StatusInvestApiItem item) {
        FIIData e = new FIIData();
        e.setTicker(item.getTicker());
        e.setCompanyId(item.getCompanyId());
        e.setCompanyName(item.getCompanyName());
        e.setSector(item.getSegment());
        e.setGestao(item.getGestao());
        e.setPrice(item.getPrice());
        e.setDy(item.getDy() != null ? item.getDy() : 0.0);
        e.setPVp(item.getPVp());
        e.setVpa(item.getVpa());
        e.setLastDividend(item.getLastDividend() != null ? item.getLastDividend() : 0.0);
        e.setLiquidezMediaDiaria(item.getLiquidezMediaDiaria() != null ? item.getLiquidezMediaDiaria() : 0.0);
        e.setPercentualEmCaixa(item.getPercentualEmCaixa() != null ? item.getPercentualEmCaixa() : 0.0);
        e.setDividendoCagr3Anos(item.getDividendoCagr3Anos() != null ? item.getDividendoCagr3Anos() : 0.0);
        e.setCotaCagr3Anos(item.getCotaCagr3Anos() != null ? item.getCotaCagr3Anos() : 0.0);
        e.setPatrimonio(item.getPatrimonio());
        e.setNumeroCotistas(item.getNumeroCotistas());
        e.setNumeroCotas(item.getNumeroCotas());
        return e;
    }
}
