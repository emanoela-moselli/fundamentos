package com.moselli.fundamentos.service;

import java.util.List;

import com.moselli.fundamentos.entity.StatusInvestData;
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
    
    public List<StatusInvestData> listAll(){
        return statusInvestDataRepository.findAll();
    }

}
