package com.moselli.fundamentos.model;

import com.moselli.fundamentos.entity.StatusInvestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Fundamento valuation calculations.
 *
 * Expected values are cross-referenced against the Monitor_de_Valuation_3.0.xlsx
 * "Ações" worksheet (the spreadsheet this app is meant to replace).
 *
 * Raw StatusInvest fields (DY, ROE, CAGR) are percentages; calculateFields()
 * divides them by 100 before computing derived values, so assertions use the
 * decimal form that appears in the spreadsheet.
 */
class FundamentoTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private StatusInvestData abcb4() {
        // Source: "Dados Status Invest" sheet, row 3
        StatusInvestData d = new StatusInvestData();
        d.setTicker("ABCB4");
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

    private StatusInvestData b3sa3() {
        // Source: "Dados Status Invest" sheet, row for B3SA3
        StatusInvestData d = new StatusInvestData();
        d.setTicker("B3SA3");
        d.setCompanyName("B3 S.A. - Brasil");
        d.setPrice(12.04);
        d.setDy(7.97);
        d.setPl(15.75);
        d.setRoe(20.64);
        d.setLpa(0.76);
        d.setVpa(3.70);
        d.setLucrosCagr5(16.29);
        d.setValorMercado(73_757_040_000.0);
        return d;
    }

    private static final double DELTA = 1e-4;

    // -------------------------------------------------------------------------
    // ABCB4 — standard case with positive Bazin and Graham discounts
    // Expected values from "Ações" sheet row 12
    // -------------------------------------------------------------------------

    @Test
    void abcb4_dpa() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(1.002, f.getDpa(), DELTA);
    }

    @Test
    void abcb4_valuationBazin() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(16.7, f.getValuationBazin(), DELTA);
    }

    @Test
    void abcb4_descontoBazin_positive() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.10180, f.getDescontoBazin(), DELTA);
    }

    @Test
    void abcb4_valuationGraham() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(29.4145, f.getValuationGraham(), DELTA);
    }

    @Test
    void abcb4_descontoGraham_positive() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.49005, f.getDescontoGraham(), DELTA);
    }

    @Test
    void abcb4_valuationGordon() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(3.424168, f.getValuationGordon(), DELTA);
    }

    @Test
    void abcb4_descontoGordon_negative() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(-3.38063, f.getDescontoGordon(), DELTA);
    }

    @Test
    void abcb4_payout() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.51385, f.getPayout(), DELTA);
    }

    @Test
    void abcb4_crescimentoEsperado() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.04798, f.getCrescimentoEsperado(), DELTA);
    }

    @Test
    void abcb4_mediaCrescimento() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.03659, f.getMediaCrescimento(), DELTA);
    }

    // -------------------------------------------------------------------------
    // B3SA3 — payout > 100% edge case (dividend exceeds earnings)
    // Expected values from "Ações" sheet row 9
    // -------------------------------------------------------------------------

    @Test
    void b3sa3_dpa() {
        Fundamento f = Fundamento.from(b3sa3());
        assertEquals(0.95959, f.getDpa(), DELTA);
    }

    @Test
    void b3sa3_valuationBazin() {
        Fundamento f = Fundamento.from(b3sa3());
        assertEquals(15.9931, f.getValuationBazin(), DELTA);
    }

    @Test
    void b3sa3_descontoBazin_positive() {
        Fundamento f = Fundamento.from(b3sa3());
        assertEquals(0.24718, f.getDescontoBazin(), DELTA);
    }

    @Test
    void b3sa3_valuationGraham() {
        Fundamento f = Fundamento.from(b3sa3());
        assertEquals(7.95424, f.getValuationGraham(), DELTA);
    }

    @Test
    void b3sa3_descontoGraham_negative_stock_overpriced() {
        Fundamento f = Fundamento.from(b3sa3());
        assertEquals(-0.51366, f.getDescontoGraham(), DELTA);
    }

    @Test
    void b3sa3_payout_exceeds_one() {
        Fundamento f = Fundamento.from(b3sa3());
        assertTrue(f.getPayout() > 1.0, "payout should exceed 1 when DPA > LPA");
        assertEquals(1.26261, f.getPayout(), DELTA);
    }

    @Test
    void b3sa3_crescimentoEsperado_negative_when_payout_over_one() {
        Fundamento f = Fundamento.from(b3sa3());
        assertTrue(f.getCrescimentoEsperado() < 0);
        assertEquals(-0.05421, f.getCrescimentoEsperado(), DELTA);
    }

    // -------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------

    @Test
    void graham_is_zero_when_grahamBase_negative() {
        // grahamBase = 22.5 * lpa * vpa — only zero-guarded when result is negative.
        // Requires exactly one of lpa/vpa to be negative (both negative → positive product).
        StatusInvestData d = new StatusInvestData();
        d.setTicker("TEST_NEG");
        d.setPrice(5.0);
        d.setLpa(-1.0);  // negative earnings
        d.setVpa(10.0);  // positive book value → grahamBase < 0
        Fundamento f = Fundamento.from(d);
        assertEquals(0.0, f.getValuationGraham(), DELTA);
    }

    @Test
    void all_null_inputs_do_not_throw() {
        StatusInvestData d = new StatusInvestData();
        d.setTicker("NULL1");
        assertDoesNotThrow(() -> Fundamento.from(d));
    }

    @Test
    void all_null_inputs_produce_zero_valuations() {
        StatusInvestData d = new StatusInvestData();
        d.setTicker("NULL1");
        Fundamento f = Fundamento.from(d);
        assertEquals(0.0, f.getDpa(), DELTA);
        assertEquals(0.0, f.getValuationBazin(), DELTA);
        assertEquals(0.0, f.getValuationGraham(), DELTA);
        assertEquals(0.0, f.getValuationGordon(), DELTA);
    }

    // -------------------------------------------------------------------------
    // Field mapping — Fundamento.from() propagates all StatusInvestData fields
    // -------------------------------------------------------------------------

    @Test
    void from_maps_all_identity_fields() {
        StatusInvestData d = abcb4();
        Fundamento f = Fundamento.from(d);
        assertEquals("ABCB4", f.getTicker());
        assertEquals("Bco Abc Brasil S.A.", f.getCompanyName());
        assertEquals(15.0, f.getPrice(), DELTA);
        assertEquals(3_391_351_770.0, f.getValorMercado(), 1.0);
    }

    @Test
    void from_sector_fields_are_null_when_classificacao_absent() {
        // ClassificacaoB3 has no public setters; when it is null (not yet loaded)
        // the sector fields on Fundamento should remain null rather than throw.
        Fundamento f = Fundamento.from(abcb4());
        assertNull(f.getSetorEconomico());
        assertNull(f.getSubsetor());
        assertNull(f.getSegmento());
    }

    // -------------------------------------------------------------------------
    // dy, roe, lucrosCagr5 are normalised from % → decimal inside calculateFields
    // -------------------------------------------------------------------------

    @Test
    void roe_is_stored_as_decimal_fraction() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.0987, f.getRoe(), DELTA); // input was 9.87
    }

    @Test
    void dy_is_stored_as_decimal_fraction() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.0668, f.getDy(), DELTA); // input was 6.68
    }

    @Test
    void lucrosCagr5_is_stored_as_decimal_fraction() {
        Fundamento f = Fundamento.from(abcb4());
        assertEquals(0.0252, f.getLucrosCagr5(), DELTA); // input was 2.52
    }
}
