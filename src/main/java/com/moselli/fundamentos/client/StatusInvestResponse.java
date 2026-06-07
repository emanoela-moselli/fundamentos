package com.moselli.fundamentos.client;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Serdeable
public class StatusInvestResponse {

    private List<StatusInvestApiItem> list;

    private Integer totalCount;
}
