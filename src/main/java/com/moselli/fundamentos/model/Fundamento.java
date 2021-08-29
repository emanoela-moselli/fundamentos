package com.moselli.fundamentos.model;

import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;

@Introspected
@Getter
@Setter
public class Fundamento {

    private String ticker;
    private Long companyId;
    private String companyName;
    private String setorEconomico;
    private String subsetor;
    private String segmento;
    private Double price;
    private Double pl;
    private Double dy;
    private Double pVp;
    private Double pAtivo;
    private Double evEbit;
    private Double pEbit;
    private Double margemBruta;
    private Double margemEbit;
    private Double margemLiquida;
    private Double pSr;
    private Double pCapitalGiro;
    private Double pAtivoCirculante;
    private Double giroAtivos;
    private Double roe;
    private Double roa;
    private Double roic;
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
    private Double valuationBazin;
    private Double descontoBazin;
    private Double valuationGraham;
    private Double descontoGraham;
    private Double valuationGordon;
    private Double descontoGordon;
    private Double dpa;
    private Double payout;
    private Double crescimentoEsperado;
    private Double mediaCrescimento;

    public void calculateFields() {
        this.roe = this.roe / 100.0;
        this.dy = this.dy / 100.0;
        this.lucrosCagr5 = this.lucrosCagr5 / 100.0;
        this.dpa = this.price * this.dy;
        this.valuationBazin = this.dpa / 0.06;
        this.descontoBazin = (this.valuationBazin - this.price) / this.valuationBazin;
        this.valuationGraham = Math.pow(22.5*this.lpa*this.vpa, 0.5);
        this.descontoGraham = (this.valuationGraham - this.price) / this.valuationGraham;
        this.valuationGordon = (this.dpa * (1 + this.lucrosCagr5)) / 0.3;
        this.descontoGordon = (this.valuationGordon - this.price) / this.valuationGordon;

        this.payout = this.dpa / this.lpa;
        this.crescimentoEsperado = (1 - this.payout)*this.roe;
        this.mediaCrescimento = (this.lucrosCagr5 + this.crescimentoEsperado) / 2.0;
    }
}
