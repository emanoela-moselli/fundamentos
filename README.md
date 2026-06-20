# Fundamentos

A web app that replaces the `Monitor_de_Valuation_3.0.xlsx` and `Monitor_de_FII.xlsx` spreadsheets.
It fetches fundamentals data from [StatusInvest](https://statusinvest.com.br), persists it to PostgreSQL, computes valuation metrics, and serves them through a Vue frontend.

---

## Architecture

| Layer | Technology |
|---|---|
| Backend | Java 26, Micronaut, Hibernate Reactive, R2DBC |
| Database | PostgreSQL |
| Frontend | Vue 3, Vite |
| HTTP Client | Micronaut declarative client (`StatusInvestClient`) |

The app has two asset classes — **Stocks** (Ações) and **FIIs** — each with its own entity, repository, service logic, API endpoints, and frontend tab.

---

## Modules

### Stocks

See [`docs/stock-model.md`](docs/stock-model.md) for full details.

- Entity: `Stock` (table `stock`)
- Repository: `StockRepository`
- Model: `Fundamento` (adds valuation fields computed from raw `Stock` data)
- API: `GET /api/stocks/all`, `GET /api/stocks/raw`, `POST /api/stocks/reload`

### FIIs

See [`docs/fii-model-plan.md`](docs/fii-model-plan.md) for full details.

- Entity: `FIIData` (table `fii_data`)
- Repository: `FIIDataRepository`
- Model: `FII` (adds Gordon/Bazin valuation fields)
- API: `GET /api/fiis`, `POST /api/fiis/reload`

---

## Running locally

### Prerequisites

- Java 26+
- Docker (for PostgreSQL via Testcontainers, or run your own on port 5432)
- Node.js (bundled via frontend-maven-plugin)

### Start

```bash
./mvnw mn:run
```

The frontend is built and served as static resources by the backend. Open `http://localhost:8080`.

### Reload data

```bash
# Reload stocks
curl -X POST http://localhost:8080/api/stocks/reload

# Reload FIIs
curl -X POST http://localhost:8080/api/fiis/reload
```

---

## API Reference

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/stocks/all` | All stocks with computed valuations |
| `GET` | `/api/stocks/raw` | Raw stock data (no valuation fields) |
| `POST` | `/api/stocks/reload` | Fetch and persist latest stock data from StatusInvest |
| `GET` | `/api/fiis` | All FIIs with computed valuations |
| `POST` | `/api/fiis/reload` | Fetch and persist latest FII data from StatusInvest |

---

## Tests

```bash
# Unit + repository integration tests (requires Docker for PostgreSQL container)
./mvnw test

# External integration tests (calls real StatusInvest API, requires internet + Docker)
./mvnw test -Dgroups=external
```

---

## Micronaut 3.0.0 Documentation

- [User Guide](https://docs.micronaut.io/3.0.0/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.0.0/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.0.0/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature tomcat-server documentation

- [Micronaut Tomcat Server documentation](https://micronaut-projects.github.io/micronaut-servlet/1.0.x/guide/index.html#tomcat)

## Feature mockito documentation

- [https://site.mockito.org](https://site.mockito.org)

## Feature jdbc-hikari documentation

- [Micronaut Hikari JDBC Connection Pool documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#jdbc)

## Feature lombok documentation

- [Micronaut Project Lombok documentation](https://docs.micronaut.io/latest/guide/index.html#lombok)

- [https://projectlombok.org/features/all](https://projectlombok.org/features/all)

## Feature testcontainers documentation

- [https://www.testcontainers.org/](https://www.testcontainers.org/)
