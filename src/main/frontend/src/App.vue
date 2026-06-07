<template>
  <div class="app">
    <header class="toolbar">
      <div class="toolbar-left">
        <h1>Fundamentos B3</h1>
        <span v-if="!loading" class="stock-count">{{ sorted.length }} ações</span>
      </div>
      <div class="toolbar-right">
        <input
          v-model="filter"
          class="filter-input"
          placeholder="Filtrar por ticker ou empresa..."
        />
        <button class="btn-reload" :disabled="reloading" @click="reloadData">
          {{ reloading ? 'Atualizando...' : 'Atualizar Dados' }}
        </button>
      </div>
    </header>

    <div v-if="error" class="error-banner">
      Erro ao carregar dados: {{ error }}
    </div>

    <div v-if="loading" class="loading">Carregando...</div>

    <div v-else class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th
              v-for="col in columns"
              :key="col.key"
              :class="['sortable', { active: sortColumn === col.key }]"
              @click="setSort(col.key)"
            >
              {{ col.label }}
              <span class="sort-arrow">{{ sortColumn === col.key ? (sortAsc ? '▲' : '▼') : '⇅' }}</span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stock in sorted" :key="stock.ticker">
            <td class="ticker">{{ stock.ticker }}</td>
            <td class="company">{{ stock.companyName }}</td>
            <td class="number">{{ fmt(stock.price, 'currency') }}</td>
            <td class="number">{{ fmt(stock.pl, 'number') }}</td>
            <td class="number">{{ fmt(stock.pVp, 'number') }}</td>
            <td class="number">{{ fmt(stock.dy, 'percent') }}</td>
            <td class="number">{{ fmt(stock.roe, 'percent') }}</td>
            <td class="number">{{ fmt(stock.valuationBazin, 'currency') }}</td>
            <td :class="['number', 'discount', discountClass(stock.descontoBazin)]">
              {{ fmt(stock.descontoBazin, 'discount') }}
            </td>
            <td class="number">{{ fmt(stock.valuationGraham, 'currency') }}</td>
            <td :class="['number', 'discount', discountClass(stock.descontoGraham)]">
              {{ fmt(stock.descontoGraham, 'discount') }}
            </td>
            <td class="number">{{ fmt(stock.valuationGordon, 'currency') }}</td>
            <td :class="['number', 'discount', discountClass(stock.descontoGordon)]">
              {{ fmt(stock.descontoGordon, 'discount') }}
            </td>
          </tr>
          <tr v-if="sorted.length === 0">
            <td :colspan="columns.length" class="empty">Nenhuma ação encontrada.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const stocks = ref([])
const loading = ref(false)
const reloading = ref(false)
const error = ref(null)
const filter = ref('')
const sortColumn = ref('ticker')
const sortAsc = ref(true)

const columns = [
  { key: 'ticker',          label: 'Ticker' },
  { key: 'companyName',     label: 'Empresa' },
  { key: 'price',           label: 'Preço',        format: 'currency' },
  { key: 'pl',              label: 'P/L',           format: 'number' },
  { key: 'pVp',             label: 'P/VP',          format: 'number' },
  { key: 'dy',              label: 'DY',            format: 'percent' },
  { key: 'roe',             label: 'ROE',           format: 'percent' },
  { key: 'valuationBazin',  label: 'Bazin',         format: 'currency' },
  { key: 'descontoBazin',   label: 'Desc. Bazin',   format: 'discount' },
  { key: 'valuationGraham', label: 'Graham',        format: 'currency' },
  { key: 'descontoGraham',  label: 'Desc. Graham',  format: 'discount' },
  { key: 'valuationGordon', label: 'Gordon',        format: 'currency' },
  { key: 'descontoGordon',  label: 'Desc. Gordon',  format: 'discount' },
]

async function fetchStocks() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch('/api/all')
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    stocks.value = await res.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function reloadData() {
  reloading.value = true
  error.value = null
  try {
    const res = await fetch('/api/reload', { method: 'POST' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    await fetchStocks()
  } catch (e) {
    error.value = e.message
  } finally {
    reloading.value = false
  }
}

function setSort(key) {
  if (sortColumn.value === key) sortAsc.value = !sortAsc.value
  else { sortColumn.value = key; sortAsc.value = true }
}

const filtered = computed(() => {
  const q = filter.value.toLowerCase()
  if (!q) return stocks.value
  return stocks.value.filter(s =>
    s.ticker?.toLowerCase().includes(q) || s.companyName?.toLowerCase().includes(q)
  )
})

const sorted = computed(() => {
  const col = sortColumn.value
  return [...filtered.value].sort((a, b) => {
    const av = a[col] ?? (typeof b[col] === 'number' ? -Infinity : '')
    const bv = b[col] ?? (typeof a[col] === 'number' ? -Infinity : '')
    if (av === bv) return 0
    const result = av > bv ? 1 : -1
    return sortAsc.value ? result : -result
  })
})

function fmt(value, format) {
  if (value === null || value === undefined || (typeof value === 'number' && !isFinite(value))) return '—'
  switch (format) {
    case 'currency': return `R$ ${value.toFixed(2)}`
    case 'percent':  return `${(value * 100).toFixed(1)}%`
    case 'discount': return `${(value * 100).toFixed(1)}%`
    case 'number':   return value.toFixed(2)
    default:         return value
  }
}

function discountClass(value) {
  if (value == null || !isFinite(value)) return ''
  if (value >= 0.15) return 'positive'
  if (value <= -0.15) return 'negative'
  return 'neutral'
}

onMounted(fetchStocks)
</script>

<style>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: 'Segoe UI', system-ui, sans-serif;
  font-size: 13px;
  background: #0f1117;
  color: #e2e8f0;
  min-height: 100vh;
}

.app {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* ── Toolbar ── */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #1a1d27;
  border-bottom: 1px solid #2d3148;
  gap: 16px;
  flex-shrink: 0;
}

.toolbar-left { display: flex; align-items: baseline; gap: 12px; }

h1 { font-size: 18px; font-weight: 600; color: #7c9ef5; letter-spacing: 0.3px; }

.stock-count { font-size: 12px; color: #64748b; }

.toolbar-right { display: flex; align-items: center; gap: 10px; }

.filter-input {
  padding: 7px 12px;
  border: 1px solid #2d3148;
  border-radius: 6px;
  background: #0f1117;
  color: #e2e8f0;
  font-size: 13px;
  width: 260px;
  outline: none;
  transition: border-color 0.15s;
}
.filter-input:focus { border-color: #7c9ef5; }
.filter-input::placeholder { color: #475569; }

.btn-reload {
  padding: 7px 16px;
  background: #7c9ef5;
  color: #0f1117;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, opacity 0.15s;
}
.btn-reload:hover:not(:disabled) { background: #9ab4f8; }
.btn-reload:disabled { opacity: 0.5; cursor: default; }

/* ── States ── */
.error-banner {
  background: #3b1f1f;
  color: #f87171;
  padding: 10px 20px;
  font-size: 13px;
  border-bottom: 1px solid #7f1d1d;
  flex-shrink: 0;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  font-size: 15px;
  color: #64748b;
}

/* ── Table ── */
.table-wrapper {
  flex: 1;
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}

thead {
  position: sticky;
  top: 0;
  z-index: 1;
  background: #1a1d27;
}

th {
  padding: 10px 12px;
  text-align: left;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: #64748b;
  border-bottom: 2px solid #2d3148;
  white-space: nowrap;
  user-select: none;
}

th.sortable { cursor: pointer; }
th.sortable:hover { color: #94a3b8; }
th.active { color: #7c9ef5; }

.sort-arrow { margin-left: 4px; font-size: 10px; opacity: 0.7; }

tbody tr {
  border-bottom: 1px solid #1e2235;
  transition: background 0.08s;
}
tbody tr:hover { background: #161926; }

td {
  padding: 9px 12px;
  white-space: nowrap;
}

td.ticker { font-weight: 600; color: #7c9ef5; font-size: 13px; }
td.company { color: #94a3b8; max-width: 200px; overflow: hidden; text-overflow: ellipsis; }
td.number { text-align: right; font-variant-numeric: tabular-nums; }

/* ── Discount colors ── */
td.discount.positive { color: #4ade80; font-weight: 600; }
td.discount.negative { color: #f87171; font-weight: 600; }
td.discount.neutral  { color: #94a3b8; }

td.empty {
  text-align: center;
  color: #475569;
  padding: 40px;
}
</style>
