<template>
  <div class="page-container">
    <div class="page-card" v-loading="loading">
      <template v-if="refund">
        <div class="detail-header">
          <h2>退款详情</h2>
          <el-tag :type="REFUND_STATUS[refund.status]?.type">{{ REFUND_STATUS[refund.status]?.label }}</el-tag>
        </div>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="退款单号">{{ refund.refundNo }}</el-descriptions-item>
          <el-descriptions-item label="关联订单">{{ refund.order?.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="店铺">{{ refund.merchantName }}</el-descriptions-item>
          <el-descriptions-item label="退款原因">{{ refund.reason }}</el-descriptions-item>
          <el-descriptions-item label="详细说明">{{ refund.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="退款金额">
            <span class="price">¥{{ formatPrice(refund.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ refund.createdAt }}</el-descriptions-item>
          <el-descriptions-item v-if="refund.merchantRemark" label="商家备注">{{ refund.merchantRemark }}</el-descriptions-item>
          <el-descriptions-item v-if="refund.returnLogisticsNo" label="退货物流">
            {{ refund.returnLogisticsCompany }} {{ refund.returnLogisticsNo }}
          </el-descriptions-item>
          <el-descriptions-item v-if="refund.appealReason" label="申诉原因">{{ refund.appealReason }}</el-descriptions-item>
        </el-descriptions>

        <!-- 填写退货物流 -->
        <div v-if="refund.status === 1" class="action-section">
          <h3>填写退货物流</h3>
          <el-form :model="logisticsForm" label-width="100px" style="max-width: 480px;">
            <el-form-item label="物流公司">
              <el-input v-model="logisticsForm.logisticsCompany" placeholder="如：顺丰速运" />
            </el-form-item>
            <el-form-item label="物流单号">
              <el-input v-model="logisticsForm.logisticsNo" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmitLogistics">提交</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 申诉 -->
        <div v-if="refund.status === 4" class="action-section">
          <h3>申请平台介入</h3>
          <el-form :model="appealForm" label-width="100px" style="max-width: 480px;">
            <el-form-item label="申诉原因">
              <el-input v-model="appealForm.reason" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAppeal">提交申诉</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="action-bar">
          <el-button @click="$router.push('/refunds')">返回列表</el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRefundDetail, submitReturnLogistics, appealRefund } from '@/api'
import { formatPrice, REFUND_STATUS } from '@/utils/format'

const route = useRoute()
const loading = ref(false)
const refund = ref(null)
const logisticsForm = reactive({ logisticsCompany: '', logisticsNo: '' })
const appealForm = reactive({ reason: '' })

async function loadRefund() {
  loading.value = true
  try {
    refund.value = await getRefundDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handleSubmitLogistics() {
  if (!logisticsForm.logisticsCompany || !logisticsForm.logisticsNo) {
    ElMessage.warning('请填写完整物流信息')
    return
  }
  await submitReturnLogistics(refund.value.id, logisticsForm)
  ElMessage.success('退货物流已提交')
  await loadRefund()
}

async function handleAppeal() {
  if (!appealForm.reason.trim()) {
    ElMessage.warning('请填写申诉原因')
    return
  }
  await appealRefund(refund.value.id, appealForm)
  ElMessage.success('申诉已提交，等待平台处理')
  await loadRefund()
}

onMounted(loadRefund)
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.action-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--jd-border);
}

.action-section h3 { font-size: 16px; margin-bottom: 16px; }
.action-bar { margin-top: 24px; }
</style>
