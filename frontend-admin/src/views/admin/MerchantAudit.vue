<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMerchantList, getMerchantDetail, auditMerchant } from '@/api/merchant'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

const auditDialog = ref(false)
const auditForm = ref({ action: 'approve', remark: '' })
const currentMerchant = ref<any>(null)
const auditLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getMerchantList()
  list.value = res.data.list
  loading.value = false
}

async function openAudit(row: any) {
  const res: any = await getMerchantDetail(row.id)
  currentMerchant.value = res.data
  auditForm.value = { action: 'approve', remark: '' }
  auditDialog.value = true
}

async function handleAudit() {
  auditLoading.value = true
  await auditMerchant(currentMerchant.value.id, {
    approve: auditForm.value.action === 'approve',
    remark: auditForm.value.remark,
  })
  ElMessage.success(auditForm.value.action === 'approve' ? '审核通过' : '已驳回')
  auditDialog.value = false
  auditLoading.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>商家审核</h2></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="shopName" label="店铺名称" width="160" />
      <el-table-column label="申请人" width="120">
        <template #default="{ row }">{{ row.applicant?.nickname || row.applicant?.username }}</template>
      </el-table-column>
      <el-table-column prop="contactPhone" label="联系电话" width="140" />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openAudit(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditDialog" title="商家审核" width="520px">
      <el-descriptions v-if="currentMerchant" :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="店铺名称">{{ currentMerchant.shopName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentMerchant.applicant?.nickname || currentMerchant.applicant?.username }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentMerchant.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="店铺Logo">{{ currentMerchant.shopLogo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentMerchant.description }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.action">
            <el-radio value="approve">通过</el-radio>
            <el-radio value="reject">驳回</el-radio>
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