package com.moselli.fundamentos;

import com.moselli.fundamentos.client.StatusInvestApiItem;
import com.moselli.fundamentos.client.StatusInvestClient;
import com.moselli.fundamentos.client.StatusInvestResponse;
import com.moselli.fundamentos.repository.FIIDataRepository;
import com.moselli.fundamentos.repository.StatusInvestDataRepository;
import com.moselli.fundamentos.service.FundamentosService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Calls the real StatusInvest API and persists results to a test database.
 * Requires internet access and Docker (for the PostgreSQL test container).
 * Run with: mvn test -Dgroups=external
 */
@Tag("external")
@MicronautTest
class StatusInvestLoadIntegrationTest {

    @Inject
    StatusInvestClient statusInvestClient;

    @Inject
    StatusInvestDataRepository repository;

    @Inject
    FIIDataRepository fiiDataRepository;

    @Inject
    FundamentosService fundamentosService;

    @AfterEach
    void cleanup() {
        repository.deleteAll().block();
        fiiDataRepository.deleteAll().block();
    }

    @Test
    void fetchStockData_returns_items_with_non_null_tickers() {
        StatusInvestResponse response = statusInvestClient.fetchStocksData()
                .block(Duration.ofSeconds(60));

        assertNotNull(response, "fetchData() returned null");
        assertNotNull(response.getList(), "response.list is null");
        assertFalse(response.getList().isEmpty(), "StatusInvest API returned an empty list");

        StatusInvestApiItem first = response.getList().get(0);
        System.out.println("First item ticker: " + first.getTicker());
        System.out.println("First item companyName: " + first.getCompanyName());
        System.out.println("First item companyId: " + first.getCompanyId());
        System.out.println("First item price: " + first.getPrice());
        System.out.println("First item dy: " + first.getDy());
        System.out.println("First item pl: " + first.getPl());
        System.out.println("First item valorMercado: " + first.getValorMercado());
        System.out.println("Total items received: " + response.getList().size());

        assertNotNull(first.getTicker(), "ticker should not be null after deserialization");
        assertNotNull(first.getCompanyName(), "companyName should not be null after deserialization");
    }

    @Test
    void fetchFiiData_returns_items_with_non_null_tickers() {
        StatusInvestResponse response = statusInvestClient.fetchFiiData()
                .block(Duration.ofSeconds(60));

        assertNotNull(response, "fetchData() returned null");
        assertNotNull(response.getList(), "response.list is null");
        assertFalse(response.getList().isEmpty(), "StatusInvest API returned an empty list");

        StatusInvestApiItem first = response.getList().get(0);
        System.out.println("First item ticker: " + first.getTicker());
        System.out.println("First item companyName: " + first.getCompanyName());
        System.out.println("First item price: " + first.getPrice());
        System.out.println("First item dy: " + first.getDy());
        System.out.println("First item pVp: " + first.getPVp());
        System.out.println("First item vpa: " + first.getVpa());
        System.out.println("First item lastDividend: " + first.getLastDividend());
        System.out.println("First item segment: " + first.getSegment());
        System.out.println("First item gestao: " + first.getGestao());
        System.out.println("First item percentualEmCaixa: " + first.getPercentualEmCaixa());
        System.out.println("First item dividendoCagr3Anos: " + first.getDividendoCagr3Anos());
        System.out.println("First item cotaCagr3Anos: " + first.getCotaCagr3Anos());
        System.out.println("First item patrimonio: " + first.getPatrimonio());
        System.out.println("First item numeroCotistas: " + first.getNumeroCotistas());
        System.out.println("First item numeroCotas: " + first.getNumeroCotas());
        System.out.println("Total items received: " + response.getList().size());

        assertNotNull(first.getTicker(), "ticker should not be null after deserialization");
        assertNotNull(first.getCompanyName(), "companyName should not be null after deserialization");
    }

    @Test
    void updateStatusInvestData_persists_stock_records_to_database() {
        fundamentosService.updateStatusInvestData().block(Duration.ofSeconds(60));

        long count = repository.count().block(Duration.ofSeconds(10));
        assertTrue(count > 0, "Database should have records after reload");
        System.out.println("Persisted " + count + " stock records");

        StatusInvestResponse response = statusInvestClient.fetchStocksData()
                .block(Duration.ofSeconds(60));
        assertNotNull(response);

        List<StatusInvestApiItem> items = response.getList();
        assertFalse(items.isEmpty());

        String firstTicker = items.get(0).getTicker();
        assertNotNull(firstTicker);

        var found = repository.findById(firstTicker).block();
        assertNotNull(found, "Record for ticker " + firstTicker + " not found after reload");
        assertEquals(firstTicker, found.getTicker());
        assertNotNull(found.getCompanyName(), "companyName should be persisted");
    }

    @Test
    void updateFIIData_persists_fii_records_to_database() {
        fundamentosService.updateFIIData().block(Duration.ofSeconds(60));

        long count = fiiDataRepository.count().block(Duration.ofSeconds(10));
        assertTrue(count > 0, "Database should have FII records after reload");
        System.out.println("Persisted " + count + " FII records");

        StatusInvestResponse response = statusInvestClient.fetchFiiData()
                .block(Duration.ofSeconds(60));
        assertNotNull(response);

        List<StatusInvestApiItem> items = response.getList();
        assertFalse(items.isEmpty());

        String firstTicker = items.get(0).getTicker();
        assertNotNull(firstTicker);

        var found = fiiDataRepository.findById(firstTicker).block();
        assertNotNull(found, "FII record for ticker " + firstTicker + " not found after reload");
        assertEquals(firstTicker, found.getTicker());
        assertNotNull(found.getCompanyName(), "companyName should be persisted");
    }
}
