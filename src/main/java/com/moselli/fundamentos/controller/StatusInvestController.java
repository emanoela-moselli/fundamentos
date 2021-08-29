package com.moselli.fundamentos.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.moselli.fundamentos.entity.StatusInvestData;
import com.moselli.fundamentos.service.FundamentosService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.inject.Inject;

@Controller("/views")
public class StatusInvestController {


    @Inject
    private FundamentosService fundamentosService;
    
    List<StatusInvestData> statusInvestData = new ArrayList<StatusInvestData>();

    @View("home")
    @Get("/")
    public ModelAndView index() {
        return new ModelAndView<>("home", Collections.singletonMap("statusInvestData", fundamentosService.findAll()));
    }

}
