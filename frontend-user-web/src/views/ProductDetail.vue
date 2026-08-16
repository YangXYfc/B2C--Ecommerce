<template>
  <div class="page-container" v-loading="loading">
    <template v-if="product">
      <div class="detail-layout">
        <!-- 左侧图片 -->
        <div class="detail-gallery page-card">
          <img :src="currentImage" :alt="product.name" class="main-image" />
          <div class="thumb-list">
            <img
              v-for="(img, idx) in allImages"
              :key="idx"
              :src="img"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            />
          </div>
        </div>

        <!-- 右侧购买区 -->
        <div class="detail-buy page-card">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.subtitle }}</p>
          <div class="price-box">
            <span class="price"><span class="price-symbol">¥</span>{{ formatPrice(selectedSku?.price || product.price) }}</span>
            <span class="original-price" v-if="selectedSku?.originalPrice">
              ¥{{ formatPrice(selectedSku.originalPrice) }}
            </span>
          </div>
          <div class="shop-info">
            <el-icon><Shop /></el-icon>
            {{ product.merchantName }}
          </div>

          <!-- SKU 选择 -->
          <div class="sku-section" v-for="(values, attrName) in skuAttributes" :key="attrName">
            <div class="sku-label">{{ attrName }}</div>
            <div class="sku-options">
              <span
                v-for="val in values"
                :key="val"
                class="sku-option"
                :class="{ active: selectedAttrs[attrName] === val, disabled: !isAttrAvailable(attrName, val) }"
                @click="selectAttr(attrName, val)"
              >{{ val }}</span>
            </div>
          </div>

          <div class="quantity-row">
            <span class="sku-label">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="selectedSku?.stock || 1" />
            <span class="stock-tip">库存 {{ selectedSku?.stock || 0 }} 件</span>
          </div>

          <div class="action-buttons">
            <el-button type="primary" size="large" :disabled="!selectedSku" @click="handleAddCart">
              加入购物车
            </el-button>
            <el-button size="large" :disabled="!selectedSku" @click="handleBuyNow">立即购买</el-button>
          </div>
        </div>
      </div>

      <!-- 详情与评价 -->
      <div class="detail-tabs page-card">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content" v-html="product.detailHtml || product.description"></div>
          </el-tab-pane>
          <el-tab-pane :label="`用户评价 (${reviews.length})`" name="reviews">
            <div v-if="reviews.length" class="review-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <span class="review-user">{{ review.nickname }}</span>
                  <el-rate :model-value="review.rating" disabled />
                  <span class="review-time">{{ review.createdAt }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <p v-if="review.merchantReply" class="merchant-reply">商家回复：{{ review.merchantReply }}</p>
              </div>
            </div>
            <div v-else class="empty-state"><p>暂无评价</p></div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Shop } from '@element-plus/icons-vue'
import { getProductDetail, getProductReviews, addCartItem } from '@/api'
import { formatPrice } from '@/utils/format'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const product = ref(null)
const reviews = ref([])
const activeTab = ref('detail')
const quantity = ref(1)
const currentImage = ref('')
const selectedAttrs = reactive({})

const allImages = computed(() => {
  if (!product.value) return []
  return [product.value.mainImage, ...(product.value.subImages || [])].filter(Boolean)
})

const skuAttributes = computed(() => {
  if (!product.value?.skus?.length) return {}
  const attrs = {}
  product.value.skus.forEach((sku) => {
    Object.entries(sku.attributes || {}).forEach(([key, val]) => {
      if (!attrs[key]) attrs[key] = new Set()
      attrs[key].add(val)
    })
  })
  return Object.fromEntries(Object.entries(attrs).map(([k, v]) => [k, [...v]]))
})

const selectedSku = computed(() => {
  if (!product.value?.skus?.length) return null
  const entries = Object.entries(selectedAttrs)
  if (!entries.length) return product.value.skus[0]
  return product.value.skus.find((sku) =>
    entries.every(([key, val]) => sku.attributes?.[key] === val),
  )
})

watch(selectedSku, (sku) => {
  if (sku?.skuImage) currentImage.value = sku.skuImage
})

function isAttrAvailable(attrName, val) {
  const test = { ...selectedAttrs, [attrName]: val }
  return product.value.skus.some((sku) =>
    Object.entries(test).every(([k, v]) => sku.attributes?.[k] === v),
  )
}

function selectAttr(attrName, val) {
  if (!isAttrAvailable(attrName, val)) return
  selectedAttrs[attrName] = val
}

function initDefaultSku() {
  if (!product.value?.skus?.length) return
  const first = product.value.skus[0]
  Object.entries(first.attributes || {}).forEach(([k, v]) => {
    selectedAttrs[k] = v
  })
}

async function handleAddCart() {
  if (!selectedSku.value) return
  try {
    await addCartItem({ productSkuId: selectedSku.value.id, quantity: quantity.value })
    await cartStore.refreshCount()
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function handleBuyNow() {
  await handleAddCart()
  router.push('/cart')
}

onMounted(async () => {
  loading.value = true
  try {
    const id = route.params.id
    const [detail, reviewData] = await Promise.all([
      getProductDetail(id),
      getProductReviews(id),
    ])
    product.value = detail
    reviews.value = reviewData
    currentImage.value = detail.mainImage
    initDefaultSku()
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-layout {
  display: grid;
  grid-template-columns: 480px 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 4px;
}

.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.thumb-list img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
}

.thumb-list img.active {
  border-color: var(--jd-red);
}

.product-title {
  font-size: 20px;
  line-height: 1.4;
  margin-bottom: 8px;
}

.product-subtitle {
  color: var(--jd-text-light);
  margin-bottom: 16px;
}

.price-box {
  background: #fef0f0;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.price-box .price { font-size: 28px; }
.original-price { color: var(--jd-text-light); text-decoration: line-through; margin-left: 12px; font-size: 14px; }

.shop-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--jd-text-light);
  margin-bottom: 20px;
  font-size: 14px;
}

.sku-section { margin-bottom: 16px; }
.sku-label { font-size: 14px; color: var(--jd-text-light); margin-bottom: 8px; }

.sku-options { display: flex; flex-wrap: wrap; gap: 8px; }

.sku-option {
  padding: 6px 16px;
  border: 1px solid var(--jd-border);
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.sku-option.active { border-color: var(--jd-red); color: var(--jd-red); }
.sku-option.disabled { opacity: 0.4; cursor: not-allowed; }

.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.stock-tip { font-size: 13px; color: var(--jd-text-light); }

.action-buttons { display: flex; gap: 12px; }
.action-buttons .el-button--primary { background: var(--jd-red); border-color: var(--jd-red); }

.detail-content { padding: 16px 0; line-height: 1.8; }

.review-item { padding: 16px 0; border-bottom: 1px solid var(--jd-border); }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.review-user { font-weight: 500; }
.review-time { color: var(--jd-text-light); font-size: 12px; margin-left: auto; }
.review-content { font-size: 14px; line-height: 1.6; }
.merchant-reply { margin-top: 8px; padding: 8px 12px; background: #f9f9f9; font-size: 13px; color: var(--jd-text-light); border-radius: 4px; }

@media (max-width: 900px) {
  .detail-layout { grid-template-columns: 1fr; }
}
</style>
