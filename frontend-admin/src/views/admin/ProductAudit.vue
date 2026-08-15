<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProductList, getProductDetail, auditProduct } from '@/api/product'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

const auditDialog = ref(false)
const auditForm = ref({ action: 'approve', remark: '' })
const currentProduct = ref<any>(null)
const auditLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getProductList()
  list.value = res.data.list
  loading.value = false
}

async function openAudit(row: any) {
  const res: any = await getProductDetail(row.id)
  currentProduct.value = res.data
  auditForm.value = { action: 'approve', remark: '' }
  auditDialog.value = true
}

async function handleAudit() {
  auditLoading.value = true
  await auditProduct(currentProduct.value.id, {
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
    <div class="page-header"><h2>商品审核</h2></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img :src="row.mainImage" style="width:50px;height:50px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="shopName" label="商家" width="140" />
      <el-table-column prop="price" label="价格" width="90">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openAudit(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditDialog" title="商品审核" width="600px">
      <template v-if="currentProduct">
        <el-descriptions :column="2" border style="margin-bottom:16px">
          <el-descriptions-item label="商品名称" :span="2">{{ currentProduct.name }}</el-descriptions-item>
          <el-descriptions-item label="商家">{{ currentProduct.shopName }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ currentProduct.price }}</el-descriptions-item>
          <el-descriptions-item label="主图" :span="2">
            <img :src="currentProduct.mainImage" style="width:120px;height:80px;object-fit:cover;border-radius:4px" />
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ currentProduct.description || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-table :data="currentProduct.skus || []" border size="small" style="margin-bottom:16px">
          <el-table-column prop="skuName" label="规格" min-width="120" />
          <el-table-column prop="price" label="售价" width="100" />
          <el-table-column prop="originalPrice" label="原价" width="100" />
          <el-table-column prop="stock" label="库存" width="80" />
        </el-table>
      </template>
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