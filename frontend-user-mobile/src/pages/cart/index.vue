<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import { useCartStore } from '../../stores/cart.js'
import { calculateSelectedTotal, groupItemsByMerchant } from '../../utils/cart.js'
import { formatPrice } from '../../utils/format.js'
import QuantityStepper from '../../components/QuantityStepper.vue'
import StateBlock from '../../components/StateBlock.vue'

const items = ref([]); const loading = ref(true); const cartStore = useCartStore()
const groups = computed(() => groupItemsByMerchant(items.value))
const selected = computed(() => items.value.filter((item) => Number(item.selected) === 1))
const total = computed(() => calculateSelectedTotal(items.value))
const allSelected = computed(() => items.value.length > 0 && selected.value.length === items.value.length)
async function load() { loading.value = true; try { items.value = await api.getCart(); await cartStore.refresh() } finally { loading.value = false } }
async function patch(line, data) { await api.updateCartItem(line.id, data); await load() }
async function remove(line) { await api.removeCartItem(line.id); await load() }
async function toggleAll() { await Promise.all(items.value.map((line) => api.updateCartItem(line.id, { selected: allSelected.value ? 0 : 1 }))); await load() }
function checkout() { if (!selected.value.length) return uni.showToast({ title: '请选择商品', icon: 'none' }); uni.navigateTo({ url: `/pages/checkout/index?cartItemIds=${selected.value.map((item) => item.id).join(',')}` }) }
onShow(load)
</script>

<template>
  <view class="page cart-page">
    <StateBlock v-if="!loading && !items.length" title="购物车还是空的" description="去挑一件喜欢的好物吧" action-text="去逛逛" @action="uni.switchTab({ url: '/pages/home/index' })" />
    <view v-for="group in groups" :key="group.id" class="merchant">
      <view class="merchant-name">{{ group.name }}</view>
      <view v-for="line in group.items" :key="line.id" class="cart-line">
        <switch :checked="Boolean(line.selected)" color="#e4393c" @change="patch(line, { selected: $event.detail.value ? 1 : 0 })" />
        <image :src="line.productImage" mode="aspectFill" @tap="uni.navigateTo({ url: `/pages/product/detail?id=${line.productId}` })" />
        <view class="line-main"><text class="line-name">{{ line.productName }}</text><text class="muted sku">{{ line.skuName }}</text><view class="line-bottom"><text class="price">¥{{ formatPrice(line.price) }}</text><QuantityStepper :model-value="line.quantity" :max="line.stock" @change="patch(line, { quantity: $event })" /></view><text class="remove" @tap="remove(line)">删除</text></view>
      </view>
    </view>
    <view v-if="items.length" class="bottom-action bar">
      <view class="all" @tap="toggleAll"><switch :checked="allSelected" color="#e4393c" />全选</view>
      <view class="sum"><text>合计：</text><text class="price">¥{{ formatPrice(total) }}</text></view>
      <button class="primary-button" @tap="checkout">结算（{{ selected.length }}）</button>
    </view>
  </view>
</template>

<style scoped>
.cart-page { padding-bottom: 140rpx; }.merchant { margin: 20rpx 24rpx; background: #fff; border-radius: 20rpx; overflow: hidden; }.merchant-name { padding: 24rpx; font-weight: 650; }.cart-line { display: flex; gap: 16rpx; padding: 18rpx 24rpx 24rpx; border-top: 2rpx solid var(--line); align-items: flex-start; }.cart-line switch,.all switch { transform: scale(.7); transform-origin: left center; width: 74rpx; }.cart-line image { width: 160rpx; height: 160rpx; border-radius: 14rpx; }.line-main { flex: 1; min-width: 0; position: relative; }.line-name { font-size: 27rpx; line-height: 1.4; }.sku { display: block; margin: 12rpx 0; font-size: 23rpx; }.line-bottom { display: flex; justify-content: space-between; align-items: center; }.remove { display: block; margin-top: 14rpx; text-align: right; color: var(--muted); font-size: 23rpx; }.bar { display: flex; align-items: center; gap: 14rpx; }.all { display: flex; align-items: center; font-size: 25rpx; }.sum { flex: 1; text-align: right; font-size: 25rpx; }.bar button { width: 210rpx; line-height: 76rpx; }
</style>
