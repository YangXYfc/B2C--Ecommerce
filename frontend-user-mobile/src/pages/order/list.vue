<script setup>
import { ref } from 'vue'
import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import OrderCard from '../../components/OrderCard.vue'
import StateBlock from '../../components/StateBlock.vue'
const tabs = [{ label: '全部', value: '' }, { label: '待付款', value: 0 }, { label: '待发货', value: 1 }, { label: '待收货', value: 2 }, { label: '已完成', value: 4 }]
const active = ref(''); const orders = ref([])
async function load() { const result = await api.getOrders({ status: active.value }); orders.value = result.records; uni.stopPullDownRefresh() }
function select(value) { active.value = value; load() }
onLoad((query) => { active.value = query.status === undefined ? '' : query.status === '' ? '' : Number(query.status) })
onShow(load); onPullDownRefresh(load)
</script>
<template><view class="page"><scroll-view scroll-x class="tabs"><view class="tab-row"><text v-for="tab in tabs" :key="String(tab.value)" :class="['tab', { active: active === tab.value }]" @tap="select(tab.value)">{{ tab.label }}</text></view></scroll-view><StateBlock v-if="!orders.length" title="暂无相关订单" description="订单动态会展示在这里" /><OrderCard v-for="order in orders" :key="order.id" :order="order" @open="uni.navigateTo({ url: `/pages/order/detail?id=${$event}` })" /></view></template>
<style scoped>.tabs { position: sticky; top: 0; z-index: 5; white-space: nowrap; background: #fff; }.tab-row { display: flex; min-width: 760rpx; }.tab { flex: 1; padding: 26rpx 18rpx 22rpx; text-align: center; color: var(--muted); border-bottom: 4rpx solid transparent; }.tab.active { color: var(--text); font-weight: 650; border-color: var(--brand); }</style>
