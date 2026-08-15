<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList, getProductDetail, offShelfProduct, updateStock } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref({ status: '' })

const statusMap: Record<number, string> = { 0: '待审核', 1: '上架', 2: '下架', 3: '拒绝' }

const stockDialog = ref(false)
const stockSkus = ref<any[]>([])
const currentProductName = ref('')
const stockLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getProductList({ status: query.value.status || undefined })
  list.value = res.data.list
  total.value = res.data.total
  loading.value = false
}

async function handleOffShelf(id: number) {
  await ElMessageBox.confirm('确定下架该商品？', '提示', { type: 'warning' })
  await offShelfProduct(id)
  ElMessage.success('已下架')
  fetchData()
}

function handleSearch() { fetchData() }
function handleReset() { query.value = { status: '' }; fetchData() }

async function openStock(row: any) {
  const res: any = await getProductDetail(row.id)
  const d = res.data
  currentProductName.value = d.name
  stockSkus.value = (d.skus || []).map((s: any) => ({ id: s.id, skuName: s.skuName, stock: s.stock ?? 0 }))
  stockDialog.value = true
}

async function handleSaveStock() {
  stockLoading.value = true
  try {
    for (const sku of stockSkus.value) {
      await updateStock(sku.id, sku.stock)
    }
    ElMessage.success('库存更新成功')
    stockDialog.value = false
    fetchData()
  } finally {
    stockLoading.value = false
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品管理</h2>
      <el-button type="primary" @click="router.push('/products/create')">发布商品</el-button>
    </div>
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
        <el-option label="待审核" :value="0" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="2" />
        <el-option label="拒绝" :value="3" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img :src="row.mainImage" style="width:50px;height:50px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : row.status === 3 ? 'danger' : 'info'" size="small">
            {{ statusMap[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="salesCount" label="销量" width="80" />
      <el-table-column prop="createdAt" label="创建时间" width="160" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/products/${row.id}/edit`)">编辑</el-button>
          <el-button size="small" @click="openStock(row)">改库存</el-button>
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleOffShelf(row.id)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="stockDialog" :title="`调整库存 - ${currentProductName}`" width="560px">
      <el-table :data="stockSkus" border>
        <el-table-column prop="skuName" label="规格" min-width="160" />
        <el-table-column label="库存" width="200">
          <template #default="{ row }">
            <el-input-number v-model="row.stock" :min="0" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="stockDialog = false">取消</el-button>
        <el-button type="primary" :loading="stockLoading" @click="handleSaveStock">保存库存</el-button>
      </template>
    </el-dialog>
  </div>
</template>