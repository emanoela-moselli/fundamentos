package com.moselli.fundamentos.model;

import com.moselli.fundamentos.entity.ClassificacaoB3;
import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Introspected
@Getter
@Setter
@Serdeable
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
    private Double valorMercado;

    public static Fundamento from(StatusInvestData data) {
        Fundamento f = new Fundamento();
        f.setTicker(data.getTicker());
        f.setCompanyId(data.getCompanyId());
        f.setCompanyName(data.getCompanyName());
        f.setPrice(data.getPrice());
        f.setPl(data.getPl());
        f.setDy(data.getDy());
        f.setPVp(data.getPVp());
        f.setPAtivo(data.getPAtivo());
        f.setEvEbit(data.getEvEbit());
        f.setPEbit(data.getPEbit());
        f.setMargemBruta(data.getMargemBruta());
        f.setMargemEbit(data.getMargemEbit());
        f.setMargemLiquida(data.getMargemLiquida());
        f.setPSr(data.getPSr());
        f.setPCapitalGiro(data.getPCapitalGiro());
        f.setPAtivoCirculante(data.getPAtivoCirculante());
        f.setGiroAtivos(data.getGiroAtivos());
        f.setRoe(data.getRoe());
        f.setRoa(data.getRoa());
        f.setRoic(data.getRoic());
        f.setDividaLiquidaPatrimonioLiquido(data.getDividaLiquidaPatrimonioLiquido());
        f.setDividaLiquidaEbit(data.getDividaLiquidaEbit());
        f.setPlAtivo(data.getPlAtivo());
        f.setPassivoAtivo(data.getPassivoAtivo());
        f.setLiquidezCorrente(data.getLiquidezCorrente());
        f.setPegRatio(data.getPegRatio());
        f.setReceitasCagr5(data.getReceitasCagr5());
        f.setLucrosCagr5(data.getLucrosCagr5());
        f.setLiquidezMediaDiaria(data.getLiquidezMediaDiaria());
        f.setVpa(data.getVpa());
        f.setLpa(data.getLpa());
        f.setValorMercado(data.getValorMercado());
        ClassificacaoB3 c = data.getClassificacaoB3();
        if (c != null) {
            f.setSetorEconomico(c.getSetorEconomico());
            f.setSubsetor(c.getSubsetor());
            f.setSegmento(c.getSegmento());
        }
        f.calculateFields();
        return f;
    }

    public void calculateFields() {
        double roe = safe(this.roe) / 100.0;
        this.roe = roe;
        double dy = safe(this.dy) / 100.0;
        this.dy = dy;
        double lucrosCagr5 = safe(this.lucrosCagr5) / 100.0;
        this.lucrosCagr5 = lucrosCagr5;
        double price = safe(this.price);
        double lpa = safe(this.lpa);
        double vpa = safe(this.vpa);

        double dpa = price * dy;
        this.dpa = dpa;

        this.valuationBazin = dpa / 0.06;
        this.descontoBazin = safeDiv(this.valuationBazin - price, this.valuationBazin);

        double grahamBase = 22.5 * lpa * vpa;
        this.valuationGraham = grahamBase > 0 ? Math.pow(grahamBase, 0.5) : 0.0;
        this.descontoGraham = safeDiv(this.valuationGraham - price, this.valuationGraham);

        this.valuationGordon = (dpa * (1 + lucrosCagr5)) / 0.3;
        this.descontoGordon = safeDiv(this.valuationGordon - price, this.valuationGordon);

        this.payout = safeDiv(dpa, lpa);
        this.crescimentoEsperado = (1 - this.payout) * roe;
        this.mediaCrescimento = (lucrosCagr5 + this.crescimentoEsperado) / 2.0;
    }

    private static double safe(Double v) {
        return v != null ? v : 0.0;
    }

    private static double safeDiv(double num, double den) {
        return den == 0.0 ? 0.0 : num / den;
    }
}
