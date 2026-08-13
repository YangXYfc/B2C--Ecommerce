<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, createProduct, updateProduct, categories } from '@/api/mock/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const formRef = ref()
const loading = ref(false)

const form = ref({
  name: '', subtitle: '', categoryId: null as number | null, mainImage: '',
  description: '', detailHtml: '', price: 0,
})

const skuList = ref([{ skuName: '', price: 0, originalPrice: 0, stock: 0 }])

onMounted(async () => {
  const id = route.params.id as string
  if (id) {
    isEdit.value = true
    const res: any = await getProductDetail(Number(id))
    const d = res.data
    form.value = { name: d.name, subtitle: d.subtitle, categoryId: d.categoryId, mainImage: d.mainImage, description: d.description || '', detailHtml: d.detailHtml || '', price: d.price }
    if (d.skus?.length) skuList.value = d.skus.map((s: any) => ({ skuName: s.skuName, price: s.price, originalPrice: s.originalPrice || 0, stock: s.stock || 0 }))
  }
})

function addSku() { skuList.value.push({ skuName: '', price: 0, originalPrice: 0, stock: 0 }) }
function removeSku(idx: number) { skuList.value.splice(idx, 1) }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  const data = { ...form.value, skus: skuList.value }
  if (isEdit.value) {
    await updateProduct(Number(route.params.id), data)
    ElMessage.success('保存成功')
  } else {
    await createProduct(data)
    ElMessage.success('发布成功，等待审核')
  }
  loading.value = false
  router.push('/products')
}
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>{{ isEdit ? '编辑商品' : '发布商品' }}</h2></div>
    <el-form ref="formRef" :model="form" label-width="100px" style="max-width:800px">
      <el-form-item label="商品名称" prop="name" :rules="[{ required: true }]">
        <el-input v-model="form.name" placeholder="请输入商品名称" />
      </el-form-item>
      <el-form-item label="副标题" prop="subtitle">
        <el-input v-model="form.subtitle" placeholder="营销卖点" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
        <el-select v-model="form.categoryId" placeholder="选择分类" style="width:240px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="主图URL" prop="mainImage">
        <el-input v-model="form.mainImage" placeholder="输入图片URL" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="商品价格" prop="price" :rules="[{ required: true }]">
        <el-input-number v-model="form.price" :min="0" :precision="2" />
      </el-form-item>

      <el-divider>商品规格 (SKU)</el-divider>
      <div v-for="(sku, idx) in skuList" :key="idx" style="display:flex;gap:12px;align-items:center;margin-bottom:12px">
        <el-input v-model="sku.skuName" placeholder="规格名称" style="width:160px" />
        <el-input-number v-model="sku.price" :min="0" :precision="2" placeholder="售价" />
        <el-input-number v-model="sku.originalPrice" :min="0" :precision="2" placeholder="原价" />
        <el-input-number v-model="sku.stock" :min="0" placeholder="库存" />
        <el-button v-if="skuList.length > 1" type="danger" :icon="'Delete'" circle size="small" @click="removeSku(idx)" />
      </div>
      <el-button type="primary" plain @click="addSku" style="margin-bottom:20px">+ 添加规格</el-button>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</el-button>
        <el-button @click="router.push('/products')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
