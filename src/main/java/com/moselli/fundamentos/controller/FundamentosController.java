package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.service.FundamentosService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/greet")
public class FundamentosController {


    @Inject
    private FundamentosService fundamentosService;

    @Get("/{name}")
    public String greet(String name) {
        fundamentosService.updateStatusInvestData();
        return "It Works!";
    }

}
