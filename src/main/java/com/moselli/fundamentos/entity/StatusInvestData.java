package com.moselli.fundamentos.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Entity
@Introspected(packages="com.moselli.fundamentos.entity", includedAnnotations=Entity.class)
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

    private Double margemBruta = 0.0;

    private Double margemEbit = 0.0;

    private Double margemLiquida = 0.0;

    @JsonProperty("p_SR")
    private Double pSr = 0.0;

    @JsonProperty("p_CapitalGiro")
    private Double pCapitalGiro = 0.0;

    @JsonProperty("p_AtivoCirculante")
    private Double pAtivoCirculante;

    private Double giroAtivos;

    private Double roe;

    private Double roa;

    private Double roic = 0.0;

    @JsonProperty("dividaliquidaPatrimonioLiquido")
    private Double dividaLiquidaPatrimonioLiquido = 0.0;

    private Double dividaLiquidaEbit = 0.0;

    @JsonProperty("pl_Ativo")
    private Double plAtivo;

    @JsonProperty("passivo_Ativo")
    private Double passivoAtivo;

    private Double liquidezCorrente = 0.0;

    @JsonProperty("peg_Ratio")
    private Double pegRatio = 0.0;

    @JsonProperty("receitas_Cagr5")
    private Double receitasCagr5 = 0.0;

    @JsonProperty("lucros_Cagr5")
    private Double lucrosCagr5 = 0.0;

    private Double liquidezMediaDiaria = 0.0;

    private Double vpa;

    private Double lpa;

    private Double valorMercado;

    @OneToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker")
    private ClassificacaoB3 classificacaoB3;

}
