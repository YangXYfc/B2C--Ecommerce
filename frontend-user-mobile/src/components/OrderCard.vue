<script setup>
import { formatPrice } from '../utils/format.js'
import { getOrderStatus } from '../utils/order.js'
defineProps({ order: { type: Object, required: true } })
defineEmits(['open'])
</script>

<template>
  <view class="card" @tap="$emit('open', order.id)">
    <view class="head"><text>{{ order.merchantName }}</text><text class="status">{{ getOrderStatus(order.status).text }}</text></view>
    <view v-for="item in order.items" :key="item.skuId" class="item">
      <image :src="item.image" mode="aspectFill" />
      <view class="grow"><text class="name">{{ item.name }}</text><text class="muted sku">{{ item.skuName }}</text></view>
      <view class="right"><text>¥{{ formatPrice(item.price) }}</text><text class="muted">×{{ item.quantity }}</text></view>
    </view>
    <view class="total">共 {{ order.items.length }} 件　实付款 <text class="price">¥{{ formatPrice(order.payAmount ?? order.totalAmount) }}</text></view>
  </view>
</template>

<style scoped>
.card { margin: 20rpx 24rpx; padding: 24rpx; background: #fff; border-radius: 20rpx; }
.head { display: flex; justify-content: space-between; font-size: 28rpx; font-weight: 600; }
.status { color: var(--brand); }
.item { display: flex; gap: 18rpx; padding: 22rpx 0; border-bottom: 2rpx solid var(--line); }
.item image { width: 150rpx; height: 150rpx; border-radius: 14rpx; }
.grow { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 14rpx; }
.name { font-size: 27rpx; line-height: 1.45; }
.sku { font-size: 24rpx; }
.right { display: flex; flex-direction: column; align-items: flex-end; gap: 12rpx; font-size: 25rpx; }
.total { padding-top: 22rpx; text-align: right; font-size: 25rpx; }
</style>
