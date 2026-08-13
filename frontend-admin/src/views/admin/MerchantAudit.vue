<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMerchantList, auditMerchant } from '@/api/mock/merchant'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ shopName: '', auditStatus: '' })

const auditDialog = ref(false)
const auditForm = ref({ action: 'approve', remark: '' })
const currentMerchant = ref<any>(null)
const auditLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getMerchantList({ shopName: query.value.shopName || undefined, auditStatus: query.value.auditStatus })
  list.value = res.data.list
  loading.value = false
}

function openAudit(row: any) {
  currentMerchant.value = row
  auditForm.value = { action: 'approve', remark: '' }
  auditDialog.value = true
}

async function handleAudit() {
  auditLoading.value = true
  await auditMerchant(currentMerchant.value.id, auditForm.value)
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
    <div class="search-bar">
      <el-input v-model="query.shopName" placeholder="店铺名称" clearable style="width:180px" />
      <el-select v-model="query.auditStatus" placeholder="审核状态" clearable style="width:140px">
        <el-option label="待审核" :value="0" />
        <el-option label="通过" :value="1" />
        <el-option label="拒绝" :value="2" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { shopName: '', auditStatus: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="shopName" label="店铺名称" width="140" />
      <el-table-column prop="userName" label="申请人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="140" />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="审核状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.auditStatus === 1 ? 'success' : row.auditStatus === 2 ? 'danger' : 'warning'" size="small">
            {{ row.auditStatus === 1 ? '已通过' : row.auditStatus === 2 ? '已拒绝' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.auditStatus === 0" size="small" type="primary" @click="openAudit(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditDialog" title="商家审核" width="450px">
      <el-descriptions v-if="currentMerchant" :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="店铺名称">{{ currentMerchant.shopName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentMerchant.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentMerchant.contactPhone }}</el-descriptions-item>
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
