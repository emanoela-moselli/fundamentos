package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/api")
public class FundamentosController {

    @Inject
    private FundamentosService fundamentosService;

    @Get("/reload")
    public String reload() {
        return fundamentosService.updateStatusInvestData().thenReturn("Reload Complete!").block();
    }

}
