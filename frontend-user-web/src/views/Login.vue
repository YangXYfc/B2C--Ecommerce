<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>用户登录</h2>
      <p class="auth-tip">测试账号：user1 / 123456</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" native-type="submit" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login(form)
    userStore.setAuth(data)
    await cartStore.refreshCount()
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fef0f0 0%, #f4f4f4 100%);
}

.auth-card {
  width: 400px;
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.auth-card h2 {
  text-align: center;
  margin-bottom: 8px;
  color: var(--jd-red);
}

.auth-tip {
  text-align: center;
  font-size: 12px;
  color: var(--jd-text-light);
  margin-bottom: 24px;
}

.submit-btn {
  width: 100%;
  background: var(--jd-red);
  border-color: var(--jd-red);
}

.auth-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: var(--jd-text-light);
}

.auth-footer a {
  color: var(--jd-red);
}
</style>
