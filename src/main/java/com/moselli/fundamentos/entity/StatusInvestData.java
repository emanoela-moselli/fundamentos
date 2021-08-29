package com.moselli.fundamentos.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@Entity
@Introspected(includedAnnotations = Entity.class)
public class StatusInvestData {

    @Id
    private String ticker;

    private Long companyId;

    private String companyName;

    private Double price;

    private Double dy = 0.0;

    @JsonProperty("p_L")
    private Double pl;

    @JsonProperty("p_VP")
    private Double pVp;

    @JsonProperty("p_Ativo")
    private Double pAtivo;

    @JsonProperty("p_Ebit")
    private Double pEbit;

    @JsonProperty("eV_Ebit")
    private Double evEbit;

    private Double margemBruta;

    private Double margemEbit;

    private Double margemLiquida;

    @JsonProperty("p_SR")
    private Double pSr;

    @JsonProperty("p_CapitalGiro")
    private Double pCapitalGiro;

    @JsonProperty("p_AtivoCirculante")
    private Double pAtivoCirculante;

    private Double giroAtivos;

    private Double roe;

    private Double roa;

    private Double roic;

    @JsonProperty("dividaliquidaPatrimonioLiquido")
    private Double dividaLiquidaPatrimonioLiquido;

    private Double dividaLiquidaEbit;

    @JsonProperty("pl_Ativo")
    private Double plAtivo;

    @JsonProperty("passivo_Ativo")
    private Double passivoAtivo;

    private Double liquidezCorrente;

    @JsonProperty("peg_Ratio")
    private Double pegRatio;

    @JsonProperty("receitas_Cagr5")
    private Double receitasCagr5;

    @JsonProperty("lucros_Cagr5")
    private Double lucrosCagr5;

    private Double liquidezMediaDiaria;

    private Double vpa;

    private Double lpa;

    private Double valorMercado;

    @OneToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker")
    private ClassificacaoB3 classificacaoB3;

}
