# Stock Model

## Overview

Stocks (Ações) are fetched from the StatusInvest API (`CategoryType=1`), persisted to
PostgreSQL, and enriched with valuation metrics before being served to the frontend.

---

## Data Flow

```
StatusInvest API
      │
      ▼
StatusInvestClient.fetchStocksData()   →  StatusInvestResponse (List<StatusInvestApiItem>)
      │
      ▼
FundamentosService.updateStocks()      →  Stock (JPA entity, table: stock)
      │
      ▼
StockRepository                        →  PostgreSQL
      │
      ▼
FundamentosService.findAll()           →  Flux<Stock>
      │
      ▼
Fundamento.from(Stock)                 →  Fundamento (adds valuation fields)
      │
      ▼
GET /api/all                           →  List<Fundamento>
```

---

## Entity — `Stock`

```
src/main/java/com/moselli/fundamentos/entity/Stock.java
```

JPA entity mapped to the `stock` table. Fields are the raw values returned by StatusInvest.

| Field | Type | Description |
|---|---|---|
| `ticker` | `String` (PK) | Stock ticker (e.g. `PETR4`) |
| `companyId` | `Long` | Internal StatusInvest ID |
| `companyName` | `String` | Company name |
| `price` | `Double` | Current price (cotação) |
| `dy` | `Double` | Dividend yield (%) |
| `pl` | `Double` | P/L |
| `pVp` | `Double` | P/VP |
| `pAtivo` | `Double` | P/Ativo |
| `pEbit` | `Double` | P/EBIT |
| `evEbit` | `Double` | EV/EBIT |
| `margemBruta` | `Double` | Gross margin (%) |
| `margemEbit` | `Double` | EBIT margin (%) |
| `margemLiquida` | `Double` | Net margin (%) |
| `pSr` | `Double` | PSR |
| `pCapitalGiro` | `Double` | P/Capital de Giro |
| `pAtivoCirculante` | `Double` | P/Ativo Circulante |
| `giroAtivos` | `Double` | Asset turnover |
| `roe` | `Double` | ROE (%) |
| `roa` | `Double` | ROA (%) |
| `roic` | `Double` | ROIC (%) |
| `dividaLiquidaPatrimonioLiquido` | `Double` | Net debt / equity |
| `dividaLiquidaEbit` | `Double` | Net debt / EBIT |
| `plAtivo` | `Double` | PL/Ativo |
| `passivoAtivo` | `Double` | Passivo/Ativo |
| `liquidezCorrente` | `Double` | Current ratio |
| `pegRatio` | `Double` | PEG ratio |
| `receitasCagr5` | `Double` | Revenue CAGR 5 years (%) |
| `lucrosCagr5` | `Double` | Earnings CAGR 5 years (%) |
| `liquidezMediaDiaria` | `Double` | Average daily liquidity |
| `vpa` | `Double` | Book value per share |
| `lpa` | `Double` | Earnings per share |
| `valorMercado` | `Double` | Market cap |
| `classificacaoB3` | `ClassificacaoB3` | B3 sector/subsector/segment (FK) |

---

## Repository — `StockRepository`

```
src/main/java/com/moselli/fundamentos/repository/StockRepository.java
```

Extends `ReactorCrudRepository<Stock, String>`. Provides reactive CRUD; Micronaut Data generates the implementation.

---

## Model — `Fundamento`

```
src/main/java/com/moselli/fundamentos/model/Fundamento.java
```

Computed from a `Stock` via `Fundamento.from(Stock)`. Adds derived fields.

`calculateFields()` is called automatically by `from()` and normalises percentage fields (DY, ROE, CAGR ÷ 100) before computing valuations.

### Derived fields

| Field | Formula | Notes |
|---|---|---|
| `dpa` | `price × dy` | Annual dividend per share |
| `valuationBazin` | `dpa / 0.06` | 6% fair-value floor |
| `descontoBazin` | `(valuationBazin - price) / valuationBazin` | Positive = undervalued |
| `valuationGraham` | `√(22.5 × lpa × vpa)` | 0 when `lpa × vpa < 0` |
| `descontoGraham` | `(valuationGraham - price) / valuationGraham` | |
| `valuationGordon` | `(dpa × (1 + lucrosCagr5)) / 0.30` | 30% discount rate |
| `descontoGordon` | `(valuationGordon - price) / valuationGordon` | |
| `payout` | `dpa / lpa` | Can exceed 1 |
| `crescimentoEsperado` | `(1 - payout) × roe` | Sustainable growth rate |
| `mediaCrescimento` | `(lucrosCagr5 + crescimentoEsperado) / 2` | |

---

## Service — `FundamentosService`

Relevant methods for the stock flow:

| Method | Description |
|---|---|
| `updateStocks()` | Deletes all rows in `stock`, fetches from StatusInvest, persists |
| `findAll()` | Returns `Flux<Stock>` from the repository |

---

## API Endpoints

| Method | Path                 | Response | Description |
|---|----------------------|---|---|
| `GET` | `/api/stocks`        | `List<Fundamento>` | Stocks with computed valuation fields |
| `POST` | `/api/stocks/reload` | `String` | Triggers a full stock data refresh from StatusInvest |

---

## Sector Classification — `ClassificacaoB3`

`Stock` has a `@OneToOne` join to `ClassificacaoB3` (keyed on `ticker`). This table holds
B3's official sector, subsector, and segment classification for each ticker.
When present, `Fundamento.from()` propagates `setorEconomico`, `subsetor`, and `segmento`
to the response.
