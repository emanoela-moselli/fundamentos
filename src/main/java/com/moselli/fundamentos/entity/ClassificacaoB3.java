package com.moselli.fundamentos.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ClassificacaoB3 {

    @Id
    private String ticker;
    private String empresa;
    private String setorEconomico;
    private String subsetor;
    private String segmento;

}
