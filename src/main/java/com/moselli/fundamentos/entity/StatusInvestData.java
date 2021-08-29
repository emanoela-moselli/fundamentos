package com.moselli.fundamentos.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class StatusInvestData {

    @Id
    private Long companyId;
    private String companyName;
    private String ticker;
    private Double price;
    private Double pl;
    private Double pVp;
    private Double pAtivo;
    private Double evEbit;
    private Double margemBruta;
    private Double margemEbit;
    private Double margemLiquida;
    private Double pSr;
    private Double pCapitalGiro;
    private Double pAtivoCirculante;
    private Double giroAtivos;
    private Double roe;
    private Double roa;
    private Double dividaLiquidaPatrimonioLiquido;
    private Double dividaLiquidaEbit;
    private Double plAtivo;
    private Double passivoAtivo;
    private Double liquidezCorrente;
    private Double pegRatio;
    private Double receitasCagr5;
    private Double lucrosCagr5;
    private Double liquidezMediaDiaria;
    private Double vpa;
    private Double lpa;
    private Double valorMercado;

}