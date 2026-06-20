package com.moselli.fundamentos.entity;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class FIIData {

    @Id
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FIIData that = (FIIData) o;
        return getTicker() != null && Objects.equals(getTicker(), that.getTicker());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}