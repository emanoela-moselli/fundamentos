package com.moselli.fundamentos.repository;

import com.moselli.fundamentos.entity.StatusInvestData;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for StatusInvestDataRepository.
 *
 * These tests require a live PostgreSQL instance. When running via Maven,
 * micronaut-test-resources-client (provided scope) wires the Micronaut Maven
 * plugin to start a PostgreSQL container automatically. Make sure Docker is
 * running before executing the test suite.
 *
 * Test data uses ABCB4 values from the Monitor_de_Valuation_3.0.xlsx spreadsheet.
 */
@MicronautTest
class StatusInvestDataRepositoryTest {

    @Inject
    StatusInvestDataRepository repository;

    @AfterEach
    void cleanup() {
        repository.deleteAll().block();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private StatusInvestData abcb4() {
        StatusInvestData d = new StatusInvestData();
        d.setTicker("ABCB4");
        d.setCompanyId(1L);
        d.setCompanyName("Bco Abc Brasil S.A.");
        d.setPrice(15.0);
        d.setDy(6.68);
        d.setPl(7.71);
        d.setRoe(9.87);
        d.setLpa(1.95);
        d.setVpa(19.72);
        d.setLucrosCagr5(2.52);
        d.setValorMercado(3_391_351_770.0);
        return d;
    }

    private StatusInvestData bbas3() {
        StatusInvestData d = new StatusInvestData();
        d.setTicker("BBAS3");
        d.setCompanyId(2L);
        d.setCompanyName("Bco Brasil S.A.");
        d.setPrice(28.91);
        d.setDy(6.95);
        d.setPl(5.21);
        d.setRoe(11.73);
        d.setLpa(5.55);
        d.setVpa(47.27);
        d.setLucrosCagr5(2.46);
        d.setValorMercado(82_294_776_814.0);
        return d;
    }

    // -------------------------------------------------------------------------
    // Save and retrieve
    // -------------------------------------------------------------------------

    @Test
    void save_and_findById_returns_correct_record() {
        repository.save(abcb4()).block();

        StatusInvestData found = repository.findById("ABCB4").block();

        assertNotNull(found);
        assertEquals("ABCB4", found.getTicker());
        assertEquals("Bco Abc Brasil S.A.", found.getCompanyName());
        assertEquals(15.0, found.getPrice(), 1e-6);
        assertEquals(6.68, found.getDy(), 1e-6);
        assertEquals(1.95, found.getLpa(), 1e-6);
        assertEquals(19.72, found.getVpa(), 1e-6);
    }

    @Test
    void findById_returns_empty_for_unknown_ticker() {
        StatusInvestData found = repository.findById("UNKNOWN").block();
        assertNull(found);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    void findAll_returns_all_saved_records() {
        repository.save(abcb4()).block();
        repository.save(bbas3()).block();

        List<StatusInvestData> all = repository.findAll().collectList().block();

        assertNotNull(all);
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(d -> "ABCB4".equals(d.getTicker())));
        assertTrue(all.stream().anyMatch(d -> "BBAS3".equals(d.getTicker())));
    }

    @Test
    void findAll_returns_empty_list_when_no_data() {
        List<StatusInvestData> all = repository.findAll().collectList().block();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Test
    void update_persists_changed_price() {
        // Save original record
        repository.save(abcb4()).block();

        // Simulate what the service does: create a fresh instance (from API response)
        // with the same ticker and call update() — this is the "upsert" path used by
        // FundamentosService.updateStatusInvestData(). Calling update() on a detached
        // entity loaded in a separate reactive session causes Hibernate to open a new
        // session where the entity is not yet managed, leading to an INSERT attempt
        // rather than an UPDATE, which violates the PK constraint.
        StatusInvestData refreshed = abcb4();
        refreshed.setPrice(16.50);
        repository.update(refreshed).block();

        StatusInvestData found = repository.findById("ABCB4").block();
        assertNotNull(found);
        assertEquals(16.50, found.getPrice(), 1e-6);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @Test
    void deleteById_removes_the_record() {
        repository.save(abcb4()).block();
        assertTrue(repository.existsById("ABCB4").block());

        repository.deleteById("ABCB4").block();

        assertFalse(repository.existsById("ABCB4").block());
    }

    @Test
    void count_reflects_saves_and_deletes() {
        assertEquals(0L, repository.count().block());

        repository.save(abcb4()).block();
        repository.save(bbas3()).block();
        assertEquals(2L, repository.count().block());

        repository.deleteById("ABCB4").block();
        assertEquals(1L, repository.count().block());
    }
}
