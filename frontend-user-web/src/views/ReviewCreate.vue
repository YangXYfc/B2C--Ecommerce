<template>
  <div class="page-container">
    <div class="page-card" style="max-width: 600px; margin: 0 auto;" v-loading="loading">
      <h2>发表评价</h2>
      <div v-for="item in order?.items" :key="item.id" class="review-form">
        <div class="product-row">
          <img :src="item.productImage" />
          <span>{{ item.productName }}</span>
        </div>
        <el-form label-width="80px">
          <el-form-item label="评分">
            <el-rate v-model="forms[item.productId].rating" />
          </el-form-item>
          <el-form-item label="评价内容">
            <el-input v-model="forms[item.productId].content" type="textarea" :rows="4" placeholder="分享您的使用体验" />
          </el-form-item>
          <el-form-item label="匿名评价">
            <el-switch v-model="forms[item.productId].isAnonymous" />
          </el-form-item>
        </el-form>
      </div>
      <div class="action-bar">
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交评价</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail, createReview } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const order = ref(null)
const forms = reactive({})

async function loadOrder() {
  loading.value = true
  try {
    order.value = await getOrderDetail(route.params.id)
    order.value.items.forEach((item) => {
      forms[item.productId] = { rating: 5, content: '', isAnonymous: false }
    })
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    for (const item of order.value.items) {
      const form = forms[item.productId]
      if (!form.content.trim()) {
        ElMessage.warning(`请填写 ${item.productName} 的评价内容`)
        return
      }
      await createReview({
        orderId: order.value.id,
        productId: item.productId,
        rating: form.rating,
        content: form.content,
        isAnonymous: form.isAnonymous,
      })
    }
    ElMessage.success('评价成功')
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(loadOrder)
</script>

<style scoped>
h2 { margin-bottom: 24px; }
.review-form { margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid var(--jd-border); }
.product-row { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.product-row img { width: 48px; height: 48px; object-fit: cover; border-radius: 4px; }
.action-bar { display: flex; gap: 12px; }
</style>
