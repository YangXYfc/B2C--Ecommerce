<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import { getRefundStatus } from '../../utils/refund.js'
import { formatPrice, formatDate } from '../../utils/format.js'
import StateBlock from '../../components/StateBlock.vue'
const refunds = ref([])
onShow(async () => { refunds.value = (await api.getRefunds()).records })
</script>
<template><view class="page refund-list"><StateBlock v-if="!refunds.length" title="暂无售后记录" description="已提交的退款与退货申请会显示在这里" /><view v-for="item in refunds" :key="item.id" class="refund" @tap="uni.navigateTo({ url: `/pages/refund/detail?id=${item.id}` })"><view class="head"><text>售后单 {{ item.refundNo }}</text><text class="status">{{ getRefundStatus(item.status).text }}</text></view><text class="reason">{{ item.reason }}</text><view class="foot"><text class="muted">{{ formatDate(item.createdAt) }}</text><text class="price">¥{{ formatPrice(item.amount) }}</text></view></view></view></template>
<style scoped>.refund-list { padding: 2rpx 24rpx; }.refund { margin-top: 20rpx; padding: 26rpx; border-radius: 20rpx; background: #fff; }.head,.foot { display: flex; justify-content: space-between; font-size: 24rpx; }.status { color: var(--brand); }.reason { display: block; margin: 26rpx 0; font-size: 29rpx; }.price { font-size: 29rpx; }</style>
