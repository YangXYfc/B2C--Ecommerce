<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '../../stores/user.js'
import { validateLogin } from '../../utils/validation.js'
const form = reactive({ username: 'user1', password: '123456' }); const loading = ref(false); const user = useUserStore()
async function submit() { const message = validateLogin(form); if (message) return uni.showToast({ title: message, icon: 'none' }); loading.value = true; try { await user.login(form); uni.showToast({ title: '登录成功' }); setTimeout(() => uni.switchTab({ url: '/pages/profile/index' }), 300) } catch (error) { uni.showToast({ title: error.message, icon: 'none' }) } finally { loading.value = false } }
</script>
<template><view class="auth-page"><view class="brand">悦选</view><text class="slogan">发现你的生活好物</text><view class="form"><view class="form-field"><text>账号</text><input v-model="form.username" placeholder="请输入账号" /></view><view class="form-field"><text>密码</text><input v-model="form.password" password placeholder="请输入密码" /></view></view><button class="primary-button" :loading="loading" @tap="submit">登录</button><text class="link" @tap="uni.navigateTo({ url: '/pages/auth/register' })">还没有账号？立即注册</text><text class="tip">演示账号：user1 / 123456</text></view></template>
<style scoped>.auth-page { min-height: 100vh; padding: 140rpx 52rpx 60rpx; background: #fff; }.brand { color: var(--brand); font-size: 60rpx; font-weight: 800; letter-spacing: 4rpx; }.slogan { display: block; margin: 12rpx 0 80rpx; color: var(--muted); }.form { margin-bottom: 50rpx; border-top: 2rpx solid var(--line); }.auth-page button { margin-top: 30rpx; }.link,.tip { display: block; text-align: center; }.link { margin-top: 34rpx; color: var(--brand); }.tip { margin-top: 80rpx; color: var(--muted); font-size: 24rpx; }</style>
