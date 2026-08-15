<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getBannerList, createBanner, updateBanner, deleteBanner } from '@/api/banner'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = ref({ title: '', imageUrl: '', linkUrl: '', sort: 0, enabled: true })
const currentId = ref<number | null>(null)
const submitLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getBannerList()
  list.value = res.data.list
  loading.value = false
}

function openCreate() {
  isEdit.value = false
  currentId.value = null
  form.value = { title: '', imageUrl: '', linkUrl: '', sort: 0, enabled: true }
  dialogVisible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  currentId.value = row.id
  form.value = { title: row.title, imageUrl: row.imageUrl, linkUrl: row.linkUrl, sort: row.sort, enabled: row.enabled }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  if (isEdit.value) {
    await updateBanner(currentId.value!, form.value)
    ElMessage.success('保存成功')
  } else {
    await createBanner(form.value)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  submitLoading.value = false
  fetchData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除该轮播图？', '提示', { type: 'warning' })
  await deleteBanner(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="openCreate">新增轮播图</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column label="图片" width="160">
        <template #default="{ row }">
          <img :src="row.imageUrl" style="width:140px;height:60px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="linkUrl" label="跳转链接" min-width="180" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.enabled" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="500px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="标题" prop="title" :rules="[{ required: true }]">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl" :rules="[{ required: true }]">
          <el-input v-model="form.imageUrl" />
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="启用" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>