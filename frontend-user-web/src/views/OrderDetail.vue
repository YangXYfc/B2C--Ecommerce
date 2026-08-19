<template>
  <div class="page-container">
    <div class="page-card" v-loading="loading">
      <template v-if="order">
        <div class="detail-header">
          <h2>订单详情</h2>
          <el-tag :type="ORDER_STATUS[order.status]?.type">{{ ORDER_STATUS[order.status]?.label }}</el-tag>
        </div>

        <el-descriptions :column="2" border class="info-block">
          <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ order.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="店铺">{{ order.merchantName }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span class="price">¥{{ formatPrice(order.totalAmount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="order.payTime" label="支付时间">{{ order.payTime }}</el-descriptions-item>
          <el-descriptions-item v-if="order.logisticsNo" label="物流信息">
            {{ order.logisticsCompany }} {{ order.logisticsNo }}
          </el-descriptions-item>
        </el-descriptions>

        <h3 class="block-title">收货信息</h3>
        <div class="address-block" v-if="order.addressSnapshot">
          {{ order.addressSnapshot.name }} {{ order.addressSnapshot.phone }}<br />
          {{ order.addressSnapshot.province }}{{ order.addressSnapshot.city }}{{ order.addressSnapshot.district }}
          {{ order.addressSnapshot.detail }}
        </div>

        <h3 class="block-title">商品信息</h3>
        <div v-for="item in order.items" :key="item.id" class="order-item">
          <img :src="item.productImage" />
          <div class="item-info">
            <p>{{ item.productName }}</p>
            <p class="sku">{{ item.skuName }}</p>
          </div>
          <span class="price">¥{{ formatPrice(item.unitPrice) }}</span>
          <span>x{{ item.quantity }}</span>
          <span class="price">¥{{ formatPrice(item.subtotal) }}</span>
        </div>

        <div class="action-bar">
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">模拟支付</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 2" type="primary" @click="handleConfirm">确认收货</el-button>
          <el-button v-if="order.status === 3" type="primary" @click="$router.push(`/orders/${order.id}/review`)">去评价</el-button>
          <el-button v-if="[1, 2, 3].includes(order.status)" @click="showRefundDialog = true">申请退款</el-button>
          <el-button @click="$router.push('/orders')">返回列表</el-button>
        </div>
      </template>
    </div>

    <!-- 退款申请弹窗 -->
    <el-dialog v-model="showRefundDialog" title="申请退款" width="480px">
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款原因">
          <el-select v-model="refundForm.reason" placeholder="请选择">
            <el-option label="不想要了" value="不想要了" />
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="尺码/规格不符" value="尺码不符" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input v-model="refundForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRefund">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, payOrder, cancelOrder, confirmReceipt, createRefund } from '@/api'
import { formatPrice, ORDER_STATUS } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)
const showRefundDialog = ref(false)
const refundForm = reactive({ reason: '', description: '' })

async function loadOrder() {
  loading.value = true
  try {
    order.value = await getOrderDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  await payOrder(order.value.id)
  ElMessage.success('支付成功')
  await loadOrder()
}

async function handleCancel() {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示')
  await cancelOrder(order.value.id)
  ElMessage.success('订单已取消')
  await loadOrder()
}

async function handleConfirm() {
  await confirmReceipt(order.value.id)
  ElMessage.success('已确认收货')
  await loadOrder()
}

async function handleRefund() {
  if (!refundForm.reason) {
    ElMessage.warning('请选择退款原因')
    return
  }
  await createRefund({
    orderId: order.value.id,
    amount: order.value.payAmount || order.value.totalAmount,
    ...refundForm,
  })
  ElMessage.success('退款申请已提交')
  showRefundDialog.value = false
  router.push('/refunds')
}

onMounted(loadOrder)
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.info-block { margin-bottom: 20px; }
.block-title { font-size: 16px; margin: 20px 0 12px; }

.address-block {
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 4px;
  line-height: 1.8;
  font-size: 14px;
}

.order-item {
  display: grid;
  grid-template-columns: 60px 1fr 80px 40px 80px;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--jd-border);
}

.order-item img { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.order-item .sku { font-size: 12px; color: var(--jd-text-light); }

.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--jd-border);
}
</style>
