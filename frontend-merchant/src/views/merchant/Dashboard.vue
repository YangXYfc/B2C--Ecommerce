<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/mock/dashboard'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const stats = ref({ todaySales: 0, todayOrders: 0, pendingShip: 0, activeProducts: 0 })

const lineOption = ref({})
const pieOption = ref({})

onMounted(async () => {
  const res: any = await getDashboard()
  const { todaySales, todayOrders, pendingShip, activeProducts, salesTrend, orderDistribution } = res.data
  stats.value = { todaySales, todayOrders, pendingShip, activeProducts }

  lineOption.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: salesTrend.map((s: any) => s.date) },
    yAxis: { type: 'value' },
    series: [{ data: salesTrend.map((s: any) => s.amount), type: 'line', smooth: true, areaStyle: { opacity: 0.3 } }],
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
  }

  pieOption.value = {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
      data: orderDistribution.map((o: any) => ({ name: o.name, value: o.value })),
    }],
  }
})
</script>

<template>
  <div>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">今日销售额</div>
        <div class="value">¥{{ stats.todaySales.toLocaleString() }}</div>
      </div>
      <div class="stat-card">
        <div class="label">今日订单数</div>
        <div class="value">{{ stats.todayOrders }}</div>
      </div>
      <div class="stat-card">
        <div class="label">待发货订单</div>
        <div class="value" style="color:#e6a23c">{{ stats.pendingShip }}</div>
      </div>
      <div class="stat-card">
        <div class="label">在售商品数</div>
        <div class="value">{{ stats.activeProducts }}</div>
      </div>
    </div>
    <div class="chart-section">
      <h3>近7日销售额趋势</h3>
      <VChart :option="lineOption" style="height:300px" autoresize />
    </div>
    <div class="chart-section">
      <h3>订单状态分布</h3>
      <VChart :option="pieOption" style="height:300px" autoresize />
    </div>
  </div>
</template>
