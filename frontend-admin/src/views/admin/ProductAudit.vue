<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProductList, auditProduct } from '@/api/mock/product'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ name: '', status: '' })

const auditDialog = ref(false)
const auditForm = ref({ action: 'approve', remark: '' })
const currentProduct = ref<any>(null)
const auditLoading = ref(false)

const statusMap: Record<number, string> = { 0: '待审核', 1: '上架', 2: '下架', 3: '拒绝' }

async function fetchData() {
  loading.value = true
  const res: any = await getProductList({ name: query.value.name || undefined, status: query.value.status })
  list.value = res.data.list
  loading.value = false
}

function openAudit(row: any) {
  currentProduct.value = row
  auditForm.value = { action: 'approve', remark: '' }
  auditDialog.value = true
}

async function handleAudit() {
  auditLoading.value = true
  await auditProduct(currentProduct.value.id, auditForm.value)
  ElMessage.success(auditForm.value.action === 'approve' ? '审核通过' : '已驳回')
  auditDialog.value = false
  auditLoading.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>商品审核</h2></div>
    <div class="search-bar">
      <el-input v-model="query.name" placeholder="商品名称" clearable style="width:200px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
        <el-option label="待审核" :value="0" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="2" />
        <el-option label="拒绝" :value="3" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { name: '', status: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img :src="row.mainImage" style="width:50px;height:50px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="merchantName" label="商家" width="120" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="price" label="价格" width="90">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 3 ? 'danger' : 'info'" size="small">
            {{ statusMap[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="primary" @click="openAudit(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditDialog" title="商品审核" width="450px">
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
