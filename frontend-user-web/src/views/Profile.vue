<template>
  <div class="page-container">
    <div class="profile-layout">
      <!-- 侧边菜单 -->
      <div class="profile-sidebar page-card">
        <div class="user-info">
          <el-avatar :size="64" :src="userStore.profile?.avatar">
            {{ userStore.profile?.nickname?.[0] || 'U' }}
          </el-avatar>
          <h3>{{ userStore.profile?.nickname }}</h3>
          <p>{{ userStore.profile?.username }}</p>
        </div>
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/profile">个人资料</el-menu-item>
          <el-menu-item index="/orders">我的订单</el-menu-item>
          <el-menu-item index="/refunds">退款/售后</el-menu-item>
          <el-menu-item index="/addresses">收货地址</el-menu-item>
          <el-menu-item index="/password">修改密码</el-menu-item>
          <el-menu-item index="/merchant-apply">商家入驻</el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容 -->
      <div class="profile-main page-card">
        <h2>个人资料</h2>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" v-loading="loading" @submit.prevent="handleSubmit">
          <el-form-item label="头像">
            <div class="avatar-upload">
              <el-avatar :size="80" :src="form.avatar">{{ form.nickname?.[0] }}</el-avatar>
              <el-upload :show-file-list="false" :auto-upload="false" accept="image/*" @change="handleAvatarChange">
                <el-button size="small">更换头像</el-button>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio :label="0">保密</el-radio>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" native-type="submit">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateProfile, uploadImage } from '@/api'

const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)

const activeMenu = computed(() => route.path)

const form = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: 0,
  avatar: '',
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
}

onMounted(async () => {
  loading.value = true
  try {
    const profile = await userStore.fetchProfile()
    Object.assign(form, profile)
  } finally {
    loading.value = false
  }
})

async function handleAvatarChange(uploadFile) {
  const data = await uploadImage(uploadFile.raw)
  form.avatar = data.url
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const profile = await updateProfile(form)
    userStore.profile = profile
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.profile-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 16px;
}

.user-info {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--jd-border);
  margin-bottom: 8px;
}

.user-info h3 { margin: 12px 0 4px; }
.user-info p { font-size: 13px; color: var(--jd-text-light); }

.profile-main h2 { margin-bottom: 24px; }

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 768px) {
  .profile-layout { grid-template-columns: 1fr; }
}
</style>
