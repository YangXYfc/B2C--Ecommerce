<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReviewList, replyReview } from '@/api/review'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

const replyDialog = ref(false)
const replyContent = ref('')
const currentReview = ref<any>(null)
const replyLoading = ref(false)

async function fetchData() {
  loading.value = true
  const res: any = await getReviewList()
  list.value = res.data.list
  loading.value = false
}

function openReply(row: any) {
  currentReview.value = row
  replyContent.value = row.merchantReply || ''
  replyDialog.value = true
}

async function handleReply() {
  replyLoading.value = true
  await replyReview(currentReview.value.id, replyContent.value)
  ElMessage.success('回复成功')
  replyDialog.value = false
  replyLoading.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>评价管理</h2></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="productId" label="商品ID" width="90" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column label="评分" width="150">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled show-score size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="160" />
      <el-table-column label="回复状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.merchantReply ? 'success' : 'info'" size="small">
            {{ row.merchantReply ? '已回复' : '未回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" @click="openReply(row)">{{ row.merchantReply ? '修改回复' : '回复' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="replyDialog" title="回复评价" width="500px">
      <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="输入回复内容" />
      <template #footer>
        <el-button @click="replyDialog = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="handleReply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>