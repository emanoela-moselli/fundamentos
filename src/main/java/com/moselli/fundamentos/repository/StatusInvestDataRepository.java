package com.moselli.fundamentos.repository;

import java.util.List;

import com.moselli.fundamentos.entity.StatusInvestData;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface StatusInvestDataRepository extends CrudRepository<StatusInvestData, String> {
	
	List<StatusInvestData> findAll();
}
