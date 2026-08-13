<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/mock/dashboard'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const stats = ref({ totalUsers: 0, totalMerchants: 0, totalOrders: 0, totalSales: 0 })
const lineOption = ref({})
const pieOption = ref({})

onMounted(async () => {
  const res: any = await getDashboard()
  const { totalUsers, totalMerchants, totalOrders, totalSales, salesTrend, categoryDistribution } = res.data
  stats.value = { totalUsers, totalMerchants, totalOrders, totalSales }

  lineOption.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: salesTrend.map((s: any) => s.date) },
    yAxis: { type: 'value' },
    series: [{ data: salesTrend.map((s: any) => s.amount), type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#409eff' } }],
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
  }

  pieOption.value = {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
      data: categoryDistribution.map((c: any) => ({ name: c.name, value: c.value })),
    }],
  }
})
</script>

<template>
  <div>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">平台用户数</div>
        <div class="value">{{ stats.totalUsers }}</div>
      </div>
      <div class="stat-card">
        <div class="label">入驻商家数</div>
        <div class="value">{{ stats.totalMerchants }}</div>
      </div>
      <div class="stat-card">
        <div class="label">总订单数</div>
        <div class="value">{{ stats.totalOrders }}</div>
      </div>
      <div class="stat-card">
        <div class="label">总销售额</div>
        <div class="value">¥{{ stats.totalSales.toLocaleString() }}</div>
      </div>
    </div>
    <div class="chart-section">
      <h3>近7日平台销售额</h3>
      <VChart :option="lineOption" style="height:300px" autoresize />
    </div>
    <div class="chart-section">
      <h3>各分类商品占比</h3>
      <VChart :option="pieOption" style="height:300px" autoresize />
    </div>
  </div>
</template>
