<template>
  <div class="app">
    <header class="toolbar">
      <div class="toolbar-left">
        <h1>Fundamentos B3</h1>
        <div class="tabs">
          <button
            :class="['tab-btn', { active: activeTab === 'stocks' }]"
            @click="switchTab('stocks')"
          >Ações</button>
          <button
            :class="['tab-btn', { active: activeTab === 'fiis' }]"
            @click="switchTab('fiis')"
          >FIIs</button>
        </div>
        <span v-if="!loading" class="item-count">{{ sorted.length }} {{ activeTab === 'stocks' ? 'ações' : 'FIIs' }}</span>
      </div>
      <div class="toolbar-right">
        <select v-model="sectorFilter" class="sector-select">
          <option value="">Todos os {{ activeTab === 'stocks' ? 'setores' : 'segmentos' }}</option>
          <option v-for="s in sectors" :key="s" :value="s">{{ s }}</option>
        </select>
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
      <!-- Ações table -->
      <table v-if="activeTab === 'stocks'">
        <thead>
          <tr>
            <th
              v-for="col in stockColumns"
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
            <td class="sector">{{ stock.setorEconomico || '—' }}</td>
            <td class="number">{{ fmt(stock.price, 'currency') }}</td>
            <td class="number">{{ fmt(stock.pl, 'number') }}</td>
            <td class="number">{{ fmt(stock.pVp, 'number') }}</td>
            <td class="number">{{ fmt(stock.dy, 'percent') }}</td>
            <td class="number">{{ fmt(stock.roe, 'percent') }}</td>
            <td class="number">{{ fmt(stock.lpa, 'number') }}</td>
            <td class="number">{{ fmt(stock.vpa, 'number') }}</td>
            <td class="number">{{ fmt(stock.dpa, 'currency') }}</td>
            <td class="number">{{ fmt(stock.payout, 'percent') }}</td>
            <td class="number">{{ fmt(stock.lucrosCagr5, 'percent') }}</td>
            <td class="number">{{ fmt(stock.crescimentoEsperado, 'percent') }}</td>
            <td class="number">{{ fmt(stock.mediaCrescimento, 'percent') }}</td>
            <td class="number">{{ fmt(stock.pegRatio, 'number') }}</td>
            <td class="number">{{ fmt(stock.valorMercado, 'compact') }}</td>
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
            <td :colspan="stockColumns.length" class="empty">Nenhuma ação encontrada.</td>
          </tr>
        </tbody>
      </table>

      <!-- FIIs table -->
      <table v-else>
        <thead>
          <tr>
            <th
              v-for="col in fiiColumns"
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
          <tr v-for="fii in sorted" :key="fii.ticker">
            <td class="ticker">{{ fii.ticker }}</td>
            <td class="company">{{ fii.companyName }}</td>
            <td class="sector">{{ fii.sector || '—' }}</td>
            <td class="number">{{ fmt(fii.price, 'currency') }}</td>
            <td class="number">{{ fmt(fii.dy, 'percent') }}</td>
            <td class="number">{{ fmt(fii.pVp, 'number') }}</td>
            <td class="number">{{ fmt(fii.lastDividend, 'currency') }}</td>
            <td class="number">{{ fmt(fii.percentualEmCaixa, 'percent') }}</td>
            <td class="number">{{ fmt(fii.dividendoCagr3Anos, 'percent') }}</td>
            <td class="number">{{ fmt(fii.cotaCagr3Anos, 'percent') }}</td>
            <td class="number">{{ fmt(fii.liquidezMediaDiaria, 'compact') }}</td>
            <td class="number">{{ fmt(fii.valuationBazinFII, 'currency') }}</td>
            <td :class="['number', 'discount', discountClass(fii.descontoBazinFII)]">
              {{ fmt(fii.descontoBazinFII, 'discount') }}
            </td>
            <td class="number">{{ fmt(fii.valuationGordonFII, 'currency') }}</td>
            <td :class="['number', 'discount', discountClass(fii.descontoGordonFII)]">
              {{ fmt(fii.descontoGordonFII, 'discount') }}
            </td>
          </tr>
          <tr v-if="sorted.length === 0">
            <td :colspan="fiiColumns.length" class="empty">Nenhum FII encontrado.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const activeTab = ref('stocks')
const stocks = ref([])
const fiis = ref([])
const loading = ref(false)
const reloading = ref(false)
const error = ref(null)
const filter = ref('')
const sectorFilter = ref('')
const sortColumn = ref('ticker')
const sortAsc = ref(true)

const currentData = computed(() => activeTab.value === 'stocks' ? stocks.value : fiis.value)

const sectors = computed(() => {
  if (activeTab.value === 'stocks') {
    const set = new Set(stocks.value.map(s => s.setorEconomico).filter(Boolean))
    return Array.from(set).sort()
  } else {
    const set = new Set(fiis.value.map(f => f.sector).filter(Boolean))
    return Array.from(set).sort()
  }
})

const stockColumns = [
  { key: 'ticker',              label: 'Ticker' },
  { key: 'companyName',         label: 'Empresa' },
  { key: 'setorEconomico',      label: 'Setor' },
  { key: 'price',               label: 'Preço' },
  { key: 'pl',                  label: 'P/L' },
  { key: 'pVp',                 label: 'P/VP' },
  { key: 'dy',                  label: 'DY' },
  { key: 'roe',                 label: 'ROE' },
  { key: 'lpa',                 label: 'LPA' },
  { key: 'vpa',                 label: 'VPA' },
  { key: 'dpa',                 label: 'DPA' },
  { key: 'payout',              label: 'Payout' },
  { key: 'lucrosCagr5',         label: 'CAGR L 5A' },
  { key: 'crescimentoEsperado', label: 'Cresc. Esp.' },
  { key: 'mediaCrescimento',    label: 'Méd. Cresc.' },
  { key: 'pegRatio',            label: 'PEG' },
  { key: 'valorMercado',        label: 'Valor Mercado' },
  { key: 'valuationBazin',      label: 'Bazin' },
  { key: 'descontoBazin',       label: 'Desc. Bazin' },
  { key: 'valuationGraham',     label: 'Graham' },
  { key: 'descontoGraham',      label: 'Desc. Graham' },
  { key: 'valuationGordon',     label: 'Gordon' },
  { key: 'descontoGordon',      label: 'Desc. Gordon' },
]

const fiiColumns = [
  { key: 'ticker',              label: 'Ticker' },
  { key: 'companyName',         label: 'Fundo' },
  { key: 'sector',              label: 'Segmento' },
  { key: 'price',               label: 'Cotação' },
  { key: 'dy',                  label: 'DY' },
  { key: 'pVp',                 label: 'P/VP' },
  { key: 'lastDividend',        label: 'Últ. Div.' },
  { key: 'percentualEmCaixa',   label: 'Caixa %' },
  { key: 'dividendoCagr3Anos',  label: 'CAGR Div 3A' },
  { key: 'cotaCagr3Anos',       label: 'CAGR Cota 3A' },
  { key: 'liquidezMediaDiaria', label: 'Liquidez' },
  { key: 'valuationBazinFII',   label: 'Bazin FII' },
  { key: 'descontoBazinFII',    label: 'Desc. Bazin' },
  { key: 'valuationGordonFII',  label: 'Gordon FII' },
  { key: 'descontoGordonFII',   label: 'Desc. Gordon' },
]

async function fetchStocks() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch('/api/stocks')
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    stocks.value = await res.json()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function fetchFIIs() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch('/api/fiis')
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    fiis.value = await res.json()
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
    const url = activeTab.value === 'stocks' ? '/api/stocks/reload' : '/api/fiis/reload'
    const res = await fetch(url, { method: 'POST' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    if (activeTab.value === 'stocks') await fetchStocks()
    else await fetchFIIs()
  } catch (e) {
    error.value = e.message
  } finally {
    reloading.value = false
  }
}

function switchTab(tab) {
  activeTab.value = tab
  filter.value = ''
  sectorFilter.value = ''
  sortColumn.value = 'ticker'
  sortAsc.value = true
  if (tab === 'stocks' && stocks.value.length === 0) fetchStocks()
  if (tab === 'fiis' && fiis.value.length === 0) fetchFIIs()
}

function setSort(key) {
  if (sortColumn.value === key) sortAsc.value = !sortAsc.value
  else { sortColumn.value = key; sortAsc.value = true }
}

const filtered = computed(() => {
  const q = filter.value.toLowerCase()
  const sec = sectorFilter.value
  return currentData.value.filter(item => {
    const matchText = !q || item.ticker?.toLowerCase().includes(q) || item.companyName?.toLowerCase().includes(q)
    const sectorField = activeTab.value === 'stocks' ? item.setorEconomico : item.sector
    const matchSector = !sec || sectorField === sec
    return matchText && matchSector
  })
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
    case 'compact': {
      if (value >= 1e9) return `R$ ${(value / 1e9).toFixed(1)}B`
      if (value >= 1e6) return `R$ ${(value / 1e6).toFixed(0)}M`
      return `R$ ${value.toFixed(0)}`
    }
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

.toolbar-left { display: flex; align-items: center; gap: 12px; }

h1 { font-size: 18px; font-weight: 600; color: #7c9ef5; letter-spacing: 0.3px; }

.tabs { display: flex; gap: 2px; }

.tab-btn {
  padding: 5px 14px;
  border: 1px solid #2d3148;
  border-radius: 6px;
  background: transparent;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}
.tab-btn:hover { color: #94a3b8; border-color: #3d4260; }
.tab-btn.active { background: #7c9ef5; color: #0f1117; border-color: #7c9ef5; font-weight: 600; }

.item-count { font-size: 12px; color: #64748b; }

.toolbar-right { display: flex; align-items: center; gap: 10px; }

.sector-select {
  padding: 7px 10px;
  border: 1px solid #2d3148;
  border-radius: 6px;
  background: #0f1117;
  color: #e2e8f0;
  font-size: 13px;
  outline: none;
  cursor: pointer;
  transition: border-color 0.15s;
}
.sector-select:focus { border-color: #7c9ef5; }

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
td.sector  { color: #94a3b8; max-width: 160px; overflow: hidden; text-overflow: ellipsis; }
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
