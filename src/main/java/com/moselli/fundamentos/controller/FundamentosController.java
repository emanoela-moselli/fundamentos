package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.model.FII;
import com.moselli.fundamentos.model.Stock;
import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller("/api")
@Log4j2
public class FundamentosController {

    @Inject
    private FundamentosService fundamentosService;

    @Post("/stocks/reload")
    public Mono<String> reloadStocks() {
        return fundamentosService.updateStocks().thenReturn("Stock Reload Complete!");
    }

    @Post("/fiis/reload")
    public Mono<String> reloadFIIs() {
        return fundamentosService.updateFIIData().thenReturn("FII Reload Complete!");
    }

    @Get("/stocks")
    public Mono<List<Stock>> getAllStocks() {
        return fundamentosService.findAll()
                .map(Stock::from)
                .collectList();
    }

    @Get("/fiis")
    public Mono<List<FII>> getAllFIIs() {
        return fundamentosService.findAllFII()
                .map(FII::from)
                .collectList();
    }

}
