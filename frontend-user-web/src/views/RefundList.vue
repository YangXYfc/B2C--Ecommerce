<template>
  <div class="page-container">
    <div class="page-card">
      <h2>退款/售后</h2>
      <div v-loading="loading">
        <div v-for="refund in refunds" :key="refund.id" class="refund-card" @click="$router.push(`/refunds/${refund.id}`)">
          <div class="refund-header">
            <span>退款单号：{{ refund.refundNo }}</span>
            <el-tag :type="REFUND_STATUS[refund.status]?.type" size="small">
              {{ REFUND_STATUS[refund.status]?.label }}
            </el-tag>
          </div>
          <div class="refund-body">
            <p>退款原因：{{ refund.reason }}</p>
            <p>退款金额：<span class="price">¥{{ formatPrice(refund.amount) }}</span></p>
            <p class="time">申请时间：{{ refund.createdAt }}</p>
          </div>
        </div>

        <div v-if="!loading && !refunds.length" class="empty-state">
          <p>暂无退款记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRefunds } from '@/api'
import { formatPrice, REFUND_STATUS } from '@/utils/format'

const loading = ref(false)
const refunds = ref([])

onMounted(async () => {
  loading.value = true
  try {
    refunds.value = await getRefunds()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
h2 { margin-bottom: 20px; }

.refund-card {
  border: 1px solid var(--jd-border);
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.refund-card:hover { border-color: var(--jd-red); }

.refund-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  font-size: 13px;
}

.refund-body { padding: 12px 16px; font-size: 14px; line-height: 1.8; }
.refund-body .time { color: var(--jd-text-light); font-size: 13px; }
</style>
