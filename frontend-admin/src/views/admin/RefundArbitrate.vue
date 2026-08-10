<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRefundList, arbitrateRefund, getRefundDetail } from '@/api/mock/refund'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ refundNo: '', status: '' })

const arbDialog = ref(false)
const arbForm = ref({ action: 'approve', remark: '' })
const currentRefund = ref<any>(null)
const arbLoading = ref(false)

const statusMap: Record<number, string> = {
  0: '待审核', 1: '已通过', 2: '寄回中', 3: '退款完成', 4: '已拒绝', 5: '申诉中', 6: '平台支持', 7: '平台驳回',
}

async function fetchData() {
  loading.value = true
  const res: any = await getRefundList({ refundNo: query.value.refundNo || undefined, status: query.value.status })
  list.value = res.data.list
  loading.value = false
}

async function openArbitrate(row: any) {
  const res: any = await getRefundDetail(row.id)
  currentRefund.value = res.data
  arbForm.value = { action: 'approve', remark: '' }
  arbDialog.value = true
}

async function handleArbitrate() {
  arbLoading.value = true
  await arbitrateRefund(currentRefund.value.id, arbForm.value)
  ElMessage.success(arbForm.value.action === 'approve' ? '已支持用户退款' : '已驳回申诉')
  arbDialog.value = false
  arbLoading.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>退款仲裁</h2></div>
    <div class="search-bar">
      <el-input v-model="query.refundNo" placeholder="退款编号" clearable style="width:220px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width:140px">
        <el-option v-for="(v, k) in statusMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { refundNo: '', status: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="refundNo" label="退款编号" width="180" />
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="userName" label="用户" width="80" />
      <el-table-column prop="merchantName" label="商家" width="120" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="原因" width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 5 ? 'warning' : row.status === 6 ? 'success' : 'danger'" size="small">
            {{ statusMap[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.status === 5" size="small" type="warning" @click="openArbitrate(row)">仲裁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="arbDialog" title="退款仲裁" width="500px">
      <el-descriptions v-if="currentRefund" :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="退款编号">{{ currentRefund.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentRefund.userName }}</el-descriptions-item>
        <el-descriptions-item label="商家">{{ currentRefund.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ currentRefund.amount }}</el-descriptions-item>
        <el-descriptions-item label="原因">{{ currentRefund.reason }}</el-descriptions-item>
        <el-descriptions-item label="商家备注">{{ currentRefund.merchantRemark }}</el-descriptions-item>
        <el-descriptions-item label="申诉理由">{{ currentRefund.appealReason }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="arbForm" label-width="80px">
        <el-form-item label="仲裁结果">
          <el-radio-group v-model="arbForm.action">
            <el-radio value="approve">支持用户</el-radio>
            <el-radio value="reject">驳回申诉</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="arbForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="arbDialog = false">取消</el-button>
        <el-button type="primary" :loading="arbLoading" @click="handleArbitrate">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
