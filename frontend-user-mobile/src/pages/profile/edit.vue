<script setup>
import { reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
const form = reactive({ nickname: '', phone: '', email: '', gender: 0 })
onLoad(async () => Object.assign(form, await api.getProfile()))
async function save() { await api.updateProfile(form); uni.showToast({ title: '保存成功' }); setTimeout(() => uni.navigateBack(), 300) }
</script>
<template><view class="page edit-page"><view class="surface form"><view class="form-field"><text>昵称</text><input v-model="form.nickname" /></view><view class="form-field"><text>手机号</text><input v-model="form.phone" type="number" /></view><view class="form-field"><text>邮箱</text><input v-model="form.email" /></view><picker :range="['保密','男','女']" @change="form.gender = Number($event.detail.value)"><view class="form-field"><text>性别</text><text class="picker-value">{{ ['保密','男','女'][form.gender] }} ›</text></view></picker></view><button class="primary-button" @tap="save">保存</button></view></template>
<style scoped>.edit-page { padding: 24rpx; }.form { border-radius: 20rpx; overflow: hidden; }.picker-value { margin-left: auto; color: var(--muted); }.edit-page button { margin-top: 46rpx; }</style>
