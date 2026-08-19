<script setup>
import { reactive, ref } from 'vue'
import { api } from '../../api/index.js'
import { validateRegister } from '../../utils/validation.js'
const form = reactive({ username: '', nickname: '', phone: '', password: '', confirmPassword: '' }); const loading = ref(false)
async function submit() { const message = validateRegister(form); if (message) return uni.showToast({ title: message, icon: 'none' }); loading.value = true; try { await api.register(form); uni.showToast({ title: '注册成功' }); setTimeout(() => uni.navigateBack(), 300) } catch (error) { uni.showToast({ title: error.message, icon: 'none' }) } finally { loading.value = false } }
</script>
<template><view class="page register"><view class="form surface"><view class="form-field"><text>账号</text><input v-model="form.username" placeholder="用于登录" /></view><view class="form-field"><text>昵称</text><input v-model="form.nickname" placeholder="选填" /></view><view class="form-field"><text>手机号</text><input v-model="form.phone" type="number" maxlength="11" placeholder="请输入手机号" /></view><view class="form-field"><text>密码</text><input v-model="form.password" password placeholder="至少6位" /></view><view class="form-field"><text>确认密码</text><input v-model="form.confirmPassword" password placeholder="再次输入密码" /></view></view><button class="primary-button" :loading="loading" @tap="submit">完成注册</button></view></template>
<style scoped>.register { padding: 24rpx; }.form { border-radius: 20rpx; overflow: hidden; }.register button { margin-top: 48rpx; }</style>
