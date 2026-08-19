<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/dashboard'

const stats = ref({ userCount: 0, merchantCount: 0, orderCount: 0, salesAmount: 0 })

onMounted(async () => {
  const res: any = await getDashboard()
  stats.value = res.data
})
</script>

<template>
  <div>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">平台用户数</div>
        <div class="value">{{ stats.userCount }}</div>
      </div>
      <div class="stat-card">
        <div class="label">入驻商家数</div>
        <div class="value">{{ stats.merchantCount }}</div>
      </div>
      <div class="stat-card">
        <div class="label">总订单数</div>
        <div class="value">{{ stats.orderCount }}</div>
      </div>
      <div class="stat-card">
        <div class="label">总销售额</div>
        <div class="value">¥{{ Number(stats.salesAmount).toLocaleString() }}</div>
      </div>
    </div>
  </div>
</template>
