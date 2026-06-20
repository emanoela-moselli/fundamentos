package com.moselli.fundamentos.repository;

import com.moselli.fundamentos.entity.Stock;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;

@Repository
public interface StockRepository extends ReactorCrudRepository<Stock, String> {
}
