package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.model.FII;
import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller("/api/fiis")
@Log4j2
public class FiiController {

    @Inject
    private FundamentosService fundamentosService;

    @Get
    public Mono<List<FII>> getAll() {
        return fundamentosService.findAllFII()
                .map(FII::from)
                .collectList();
    }

    @Post("/reload")
    public Mono<String> reload() {
        return fundamentosService.updateFIIData().thenReturn("FII Reload Complete!");
    }

}
