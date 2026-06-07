package com.moselli.fundamentos.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Introspected(includedAnnotations = Entity.class)
@Serdeable
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        StatusInvestData that = (StatusInvestData) o;
        return getTicker() != null && Objects.equals(getTicker(), that.getTicker());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
