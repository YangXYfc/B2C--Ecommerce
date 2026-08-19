<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import { formatPrice } from '../../utils/format.js'

const addresses = ref([]); const addressId = ref(0); const lines = ref([]); const remark = ref(''); const query = ref({}); const submitting = ref(false)
const total = computed(() => lines.value.reduce((sum, line) => sum + Number(line.price) * Number(line.quantity), 0))
async function load(options) { query.value = options; addresses.value = await api.getAddresses(); addressId.value = addresses.value.find((item) => item.isDefault)?.id || addresses.value[0]?.id || 0; if (options.skuId) { const product = await api.getProduct(options.productId); const sku = product.skus.find((item) => item.id === Number(options.skuId)); lines.value = [{ ...sku, quantity: Number(options.quantity || 1), productName: product.name, productImage: product.mainImage }] } else { const ids = String(options.cartItemIds || '').split(',').map(Number); lines.value = (await api.getCart()).filter((item) => ids.includes(item.id)) } }
async function submit() { if (!addressId.value) return uni.showToast({ title: '请选择收货地址', icon: 'none' }); submitting.value = true; try { const result = await api.createOrder({ addressId: addressId.value, remark: remark.value, cartItemIds: query.value.cartItemIds ? String(query.value.cartItemIds).split(',').map(Number) : [], skuId: query.value.skuId ? Number(query.value.skuId) : undefined, quantity: Number(query.value.quantity || 1) }); const first = Array.isArray(result) ? result[0] : result; uni.redirectTo({ url: `/pages/order/detail?id=${first.id}` }) } catch (error) { uni.showToast({ title: error.message, icon: 'none' }) } finally { submitting.value = false } }
onLoad(load)
</script>

<template>
  <view class="page checkout-page">
    <picker :range="addresses" range-key="detail" @change="addressId = addresses[$event.detail.value].id">
      <view class="address" v-if="addresses.length"><view><text class="who">{{ addresses.find(a => a.id === addressId)?.name }}　{{ addresses.find(a => a.id === addressId)?.phone }}</text><text class="muted detail">{{ addresses.find(a => a.id === addressId)?.province }}{{ addresses.find(a => a.id === addressId)?.city }}{{ addresses.find(a => a.id === addressId)?.district }}{{ addresses.find(a => a.id === addressId)?.detail }}</text></view><text>›</text></view>
      <view class="address" v-else @tap.stop="uni.navigateTo({ url: '/pages/address/edit' })">新增收货地址 <text>›</text></view>
    </picker>
    <view class="order-box"><view v-for="line in lines" :key="line.id || line.skuId" class="line"><image :src="line.productImage" mode="aspectFill" /><view class="grow"><text>{{ line.productName }}</text><text class="muted sku">{{ line.skuName || line.name }}</text></view><view class="amount">¥{{ formatPrice(line.price) }}<text class="muted">×{{ line.quantity }}</text></view></view><view class="remark"><text>订单备注</text><input v-model="remark" placeholder="选填，建议先与商家沟通" /></view></view>
    <view class="bottom-action submit-bar"><view class="total">合计 <text class="price">¥{{ formatPrice(total) }}</text></view><button class="primary-button" :loading="submitting" @tap="submit">提交订单</button></view>
  </view>
</template>

<style scoped>
.checkout-page { padding: 20rpx 24rpx 150rpx; }.address,.order-box { background: #fff; border-radius: 20rpx; }.address { min-height: 150rpx; padding: 26rpx; display: flex; align-items: center; justify-content: space-between; }.who,.detail { display: block; }.who { font-weight: 650; margin-bottom: 14rpx; }.detail { font-size: 25rpx; line-height: 1.45; }.order-box { margin-top: 20rpx; padding: 24rpx; }.line { display: flex; gap: 18rpx; padding-bottom: 22rpx; }.line image { width: 150rpx; height: 150rpx; border-radius: 14rpx; }.grow { flex: 1; display: flex; flex-direction: column; gap: 12rpx; font-size: 26rpx; }.sku { font-size: 23rpx; }.amount { display: flex; flex-direction: column; align-items: flex-end; font-size: 24rpx; }.remark { display: flex; align-items: center; gap: 20rpx; padding-top: 22rpx; border-top: 2rpx solid var(--line); font-size: 27rpx; }.remark input { flex: 1; text-align: right; font-size: 25rpx; }.submit-bar { display: flex; align-items: center; gap: 20rpx; }.total { flex: 1; text-align: right; }.submit-bar button { width: 240rpx; line-height: 78rpx; }
</style>
