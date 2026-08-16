<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRefundList, auditRefund, confirmReturn, refundStatusMap } from '@/api/refund'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ status: '' })

const auditDialog = ref(false)
const auditForm = ref({ action: 'approve', remark: '' })
const currentRefund = ref<any>(null)
const auditLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getRefundList({ status: query.value.status || undefined })
  list.value = res.data.list
  loading.value = false
}

function getStatusType(s: number) {
  if ([1, 2, 3, 6].includes(s)) return 'success'
  if ([4, 7].includes(s)) return 'danger'
  if (s === 5) return 'warning'
  return 'info'
}

function openAudit(row: any) {
  currentRefund.value = row
  auditForm.value = { action: 'approve', remark: '' }
  auditDialog.value = true
}

async function handleAudit() {
  auditLoading.value = true
  await auditRefund(currentRefund.value.id, {
    approved: auditForm.value.action === 'approve',
    remark: auditForm.value.remark,
  })
  ElMessage.success(auditForm.value.action === 'approve' ? '已同意退款' : '已拒绝退款')
  auditDialog.value = false
  auditLoading.value = false
  fetchData()
}

async function handleConfirmReturn(id: number) {
  await confirmReturn(id)
  ElMessage.success('已确认收货，退款完成')
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>退款处理</h2></div>
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="状态" clearable style="width:140px">
        <el-option v-for="(v, k) in refundStatusMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { status: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="refundNo" label="退款编号" width="180" />
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="原因" width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">{{ refundStatusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="warning" @click="openAudit(row)">审核</el-button>
          <el-button v-if="row.status === 2" size="small" type="success" @click="handleConfirmReturn(row.id)">确认收货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditDialog" title="退款审核" width="450px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.action">
            <el-radio value="approve">同意退款</el-radio>
            <el-radio value="reject">拒绝退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialog = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="handleAudit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>