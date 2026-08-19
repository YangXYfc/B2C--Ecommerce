<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/dashboard'

const stats = ref({ salesAmount: 0, orderCount: 0, pendingShipmentCount: 0 })

onMounted(async () => {
  const res: any = await getDashboard()
  stats.value = res.data
})
</script>

<template>
  <div>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">累计销售额</div>
        <div class="value">¥{{ Number(stats.salesAmount).toLocaleString() }}</div>
      </div>
      <div class="stat-card">
        <div class="label">累计订单数</div>
        <div class="value">{{ stats.orderCount }}</div>
      </div>
      <div class="stat-card">
        <div class="label">待发货订单</div>
        <div class="value" style="color:#e6a23c">{{ stats.pendingShipmentCount }}</div>
      </div>
    </div>
  </div>
</template>
