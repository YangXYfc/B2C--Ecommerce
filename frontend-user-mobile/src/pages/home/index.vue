<template>
  <view class="page home">
    <BrandHeader />
    <view v-if="loading" class="section"><StateBlock title="正在挑选好物…" /></view>
    <template v-else>
      <swiper class="hero" circular autoplay :interval="4500" indicator-dots indicator-active-color="#E4393C">
        <swiper-item v-for="banner in banners" :key="banner.id" @tap="openBanner(banner)"><image :src="banner.imageUrl" mode="aspectFill" /></swiper-item>
      </swiper>
      <scroll-view class="scenes" scroll-x show-scrollbar="false">
        <view class="scene-row">
          <view v-for="(category, index) in categories" :key="category.id" class="scene" @tap="openCategory(category.id)">
            <image :src="sceneImages[index]" mode="aspectFill" />
            <text>{{ category.scene }}</text>
          </view>
        </view>
      </scroll-view>
      <view class="section feature-section">
        <view class="heading"><text class="section-title">今日精选</text><text class="more" @tap="openProducts">更多好物</text></view>
        <view v-if="featured" class="feature" @tap="openProduct(featured.id)">
          <view class="feature-copy"><text class="feature-kicker">轻薄高能 灵感随行</text><text class="feature-name">{{ featured.name }}</text><text class="feature-price">¥{{ formatPrice(featured.price) }}</text></view>
          <image :src="featured.mainImage" mode="aspectFill" />
        </view>
        <view class="grid"><ProductCard v-for="product in products" :key="product.id" :product="product" /></view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import BrandHeader from '../../components/BrandHeader.vue'
import ProductCard from '../../components/ProductCard.vue'
import StateBlock from '../../components/StateBlock.vue'
import { api } from '../../api/index.js'
import { formatPrice } from '../../utils/format.js'

const banners = ref([]); const categories = ref([]); const products = ref([]); const loading = ref(true)
const sceneImages = [
  'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=400&q=80',
  'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=400&q=80',
  'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=400&q=80',
  'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=400&q=80',
]
const featured = computed(() => products.value[0])

onMounted(async () => {
  try {
    const [bannerValues, categoryValues, page] = await Promise.all([api.getBanners(), api.getCategories(), api.getProducts({ sort: 'sales', page: 1, size: 7 })])
    banners.value = bannerValues; categories.value = categoryValues; products.value = page.records.slice(1)
  } finally { loading.value = false }
})
function openBanner(banner) { uni.navigateTo({ url: banner.linkUrl }) }
function openCategory(id) { uni.navigateTo({ url: `/pages/product/list?categoryId=${id}` }) }
function openProduct(id) { uni.navigateTo({ url: `/pages/product/detail?id=${id}` }) }
function openProducts() { uni.navigateTo({ url: '/pages/product/list' }) }
</script>

<style scoped>
.home { padding-bottom: 30rpx; }
.hero { height: 350rpx; background: #fff; }
.hero image { width: 100%; height: 100%; }
.scenes { width: 100%; background: var(--soft-red); }
.scene-row { display: flex; gap: 28rpx; width: max-content; padding: 28rpx; }
.scene { width: 150rpx; display: flex; flex-direction: column; align-items: center; gap: 12rpx; font-size: 25rpx; }
.scene image { width: 132rpx; height: 132rpx; border: 6rpx solid #fff; border-radius: 50%; }
.feature-section { background: #fff; padding-bottom: 36rpx; }
.heading { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24rpx; }
.more { color: var(--muted); font-size: 24rpx; }
.feature { position: relative; height: 300rpx; overflow: hidden; border-radius: 20rpx; background: #f4efe9; }
.feature image { position: absolute; right: 0; width: 58%; height: 100%; }
.feature-copy { position: relative; z-index: 1; width: 48%; height: 100%; padding: 38rpx 0 30rpx 30rpx; display: flex; flex-direction: column; }
.feature-kicker { font-size: 30rpx; font-weight: 700; }
.feature-name { margin-top: 14rpx; color: #666; font-size: 23rpx; line-height: 34rpx; }
.feature-price { margin-top: auto; color: var(--brand); font-size: 40rpx; font-weight: 700; }
.grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 18rpx; margin-top: 20rpx; }
</style>
