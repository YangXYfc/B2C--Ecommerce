<script setup>
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import { validateAddress } from '../../utils/validation.js'
const form = reactive({ id: null, name: '', phone: '', province: '北京市', city: '北京市', district: '朝阳区', detail: '', isDefault: 0 }); const regions = ref(['北京市','北京市','朝阳区'])
onLoad(async ({ id }) => { if (id) { const address = (await api.getAddresses()).find((item) => item.id === Number(id)); if (address) { Object.assign(form, address); regions.value = [address.province,address.city,address.district] } } })
function regionChange(event) { regions.value = event.detail.value; [form.province,form.city,form.district] = regions.value }
async function save() { const message = validateAddress(form); if (message) return uni.showToast({ title: message, icon: 'none' }); await api.saveAddress(form); uni.showToast({ title: '保存成功' }); setTimeout(() => uni.navigateBack(), 300) }
</script>
<template><view class="page edit-page"><view class="surface form"><view class="form-field"><text>收货人</text><input v-model="form.name" /></view><view class="form-field"><text>手机号</text><input v-model="form.phone" type="number" maxlength="11" /></view><picker mode="region" :value="regions" @change="regionChange"><view class="form-field"><text>所在地区</text><text class="value">{{ regions.join(' ') }} ›</text></view></picker><view class="form-field detail"><text>详细地址</text><textarea v-model="form.detail" placeholder="街道、楼牌号等" /></view><view class="default"><text>设为默认地址</text><switch :checked="Boolean(form.isDefault)" color="#e4393c" @change="form.isDefault = $event.detail.value ? 1 : 0" /></view></view><button class="primary-button" @tap="save">保存地址</button></view></template>
<style scoped>.edit-page { padding: 24rpx; }.form { border-radius: 20rpx; overflow: hidden; }.value { margin-left: auto; color: var(--muted); font-size: 25rpx; }.detail { min-height: 180rpx; align-items: flex-start; padding-top: 30rpx; }.default { min-height: 100rpx; padding: 0 28rpx; display: flex; align-items: center; justify-content: space-between; }.default switch { transform: scale(.8); }.edit-page button { margin-top: 44rpx; }</style>
