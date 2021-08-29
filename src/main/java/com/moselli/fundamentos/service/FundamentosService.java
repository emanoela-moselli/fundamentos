package com.moselli.fundamentos.service;

import com.moselli.fundamentos.repository.StatusInvestDataRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class FundamentosService {

    @Inject
    private StatusInvestDataRepository statusInvestDataRepository;

    public void updateStatusInvestData(){
        System.out.println("Testing");
    }

}
