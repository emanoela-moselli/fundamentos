package com.moselli.fundamentos.repository;

import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;

@Repository
public interface StatusInvestDataRepository extends ReactorCrudRepository<StatusInvestData, String> {
}
