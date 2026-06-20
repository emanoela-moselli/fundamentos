# FII Model — Implementation Plan

## Goal

Add support for Fundos de Investimento Imobiliário (FIIs) to the app, replacing the
`Monitor_de_FII.xlsx` spreadsheet. The feature mirrors what already exists for Ações:
fetch data from StatusInvest, persist it, compute derived fields, expose via API, and
render in the frontend.

---

## 1. Data Source

StatusInvest exposes FII data through the same paginated search endpoint used for
stocks, with `CategoryType=2` (stocks use `CategoryType=1`). The JSON shape is the
same; only the available fields differ.

**Fields returned by StatusInvest for FIIs:**

| JSON key | Description |
|---|---|
| `ticker` | FII ticker (e.g. `HGLG11`) |
| `companyId` | Internal ID |
| `companyName` | Fund name |
| `price` | Current quota price (cotação) |
| `dy` | Dividend yield (%) |
| `p_VP` | P/VP (price / book value per quota) |
| `vpa` | Valor patrimonial da cota |
| `lastDividend` | Último dividendo mensal |
| `liquidezMediaDiaria` | Average daily liquidity |
| `percentualEmCaixa` | Cash % |
| `dividendoCagr3Anos` | CAGR dividends 3 years |
| `cotaCagr3Anos` | CAGR quota value 3 years |
| `patrimonio` | Total AUM |
| `numeroCotistas` | Number of shareholders |
| `gestao` | Management type (Ativa / Passiva) |
| `numeroCotas` | Total number of quotas |
| `sector` | FII sector/type (see below) |

> Note: field names above follow the pattern observed from the stocks endpoint and
> the spreadsheet column headers. Verify exact JSON keys on first integration.

**FII sector types (from spreadsheet):**
Recebíveis-IPCA, Recebíveis-IGPM, Recebíveis-CDI, Lajes Corporativas, Shopping,
Logístico/Industrial, Fundos de Fundos, Hospital, Hotel, Agência Bancária,
Educacional, Incorporação, Residencial, Multi, Outros.

---

## 2. Backend Architecture

### 2.1 New entity — `FIIData`

```
src/main/java/com/moselli/fundamentos/entity/FIIData.java
```

JPA entity persisted in a `fii_data` table. Fields mirror the StatusInvest response.

```java
@Entity
public class FIIData {
    @Id private String ticker;
    private Long companyId;
    private String companyName;
    private String sector;          // fund type/segment
    private Double price;
    private Double dy;
    private Double pVp;
    private Double vpa;
    private Double lastDividend;
    private Double liquidezMediaDiaria;
    private Double percentualEmCaixa;
    private Double dividendoCagr3Anos;
    private Double cotaCagr3Anos;
    private Double patrimonio;
    private Long   numeroCotistas;
    private String gestao;          // "Ativa" | "Passiva"
    private Long   numeroCotas;
}
```

### 2.2 New model — `FII`

```
src/main/java/com/moselli/fundamentos/model/FII.java
```

Computed from `FIIData`. Adds derived/valuation fields:

| Field | Formula |
|---|---|
| `dpa` | `price × dy` (annual dividend per quota) |
| `dividendoMensal` | `lastDividend` (pass-through, used in valuation) |
| `valuationGordonFII` | `(dividendoMensal × 12 × (1 + dividendoCagr3Anos)) / taxaDesconto` where `taxaDesconto` = configurable (default 0.12) |
| `descontoGordonFII` | `(valuationGordonFII - price) / valuationGordonFII` |
| `valuationBazinFII` | `(dividendoMensal × 12) / 0.06` |
| `descontoBazinFII` | `(valuationBazinFII - price) / valuationBazinFII` |

> The Tijolo (DCF) and Papel (rate-based) valuations described in sections 3 and 4
> below are more precise but require additional user-supplied parameters. Start with
> Gordon and Bazin (same approach as stocks) to get the feature shipped, then add
> the advanced models in a follow-up.

### 2.3 New client — `FIIClient`

```
src/main/java/com/moselli/fundamentos/client/FIIClient.java
```

Same interface pattern as `StatusInvestClient`, pointing to the same host with
`CategoryType=2`. Extract the shared base URL into `StatusInvestConfig`.

### 2.4 New repository — `FIIDataRepository`

```
src/main/java/com/moselli/fundamentos/repository/FIIDataRepository.java
```

Extends `CrudRepository<FIIData, String>`. Same upsert pattern as
`StatusInvestDataRepository`.

### 2.5 Extended service — `FundamentosService`

Add `updateFIIData()` and `findAllFII()` methods, injecting `FIIClient` and
`FIIDataRepository`.

### 2.6 New controller endpoints

```
GET  /api/fiis        → List<FII>
POST /api/fiis/reload → trigger FII data refresh
```

Can be added to `FundamentosController` or a new `FIIController`.

---

## 3. Valuation — Tijolo (Brick / Real-estate funds)

Applicable to: Lajes Corporativas, Shopping, Logístico/Industrial, Agência Bancária,
Hospital, Educacional, Residencial, Multi.

**Scenario 1 — Constant growth DCF:**

```
valor_justo = Σ [ dividendo_mensal × (1 + g)^t / (1 + r)^t ]  for t = 1..N
            + desinvestimento / (1 + r)^N
```

Where:
- `g` = taxa de crescimento (default: IPCA forecast, e.g. 0.045)
- `r` = taxa de desconto = IPCA + prêmio de risco (default: 0.10)
- `N` = número de anos (default: 10)
- `desinvestimento` = `vpa × cap_rate_terminal / taxa_desconto`
- `cap_rate_terminal` = configurable per sector (Shopping ~6%, Logístico ~7%, Lajes ~8%)

**Scenario 2 — Variable growth:**

Splits projection into two phases: near-term growth at `dividendoCagr3Anos`, then
long-term growth at IPCA. Implementation can follow the same DCF loop with
year-by-year rates.

**Parameters (all configurable, stored as app config or passed from frontend):**

| Parameter | Default |
|---|---|
| Taxa de desconto | 10% |
| IPCA | 4.5% |
| Prêmio de risco | 2% |
| Anos de projeção | 10 |
| Cap rate terminal (Shopping) | 6% |
| Cap rate terminal (Logístico) | 7% |
| Cap rate terminal (Lajes) | 8% |

---

## 4. Valuation — Papel (CRI/CRR funds)

Applicable to: Recebíveis-IPCA, Recebíveis-IGPM, Recebíveis-CDI.

**Approach:** Project the annual dividend from the fund's portfolio composition.

```
dividendo_anual = Σ [ percentual_i × taxa_indice_i × (1 + spread_i) × patrimonio ]
valor_justo     = dividendo_anual / taxa_desconto
desconto        = (valor_justo - price × numero_cotas) / valor_justo
```

Where `taxa_indice_i` is the current CDI, IPCA, IGP-M or IGP-DI rate.

This model requires the fund's portfolio composition breakdown, which is **not**
available in the StatusInvest API — it must be input manually or scraped from the
fund's monthly report. **Defer this model to a later iteration.** For Papel funds,
use Gordon/Bazin as a first approximation.

---

## 5. Frontend

### 5.1 New tab / view

Add a "FIIs" tab next to the existing "Ações" tab. The FII view shares the same
table/filter/sort shell.

**Columns:**

| Column | Field | Format |
|---|---|---|
| Ticker | `ticker` | text |
| Fundo | `companyName` | text |
| Setor | `sector` | text |
| Cotação | `price` | currency |
| DY | `dy` | percent |
| P/VP | `pVp` | number |
| Últ. Div. | `dividendoMensal` | currency |
| Caixa % | `percentualEmCaixa` | percent |
| CAGR Div 3A | `dividendoCagr3Anos` | percent |
| CAGR Cota 3A | `cotaCagr3Anos` | percent |
| Liquidez | `liquidezMediaDiaria` | currency |
| Bazin FII | `valuationBazinFII` | currency |
| Desc. Bazin | `descontoBazinFII` | discount |
| Gordon FII | `valuationGordonFII` | currency |
| Desc. Gordon | `descontoGordonFII` | discount |

### 5.2 Sector filter

A dropdown to filter by FII sector type (in addition to the existing text filter).

---

## 6. Delivery Sequence

1. ✅ **FIIData entity + migration** — `FIIData.java` JPA entity; Hibernate `hbm2ddl.auto=update` creates `fii_data` table automatically
2. ✅ **FIIClient + FIIDataRepository** — `fetchFiiData()` added to `StatusInvestClient`; `FIIDataRepository` extends `ReactorCrudRepository`; FII-specific fields added to `StatusInvestApiItem` (`segment`, `gestao`, `lastdividend`, `percentualcaixa`, `dividend_cagr`, `cota_cagr`, `patrimonio`, `numerocotistas`, `numerocotas`)
3. ✅ **FII model** — `FII.java` with Gordon (`dpa × (1 + dividendoCagr3Anos) / 0.12`) and Bazin (`dpa / 0.06`) valuations; percentages normalised (÷100) in `calculateFields()`
4. ✅ **API endpoints** — `POST /api/fiis/reload` and `GET /api/fiis` added to `FundamentosController`; `FundamentosService` now has separate `updateFIIData()` and `findAllFII()` methods (FIIs and stocks no longer share the same table)
5. ✅ **Frontend FII tab** — Ações / FIIs tab switcher; FII table with 15 columns (Ticker, Fundo, Segmento, Cotação, DY, P/VP, Últ. Div., Caixa %, CAGR Div 3A, CAGR Cota 3A, Liquidez, Bazin FII, Desc. Bazin, Gordon FII, Desc. Gordon)
6. ✅ **Sector dropdown filter** — adapts to active tab (`setorEconomico` for Ações, `sector` for FIIs); reloads lazily on first tab visit
7. *(Follow-up)* Tijolo DCF Scenario 1
8. *(Follow-up)* Tijolo DCF Scenario 2 / Papel model
