<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const form = ref({
  username: '',
  password: '',
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}
const loading = ref(false)

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(form.value.username, form.value.password)
    if (authStore.userInfo?.role !== 'MERCHANT') {
      ElMessage.error('该账号不是商家账号，无法登录商家后台')
      authStore.logout()
      return
    }
    router.push('/dashboard')
  } catch {
    // 错误提示已由 request 拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-card-title">
          <span class="login-card-icon">🏪</span>
          <span>B2C 电商平台 - 商家后台</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">测试账号：merchant1 / 123456</div>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #eef2f8 0%, #e2ebf6 50%, #f2f6fb 100%);
}

.login-card {
  width: 400px;
  border: none;
  --el-card-border-radius: 14px;
  box-shadow: 0 12px 32px rgba(31, 45, 61, 0.14);
}

.login-card-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 19px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 1px;
}

.login-card-icon {
  font-size: 22px;
}

.login-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
</style>