package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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
