package com.moselli.fundamentos.controller;

import com.moselli.fundamentos.model.Fundamento;
import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/api")
public class FundamentosController {

    @Inject
    private FundamentosService fundamentosService;

    @Post("/reload")
    public String reload() {
        return fundamentosService.updateStatusInvestData().thenReturn("Reload Complete!").block();
    }

    @Get("/raw")
    public String getRaw() {
        return fundamentosService.updateStatusInvestData().thenReturn("Reload Complete!").block();
    }

    @Get("/all")
    public List<Fundamento> getAll(){
        return fundamentosService.findAll().stream().map(statusInvestData -> {
            Fundamento fundamento = new Fundamento();
            try {
                BeanUtils.copyProperties(fundamento, statusInvestData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            fundamento.calculateFields();
            return fundamento;
        }).collect(Collectors.toList());
    }

}
