<template>
  <div class="page-container">
    <div class="page-card">
      <div class="list-header">
        <h2>收货地址</h2>
        <el-button type="primary" @click="$router.push('/addresses/edit')">新增地址</el-button>
      </div>

      <div v-loading="loading">
        <div v-for="addr in addresses" :key="addr.id" class="address-card">
          <div class="addr-main">
            <span class="addr-name">{{ addr.name }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="danger">默认</el-tag>
          </div>
          <p class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</p>
          <div class="addr-actions">
            <el-button link type="primary" @click="$router.push(`/addresses/edit/${addr.id}`)">编辑</el-button>
            <el-button link v-if="!addr.isDefault" @click="handleSetDefault(addr.id)">设为默认</el-button>
            <el-button link type="danger" @click="handleDelete(addr.id)">删除</el-button>
          </div>
        </div>

        <div v-if="!loading && !addresses.length" class="empty-state">
          <p>暂无收货地址</p>
          <el-button type="primary" @click="$router.push('/addresses/edit')">新增地址</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddresses, deleteAddress, setDefaultAddress } from '@/api'

const loading = ref(false)
const addresses = ref([])

async function loadAddresses() {
  loading.value = true
  try {
    addresses.value = await getAddresses()
  } finally {
    loading.value = false
  }
}

async function handleSetDefault(id) {
  await setDefaultAddress(id)
  ElMessage.success('已设为默认地址')
  await loadAddresses()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示')
  await deleteAddress(id)
  ElMessage.success('删除成功')
  await loadAddresses()
}

onMounted(loadAddresses)
</script>

<style scoped>
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-card {
  padding: 16px;
  border: 1px solid var(--jd-border);
  border-radius: 4px;
  margin-bottom: 12px;
}

.addr-main { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.addr-name { font-weight: 600; }
.addr-phone { color: var(--jd-text-light); }
.addr-detail { font-size: 14px; color: var(--jd-text-light); margin-bottom: 8px; }
</style>
