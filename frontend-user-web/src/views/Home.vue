<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <div class="page-container">
      <el-carousel height="360px" :interval="4000" v-if="banners.length">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" @click="handleBannerClick(banner)">
            <img :src="banner.imageUrl" :alt="banner.title" />
            <div class="banner-title">{{ banner.title }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类导航 -->
    <div class="page-container">
      <div class="category-section page-card">
        <h2 class="section-title">商品分类</h2>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            @click="$router.push({ name: 'ProductList', query: { categoryId: cat.id } })"
          >
            <img v-if="cat.icon" :src="cat.icon" :alt="cat.name" class="category-icon" />
            <el-icon v-else :size="32" color="#e4393c"><Grid /></el-icon>
            <span>{{ cat.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="page-container">
      <div class="section-header">
        <h2 class="section-title">热门推荐</h2>
        <router-link to="/products" class="more-link">查看更多 →</router-link>
      </div>
      <div class="product-grid" v-loading="loading">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Grid } from '@element-plus/icons-vue'
import { getBanners, getCategories, getProducts } from '@/api'
import ProductCard from '@/components/ProductCard.vue'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const products = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const [bannerData, categoryData, productData] = await Promise.all([
      getBanners(),
      getCategories(),
      getProducts({ page: 1, size: 8, sort: 'sales' }),
    ])
    banners.value = bannerData
    categories.value = categoryData
    products.value = productData.records
  } finally {
    loading.value = false
  }
})

function handleBannerClick(banner) {
  if (banner.linkUrl?.startsWith('/')) {
    router.push(banner.linkUrl)
  }
}
</script>

<style scoped>
.banner-item {
  position: relative;
  height: 360px;
  cursor: pointer;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.banner-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));
  color: #fff;
  font-size: 18px;
  border-radius: 0 0 4px 4px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.more-link {
  color: var(--jd-text-light);
  font-size: 14px;
}

.more-link:hover {
  color: var(--jd-red);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.category-item:hover {
  background: #fef0f0;
  color: var(--jd-red);
}

.category-icon {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;
}

@media (max-width: 768px) {
  .category-grid,
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
