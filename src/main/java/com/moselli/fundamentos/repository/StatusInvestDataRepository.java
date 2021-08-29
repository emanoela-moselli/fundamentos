package com.moselli.fundamentos.repository;

import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface StatusInvestDataRepository extends CrudRepository<StatusInvestData, Long> {
}
