<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, orderStatusMap } from '@/api/mock/order'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const query = ref({ orderNo: '', status: '' })

async function fetchData() {
  loading.value = true
  const res: any = await getOrderList({ orderNo: query.value.orderNo || undefined, status: query.value.status })
  list.value = res.data.list
  loading.value = false
}

function getStatusType(s: number) {
  if (s === 0) return 'warning'
  if (s === 1 || s === 2) return 'primary'
  if (s === 3 || s === 4) return 'success'
  return 'info'
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>订单管理</h2></div>
    <div class="search-bar">
      <el-input v-model="query.orderNo" placeholder="订单编号" clearable style="width:220px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
        <el-option v-for="(v, k) in orderStatusMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button @click="query = { orderNo: '', status: '' }; fetchData()">重置</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="{ row }">
          <div v-for="item in row.items" :key="item.productName" style="display:flex;align-items:center;gap:8px">
            <img :src="item.productImage" style="width:40px;height:40px;object-fit:cover;border-radius:4px" />
            <span>{{ item.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.payAmount || row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">{{ orderStatusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="160" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/orders/${row.id}`)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
