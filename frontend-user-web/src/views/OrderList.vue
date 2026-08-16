<template>
  <div class="page-container">
    <div class="page-card">
      <h2>我的订单</h2>
      <el-tabs v-model="activeStatus" @tab-change="loadOrders">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="已发货" name="2" />
        <el-tab-pane label="已收货" name="3" />
        <el-tab-pane label="已取消" name="5" />
      </el-tabs>

      <div v-loading="loading">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span>订单号：{{ order.orderNo }}</span>
            <span>{{ order.createdAt }}</span>
            <el-tag :type="ORDER_STATUS[order.status]?.type" size="small">
              {{ ORDER_STATUS[order.status]?.label }}
            </el-tag>
          </div>
          <div class="order-shop">{{ order.merchantName }}</div>
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <img :src="item.productImage" />
            <div class="item-info">
              <p>{{ item.productName }}</p>
              <p class="sku">{{ item.skuName }}</p>
            </div>
            <span class="price">¥{{ formatPrice(item.unitPrice) }}</span>
            <span>x{{ item.quantity }}</span>
          </div>
          <div class="order-footer">
            <span>合计：<span class="price">¥{{ formatPrice(order.totalAmount) }}</span></span>
            <div class="order-actions">
              <el-button size="small" @click="$router.push(`/orders/${order.id}`)">查看详情</el-button>
              <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order.id)">去支付</el-button>
              <el-button v-if="order.status === 0" size="small" @click="handleCancel(order.id)">取消订单</el-button>
              <el-button v-if="order.status === 2" type="primary" size="small" @click="handleConfirm(order.id)">确认收货</el-button>
              <el-button v-if="order.status === 3" type="primary" size="small" @click="$router.push(`/orders/${order.id}/review`)">去评价</el-button>
            </div>
          </div>
        </div>

        <div v-if="!loading && !orders.length" class="empty-state">
          <el-icon><Document /></el-icon>
          <p>暂无订单</p>
          <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getOrders, payOrder, cancelOrder, confirmReceipt } from '@/api'
import { formatPrice, ORDER_STATUS } from '@/utils/format'

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')

async function loadOrders() {
  loading.value = true
  try {
    const params = activeStatus.value !== '' ? { status: activeStatus.value } : {}
    orders.value = await getOrders(params)
  } finally {
    loading.value = false
  }
}

async function handlePay(id) {
  await payOrder(id)
  ElMessage.success('支付成功')
  await loadOrders()
}

async function handleCancel(id) {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示')
  await cancelOrder(id, { reason: '不想买了' })
  ElMessage.success('订单已取消')
  await loadOrders()
}

async function handleConfirm(id) {
  await ElMessageBox.confirm('确认已收到商品吗？', '确认收货')
  await confirmReceipt(id)
  ElMessage.success('已确认收货')
  await loadOrders()
}

onMounted(loadOrders)
</script>

<style scoped>
h2 { margin-bottom: 16px; }

.order-card {
  border: 1px solid var(--jd-border);
  border-radius: 4px;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: #fafafa;
  font-size: 13px;
  color: var(--jd-text-light);
}

.order-header .el-tag { margin-left: auto; }

.order-shop {
  padding: 8px 16px;
  font-weight: 500;
  border-bottom: 1px solid var(--jd-border);
}

.order-item {
  display: grid;
  grid-template-columns: 60px 1fr 80px 40px;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

.order-item img { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.order-item .sku { font-size: 12px; color: var(--jd-text-light); }

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid var(--jd-border);
}

.order-actions { display: flex; gap: 8px; }
</style>
