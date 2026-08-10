<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList, categories, offShelfProduct, onShelfProduct } from '@/api/mock/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref({ keyword: '', categoryId: '', status: '' })

const statusMap: Record<number, string> = { 0: '待审核', 1: '上架', 2: '下架', 3: '拒绝' }

async function fetchData() {
  loading.value = true
  const res: any = await getProductList({
    keyword: query.value.keyword || undefined,
    categoryId: query.value.categoryId ? Number(query.value.categoryId) : undefined,
    status: query.value.status,
  })
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

async function handleOnShelf(id: number) {
  await ElMessageBox.confirm('确定上架该商品？', '提示', { type: 'success' })
  await onShelfProduct(id)
  ElMessage.success('已上架')
  fetchData()
}

function handleSearch() { fetchData() }
function handleReset() { query.value = { keyword: '', categoryId: '', status: '' }; fetchData() }

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品管理</h2>
      <el-button type="primary" @click="router.push('/products/create')">发布商品</el-button>
    </div>
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="商品名称" clearable style="width:200px" />
      <el-select v-model="query.categoryId" placeholder="分类" clearable style="width:160px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
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
      <el-table-column prop="categoryName" label="分类" width="100" />
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
      <el-table-column prop="totalStock" label="库存" width="80" />
      <el-table-column prop="salesCount" label="销量" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/products/${row.id}/edit`)">编辑</el-button>
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleOffShelf(row.id)">下架</el-button>
          <el-button v-if="row.status === 2" size="small" type="success" @click="handleOnShelf(row.id)">上架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
