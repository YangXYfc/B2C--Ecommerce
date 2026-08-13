<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserList, toggleUserStatus } from '@/api/mock/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ username: '', status: '' })

const roleMap: Record<string, string> = { ADMIN: '管理员', MERCHANT: '商家', USER: '用户' }

async function fetchData() {
  loading.value = true
  const res: any = await getUserList({ username: query.value.username || undefined, status: query.value.status })
  list.value = res.data.list
  loading.value = false
}

async function handleToggle(row: any) {
  const action = row.status === 1 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定${action}用户「${row.nickname}」？`, '提示', { type: 'warning' })
  await toggleUserStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success(`已${action}`)
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>用户管理</h2></div>
    <div class="search-bar">
      <el-input v-model="query.username" placeholder="用户名" clearable style="width:180px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { username: '', status: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column label="角色" width="90">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'MERCHANT' ? 'warning' : 'info'" size="small">
            {{ roleMap[row.role] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="160" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.role !== 'ADMIN'" size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="handleToggle(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
