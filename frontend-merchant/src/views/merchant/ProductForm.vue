<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, createProduct, updateProduct } from '@/api/product'
import { getCategories } from '@/api/category'
import { ElMessage } from 'element-plus'
import ImageUploader from '@/components/ImageUploader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const formRef = ref()
const loading = ref(false)
const categories = ref<any[]>([])

const form = ref({
  name: '', subtitle: '', categoryId: null as number | null, mainImage: '',
  subImages: [] as string[], description: '', detailHtml: '',
})

const skuList = ref([{ skuName: '', price: 0, originalPrice: 0, stock: 0, attributes: {} as Record<string, string>, skuImage: '' }])

onMounted(async () => {
  const catRes: any = await getCategories()
  categories.value = catRes.data

  const id = route.params.id as string
  if (id) {
    isEdit.value = true
    const res: any = await getProductDetail(Number(id))
    const d = res.data
    form.value = {
      categoryId: d.categoryId, name: d.name, subtitle: d.subtitle, mainImage: d.mainImage,
      subImages: Array.isArray(d.subImages) ? d.subImages : [],
      description: d.description || '', detailHtml: d.detailHtml || '',
    }
    if (d.skus?.length) {
      skuList.value = d.skus.map((s: any) => ({
        skuName: s.skuName, price: s.price, originalPrice: s.originalPrice || 0,
        stock: s.stock || 0, attributes: s.attributes || {}, skuImage: s.skuImage || '',
      }))
    }
  }
})

function addSku() { skuList.value.push({ skuName: '', price: 0, originalPrice: 0, stock: 0, attributes: {}, skuImage: '' }) }
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
      <el-form-item label="商品主图" prop="mainImage" :rules="[{ required: true, message: '请上传商品主图' }]">
        <ImageUploader v-model="form.mainImage" />
      </el-form-item>
      <el-form-item label="商品相册" prop="subImages">
        <ImageUploader v-model="form.subImages" multiple :limit="6" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>

      <el-divider>商品规格 (SKU，价格取最低 SKU 售价)</el-divider>
      <div v-for="(sku, idx) in skuList" :key="idx" class="sku-card">
        <div class="sku-fields">
          <el-input v-model="sku.skuName" placeholder="规格名称" style="width:160px" />
          <el-input-number v-model="sku.price" :min="0" :precision="2" placeholder="售价" />
          <el-input-number v-model="sku.originalPrice" :min="0" :precision="2" placeholder="原价" />
          <el-input-number v-model="sku.stock" :min="0" placeholder="库存" />
          <el-button v-if="skuList.length > 1" type="danger" circle size="small" @click="removeSku(idx)">删</el-button>
        </div>
        <div class="sku-image-row">
          <span>规格图片</span>
          <ImageUploader v-model="sku.skuImage" />
        </div>
      </div>
      <el-button type="primary" plain @click="addSku" style="margin-bottom:20px">+ 添加规格</el-button>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</el-button>
        <el-button @click="router.push('/products')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.sku-card { margin-bottom: 14px; padding: 14px; border: 1px solid #ebeef5; border-radius: 10px; background: #fafafa; }
.sku-fields { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.sku-image-row { display: flex; align-items: flex-start; gap: 16px; margin-top: 14px; color: #606266; font-size: 14px; }
</style>
