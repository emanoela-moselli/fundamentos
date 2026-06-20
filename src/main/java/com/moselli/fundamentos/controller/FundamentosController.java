package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.entity.StatusInvestData;
import com.moselli.fundamentos.model.FII;
import com.moselli.fundamentos.model.Fundamento;
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

    @Post("/reload")
    public Mono<String> reload() {
        return fundamentosService.updateStatusInvestData().thenReturn("Reload Complete!");
    }

    @Post("/fiis/reload")
    public Mono<String> reloadFII() {
        return fundamentosService.updateFIIData().thenReturn("FII Reload Complete!");
    }

    @Get("/raw")
    public Mono<List<StatusInvestData>> getRaw() {
        return fundamentosService.findAll().collectList();
    }

    @Get("/all")
    public Mono<List<Fundamento>> getAll() {
        return fundamentosService.findAll()
                .map(Fundamento::from)
                .collectList();
    }

    @Get("/fiis")
    public Mono<List<FII>> getAllFII() {
        return fundamentosService.findAllFII()
                .map(FII::from)
                .collectList();
    }

}
