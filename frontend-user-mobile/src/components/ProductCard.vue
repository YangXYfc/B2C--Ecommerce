<template>
  <view :class="['product', { compact }]" @tap="open">
    <image class="product-image" :src="product.mainImage" mode="aspectFill" lazy-load />
    <view class="product-copy">
      <text class="product-name">{{ product.name }}</text>
      <text v-if="product.subtitle" class="product-subtitle">{{ product.subtitle }}</text>
      <view class="product-meta">
        <text class="price"><text class="currency">¥</text>{{ formatPrice(product.price) }}</text>
        <text class="sales">{{ formatSales(product.salesCount) }}人已买</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { formatPrice, formatSales } from '../utils/format.js'
const props = defineProps({ product: { type: Object, required: true }, featured: Boolean, compact: Boolean })
function open() { uni.navigateTo({ url: `/pages/product/detail?id=${props.product.id}` }) }
</script>

<style scoped>
.product { overflow: hidden; border-radius: 18rpx; background: #fff; border: 2rpx solid #f0f0f0; }
.product-image { width: 100%; height: 290rpx; background: #f4f4f4; }
.product-copy { padding: 18rpx; }
.product-name { display: -webkit-box; overflow: hidden; -webkit-box-orient: vertical; -webkit-line-clamp: 2; min-height: 76rpx; font-size: 27rpx; line-height: 38rpx; }
.product-subtitle { display: block; margin-top: 8rpx; overflow: hidden; color: var(--muted); font-size: 23rpx; text-overflow: ellipsis; white-space: nowrap; }
.product-meta { margin-top: 16rpx; display: flex; justify-content: space-between; align-items: baseline; }
.price { color: var(--brand); font-size: 35rpx; font-weight: 700; }
.currency { font-size: 23rpx; }
.sales { color: var(--muted); font-size: 21rpx; }
.compact { border-radius: 14rpx; }
.compact .product-image { height: 210rpx; }
.compact .product-copy { padding: 12rpx; }
.compact .product-name { display: block; min-height: 0; overflow: hidden; font-size: 22rpx; line-height: 30rpx; text-overflow: ellipsis; white-space: nowrap; }
.compact .product-subtitle { margin-top: 5rpx; font-size: 19rpx; }
.compact .product-meta { margin-top: 10rpx; display: flex; gap: 6rpx; justify-content: flex-start; }
.compact .price { flex: none; font-size: 26rpx; }
.compact .currency { font-size: 18rpx; }
.compact .sales { display: block; min-width: 0; overflow: hidden; font-size: 17rpx; text-overflow: ellipsis; white-space: nowrap; }
</style>
