package com.moselli.fundamentos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class ClassificacaoB3 {

    @Id
    private String ticker;
    private String empresa;
    private String setorEconomico;
    private String subsetor;
    private String segmento;

}
