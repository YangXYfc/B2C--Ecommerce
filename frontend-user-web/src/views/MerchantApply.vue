<template>
  <div class="page-container">
    <div class="page-card" style="max-width: 600px; margin: 40px auto;">
      <h2>商家入驻申请</h2>
      <p class="tip">提交申请后，管理员将在后台审核，审核通过即可开店经营。</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" @submit.prevent="handleSubmit">
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="客服/联系手机号" />
        </el-form-item>
        <el-form-item label="店铺描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="简要介绍您的店铺经营范围" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" native-type="submit">提交申请</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchantApply } from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ shopName: '', contactPhone: '', description: '' })
const rules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  contactPhone: [{ required: true, pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
  description: [{ required: true, message: '请填写店铺描述', trigger: 'blur' }],
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await merchantApply(form)
    ElMessage.success('申请已提交，请等待管理员审核')
    router.push('/profile')
  } catch (e) {
    ElMessage.error(e.message || '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
h2 { margin-bottom: 8px; }
.tip { color: var(--jd-text-light); font-size: 14px; margin-bottom: 24px; }
</style>
