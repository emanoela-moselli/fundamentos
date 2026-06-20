package com.moselli.fundamentos.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Serdeable
public class StatusInvestApiItem {

    private String ticker;

    @JsonProperty("companyid")
    private Long companyId;

    @JsonProperty("companyname")
    private String companyName;

    private Double price;

    private Double dy;

    @JsonProperty("p_l")
    private Double pl;

    @JsonProperty("p_vp")
    private Double pVp;

    @JsonProperty("p_ativo")
    private Double pAtivo;

    @JsonProperty("p_ebit")
    private Double pEbit;

    @JsonProperty("ev_ebit")
    private Double evEbit;

    @JsonProperty("margembruta")
    private Double margemBruta;

    @JsonProperty("margemebit")
    private Double margemEbit;

    @JsonProperty("margemliquida")
    private Double margemLiquida;

    @JsonProperty("p_sr")
    private Double pSr;

    @JsonProperty("p_capitalgiro")
    private Double pCapitalGiro;

    @JsonProperty("p_ativocirculante")
    private Double pAtivoCirculante;

    @JsonProperty("giroativos")
    private Double giroAtivos;

    private Double roe;

    private Double roa;

    private Double roic;

    @JsonProperty("dividaliquidapatrimonioliquido")
    private Double dividaLiquidaPatrimonioLiquido;

    @JsonProperty("dividaliquidaebit")
    private Double dividaLiquidaEbit;

    @JsonProperty("pl_ativo")
    private Double plAtivo;

    @JsonProperty("passivo_ativo")
    private Double passivoAtivo;

    @JsonProperty("liquidezcorrente")
    private Double liquidezCorrente;

    @JsonProperty("peg_ratio")
    private Double pegRatio;

    @JsonProperty("receitas_cagr5")
    private Double receitasCagr5;

    @JsonProperty("lucros_cagr5")
    private Double lucrosCagr5;

    @JsonProperty("liquidezmediadiaria")
    private Double liquidezMediaDiaria;

    private Double vpa;

    private Double lpa;

    @JsonProperty("valormercado")
    private Double valorMercado;

    // FII-specific fields
    private String segment;

    private String gestao;

    @JsonProperty("lastdividend")
    private Double lastDividend;

    @JsonProperty("percentualcaixa")
    private Double percentualEmCaixa;

    @JsonProperty("dividend_cagr")
    private Double dividendoCagr3Anos;

    @JsonProperty("cota_cagr")
    private Double cotaCagr3Anos;

    private Double patrimonio;

    @JsonProperty("numerocotistas")
    private Long numeroCotistas;

    @JsonProperty("numerocotas")
    private Long numeroCotas;
}
