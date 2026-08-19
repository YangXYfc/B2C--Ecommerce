<script setup>
import { reactive } from 'vue'
import { api } from '../../api/index.js'
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
async function save() { if (form.newPassword.length < 6) return uni.showToast({ title: '新密码至少6位', icon: 'none' }); if (form.newPassword !== form.confirmPassword) return uni.showToast({ title: '两次密码不一致', icon: 'none' }); try { await api.changePassword(form); uni.showToast({ title: '修改成功' }); setTimeout(() => uni.navigateBack(), 300) } catch (error) { uni.showToast({ title: error.message, icon: 'none' }) } }
</script>
<template><view class="page password-page"><view class="surface form"><view class="form-field"><text>原密码</text><input v-model="form.oldPassword" password /></view><view class="form-field"><text>新密码</text><input v-model="form.newPassword" password /></view><view class="form-field"><text>确认密码</text><input v-model="form.confirmPassword" password /></view></view><button class="primary-button" @tap="save">确认修改</button></view></template>
<style scoped>.password-page { padding: 24rpx; }.form { border-radius: 20rpx; overflow: hidden; }.password-page button { margin-top: 46rpx; }</style>
