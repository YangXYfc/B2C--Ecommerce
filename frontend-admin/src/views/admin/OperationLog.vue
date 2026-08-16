<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLogList } from '@/api/log'

const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ action: '' })

const actionMap: Record<string, string> = {
  MERCHANT_AUDIT: '商家审核',
  PRODUCT_AUDIT: '商品审核',
  REFUND_ARBITRATE: '退款仲裁',
  USER_DISABLE: '用户管理',
}

async function fetchData() {
  loading.value = true
  const res: any = await getLogList({ action: query.value.action || undefined })
  list.value = res.data.list
  loading.value = false
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>操作日志</h2></div>
    <div class="search-bar">
      <el-select v-model="query.action" placeholder="操作类型" clearable style="width:160px">
        <el-option v-for="(v, k) in actionMap" :key="k" :label="v" :value="k" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="adminId" label="管理员ID" width="100" />
      <el-table-column label="操作类型" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ actionMap[row.action] || row.action }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="targetType" label="对象类型" width="100" />
      <el-table-column prop="targetId" label="对象ID" width="80" />
      <el-table-column label="详情" min-width="200">
        <template #default="{ row }">
          <span style="font-size:12px;color:#909399">{{ row.detail }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="ipAddress" label="IP地址" width="140" />
      <el-table-column prop="createdAt" label="时间" width="160" />
    </el-table>
  </div>
</template>