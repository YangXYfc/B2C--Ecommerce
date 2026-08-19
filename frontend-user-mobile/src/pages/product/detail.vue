<template>
  <view v-if="product" class="page detail-page">
    <swiper class="gallery" indicator-dots indicator-active-color="#E4393C"><swiper-item><image :src="product.mainImage" mode="aspectFill" /></swiper-item></swiper>
    <view class="product-main surface">
      <text class="detail-price"><text>¥</text>{{ formatPrice(selectedSku?.price || product.price) }}</text>
      <text class="detail-name">{{ product.name }}</text>
      <text class="detail-subtitle">{{ product.subtitle }}</text>
      <view class="merchant"><text>{{ product.merchantName }}</text><text>进入店铺</text></view>
    </view>
    <view class="block surface" @tap="selectorOpen = !selectorOpen"><text class="label">已选</text><text class="value">{{ selectedSku?.name || '请选择规格' }} × {{ quantity }}</text><text class="muted">选择</text></view>
    <view v-if="selectorOpen" class="sku-panel surface">
      <text class="panel-title">选择规格</text>
      <view class="sku-list"><text v-for="sku in product.skus" :key="sku.id" :class="['sku', { active: selectedSku?.id === sku.id, disabled: !sku.stock }]" @tap="selectSku(sku)">{{ sku.name }}</text></view>
      <view class="quantity"><text>数量</text><view><button @tap="quantity = Math.max(1, quantity - 1)">−</button><text>{{ quantity }}</text><button @tap="quantity = Math.min(selectedSku.stock, quantity + 1)">＋</button></view></view>
    </view>
    <view class="description surface"><text class="panel-title">商品详情</text><text>{{ product.description }}</text></view>
    <view class="reviews surface"><text class="panel-title">用户评价（{{ product.reviews.length }}）</text><view v-for="review in product.reviews" :key="review.id" class="review"><text>{{ review.nickname }} · {{ review.rating }}星</text><text>{{ review.content }}</text></view></view>
    <view class="bottom-action detail-actions"><button class="secondary-button" @tap="add">加入购物车</button><button class="primary-button" @tap="buy">立即购买</button></view>
  </view>
  <StateBlock v-else title="正在加载商品…" />
</template>
<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { ref } from 'vue'
import StateBlock from '../../components/StateBlock.vue'
import { api } from '../../api/index.js'
import { formatPrice } from '../../utils/format.js'
import { selectAvailableSku } from '../../utils/catalog.js'
import { useCartStore } from '../../stores/cart.js'
const product = ref(null); const selectedSku = ref(null); const quantity = ref(1); const selectorOpen = ref(false); const cartStore = useCartStore()
onLoad(async ({ id }) => { product.value = await api.getProduct(id); selectedSku.value = selectAvailableSku(product.value.skus) })
function selectSku(sku) { if (sku.stock) { selectedSku.value = sku; quantity.value = 1 } }
async function add() { await cartStore.add(selectedSku.value.id, quantity.value); uni.showToast({ title: '已加入购物车' }) }
function buy() { uni.navigateTo({ url: `/pages/checkout/index?skuId=${selectedSku.value.id}&quantity=${quantity.value}` }) }
</script>
<style scoped>
.detail-page { padding-bottom: 150rpx; }
.gallery { height: 750rpx; background: #fff; }
.gallery image { width: 100%; height: 100%; }
.product-main { padding: 28rpx; }
.detail-price { display: block; color: var(--brand); font-size: 48rpx; font-weight: 800; }
.detail-price text { font-size: 28rpx; }
.detail-name { display: block; margin-top: 16rpx; font-size: 34rpx; font-weight: 700; line-height: 48rpx; }
.detail-subtitle { display: block; margin-top: 10rpx; color: var(--muted); font-size: 25rpx; }
.merchant { margin-top: 28rpx; padding-top: 22rpx; border-top: 2rpx solid var(--line); display: flex; justify-content: space-between; font-size: 25rpx; }
.block { margin-top: 18rpx; padding: 28rpx; display: flex; gap: 22rpx; font-size: 26rpx; }
.label { color: var(--muted); }.value { flex: 1; }
.sku-panel, .description, .reviews { margin-top: 18rpx; padding: 28rpx; }
.panel-title { display: block; margin-bottom: 22rpx; font-size: 30rpx; font-weight: 700; }
.sku-list { display: flex; flex-wrap: wrap; gap: 14rpx; }
.sku { padding: 16rpx 22rpx; border-radius: 999rpx; background: #f3f3f3; font-size: 24rpx; }
.sku.active { background: var(--soft-red); color: var(--brand); }.sku.disabled { opacity: .35; }
.quantity { margin-top: 30rpx; display: flex; justify-content: space-between; align-items: center; }
.quantity view { display: flex; align-items: center; }.quantity button { width: 64rpx; height: 58rpx; line-height: 58rpx; font-size: 28rpx; }.quantity view text { width: 70rpx; text-align: center; }
.description > text:last-child { color: #555; font-size: 27rpx; line-height: 48rpx; }
.review { padding: 18rpx 0; border-top: 2rpx solid var(--line); display: flex; flex-direction: column; gap: 10rpx; font-size: 25rpx; }
.detail-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 18rpx; }.detail-actions button { width: 100%; }
</style>
