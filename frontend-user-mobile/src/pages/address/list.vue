<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import StateBlock from '../../components/StateBlock.vue'
const addresses = ref([])
async function load() { addresses.value = await api.getAddresses() }
async function setDefault(id) { await api.setDefaultAddress(id); await load() }
async function remove(id) { uni.showModal({ title: '删除地址', content: '确认删除这个收货地址吗？', success: async ({ confirm }) => { if (confirm) { await api.deleteAddress(id); await load() } } }) }
onShow(load)
</script>
<template><view class="page list-page"><StateBlock v-if="!addresses.length" title="还没有收货地址" description="新增地址后可用于订单配送" /><view v-for="address in addresses" :key="address.id" class="address"><view class="top"><text class="name">{{ address.name }}</text><text>{{ address.phone }}</text><text v-if="address.isDefault" class="tag">默认</text></view><text class="detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</text><view class="actions"><text @tap="setDefault(address.id)">{{ address.isDefault ? '默认地址' : '设为默认' }}</text><view><text @tap="uni.navigateTo({ url: `/pages/address/edit?id=${address.id}` })">编辑</text><text @tap="remove(address.id)">删除</text></view></view></view><view class="bottom-action"><button class="primary-button" @tap="uni.navigateTo({ url: '/pages/address/edit' })">新增收货地址</button></view></view></template>
<style scoped>.list-page { padding: 2rpx 24rpx 140rpx; }.address { margin-top: 20rpx; padding: 26rpx; background: #fff; border-radius: 20rpx; }.top { display: flex; align-items: center; gap: 18rpx; }.name { font-size: 31rpx; font-weight: 650; }.tag { padding: 4rpx 12rpx; border-radius: 6rpx; background: var(--soft-red); color: var(--brand); font-size: 21rpx; }.detail { display: block; margin: 16rpx 0 22rpx; color: #555; line-height: 1.5; }.actions { padding-top: 20rpx; border-top: 2rpx solid var(--line); display: flex; justify-content: space-between; color: var(--muted); font-size: 25rpx; }.actions view text { margin-left: 34rpx; }.bottom-action button { width: 100%; line-height: 82rpx; }</style>
