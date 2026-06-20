package com.moselli.fundamentos.model;

import com.moselli.fundamentos.entity.FIIData;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Introspected
@Getter
@Setter
@Serdeable
public class FII {

    private String ticker;
    private Long companyId;
    private String companyName;
    private String sector;
    private String gestao;
    private Double price;
    private Double dy;
    private Double pVp;
    private Double vpa;
    private Double lastDividend;
    private Double liquidezMediaDiaria;
    private Double percentualEmCaixa;
    private Double dividendoCagr3Anos;
    private Double cotaCagr3Anos;
    private Double patrimonio;
    private Long numeroCotistas;
    private Long numeroCotas;
    private Double dpa;
    private Double valuationBazinFII;
    private Double descontoBazinFII;
    private Double valuationGordonFII;
    private Double descontoGordonFII;

    public static FII from(FIIData data) {
        FII f = new FII();
        f.setTicker(data.getTicker());
        f.setCompanyId(data.getCompanyId());
        f.setCompanyName(data.getCompanyName());
        f.setSector(data.getSector());
        f.setGestao(data.getGestao());
        f.setPrice(data.getPrice());
        f.setDy(data.getDy());
        f.setPVp(data.getPVp());
        f.setVpa(data.getVpa());
        f.setLastDividend(data.getLastDividend());
        f.setLiquidezMediaDiaria(data.getLiquidezMediaDiaria());
        f.setPercentualEmCaixa(data.getPercentualEmCaixa());
        f.setDividendoCagr3Anos(data.getDividendoCagr3Anos());
        f.setCotaCagr3Anos(data.getCotaCagr3Anos());
        f.setPatrimonio(data.getPatrimonio());
        f.setNumeroCotistas(data.getNumeroCotistas());
        f.setNumeroCotas(data.getNumeroCotas());
        f.calculateFields();
        return f;
    }

    public void calculateFields() {
        double price = safe(this.price);
        double lastDividend = safe(this.lastDividend);
        double dividendoCagr3Anos = safe(this.dividendoCagr3Anos) / 100.0;
        this.dividendoCagr3Anos = dividendoCagr3Anos;

        double cotaCagr3Anos = safe(this.cotaCagr3Anos) / 100.0;
        this.cotaCagr3Anos = cotaCagr3Anos;

        double dy = safe(this.dy) / 100.0;
        this.dy = dy;

        this.percentualEmCaixa = safe(this.percentualEmCaixa) / 100.0;

        double dpa = lastDividend * 12.0;
        this.dpa = dpa;

        this.valuationBazinFII = dpa / 0.06;
        this.descontoBazinFII = safeDiv(this.valuationBazinFII - price, this.valuationBazinFII);

        this.valuationGordonFII = (dpa * (1 + dividendoCagr3Anos)) / 0.12;
        this.descontoGordonFII = safeDiv(this.valuationGordonFII - price, this.valuationGordonFII);
    }

    private static double safe(Double v) {
        return v != null ? v : 0.0;
    }

    private static double safeDiv(double num, double den) {
        return den == 0.0 ? 0.0 : num / den;
    }
}
