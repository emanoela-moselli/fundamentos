package com.moselli.fundamentos.repository;

import com.moselli.fundamentos.entity.FIIData;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;

@Repository
public interface FIIDataRepository extends ReactorCrudRepository<FIIData, String> {
}