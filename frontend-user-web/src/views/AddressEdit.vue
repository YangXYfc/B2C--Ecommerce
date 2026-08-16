<template>
  <div class="page-container">
    <div class="page-card" style="max-width: 600px; margin: 0 auto;">
      <h2>{{ isEdit ? '编辑地址' : '新增地址' }}</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" v-loading="loading" @submit.prevent="handleSubmit">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" native-type="submit">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAddresses, createAddress, updateAddress } from '@/api'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
})

const rules = {
  name: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [{ required: true, pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

onMounted(async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const list = await getAddresses()
    const addr = list.find((a) => a.id === Number(route.params.id))
    if (addr) {
      Object.assign(form, { ...addr, isDefault: !!addr.isDefault })
    }
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAddress(route.params.id, form)
    } else {
      await createAddress(form)
    }
    ElMessage.success('保存成功')
    router.push('/addresses')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
h2 { margin-bottom: 24px; }
</style>
