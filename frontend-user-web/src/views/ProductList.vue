<template>
  <div class="page-container">
    <div class="filter-bar page-card">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索商品关键词"
        clearable
        style="width: 280px"
        @keyup.enter="loadProducts"
        @clear="loadProducts"
      />
      <el-select v-model="filters.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="loadProducts">
        <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
      <el-select v-model="filters.sort" placeholder="排序" style="width: 160px" @change="loadProducts">
        <el-option label="默认排序" value="" />
        <el-option label="销量优先" value="sales" />
        <el-option label="价格从低到高" value="price_asc" />
        <el-option label="价格从高到低" value="price_desc" />
      </el-select>
      <el-button type="primary" @click="loadProducts">搜索</el-button>
    </div>

    <div class="product-grid" v-loading="loading">
      <ProductCard v-for="product in products" :key="product.id" :product="product" />
    </div>

    <div v-if="!loading && !products.length" class="empty-state">
      <el-icon><Box /></el-icon>
      <p>暂无商品</p>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="filters.page"
        :page-size="filters.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Box } from '@element-plus/icons-vue'
import { getProducts, getCategories } from '@/api'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()
const loading = ref(false)
const products = ref([])
const total = ref(0)
const flatCategories = ref([])

const filters = reactive({
  keyword: route.query.keyword || '',
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : '',
  sort: '',
  page: 1,
  size: 12,
})

async function loadProducts() {
  loading.value = true
  try {
    const params = { ...filters }
    if (!params.categoryId) delete params.categoryId
    if (!params.keyword) delete params.keyword
    if (!params.sort) delete params.sort
    const data = await getProducts(params)
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const cats = await getCategories()
  flatCategories.value = cats.flatMap((c) => [c, ...(c.children || [])])
  await loadProducts()
})

watch(() => route.query, (q) => {
  filters.keyword = q.keyword || ''
  filters.categoryId = q.categoryId ? Number(q.categoryId) : ''
  filters.page = 1
  loadProducts()
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 300px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
