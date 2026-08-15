<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getShop, updateShop } from '@/api/shop'
import { ElMessage } from 'element-plus'

const formRef = ref()
const form = ref({ shopName: '', shopLogo: '', description: '', contactPhone: '' })
const loading = ref(false)

onMounted(async () => {
  const res: any = await getShop()
  form.value = { ...res.data }
})

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await updateShop(form.value)
    ElMessage.success('保存成功')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>店铺设置</h2></div>
    <el-form ref="formRef" :model="form" label-width="120px" style="max-width:600px">
      <el-form-item label="店铺名称" prop="shopName" :rules="[{ required: true, message: '请输入店铺名称' }]">
        <el-input v-model="form.shopName" />
      </el-form-item>
      <el-form-item label="店铺Logo URL" prop="shopLogo">
        <el-input v-model="form.shopLogo" placeholder="输入图片URL" />
      </el-form-item>
      <el-form-item label="店铺描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone" :rules="[{ required: true, message: '请输入联系电话' }]">
        <el-input v-model="form.contactPhone" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>