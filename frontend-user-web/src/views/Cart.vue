<template>
  <div class="page-container">
    <div class="page-card" v-loading="loading">
      <div class="cart-header">
        <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        <span class="cart-title">购物车 ({{ cartData.items?.length || 0 }})</span>
      </div>

      <template v-if="cartData.items?.length">
        <div v-for="item in cartData.items" :key="item.id" class="cart-item">
          <el-checkbox v-model="item.selected" :true-value="1" :false-value="0" @change="handleItemChange(item)" />
          <img :src="item.productImage" class="item-image" @click="$router.push(`/products/${item.productId}`)" />
          <div class="item-info">
            <h3 @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h3>
            <p class="sku-name">{{ item.skuName }}</p>
            <p class="merchant">{{ item.merchantName }}</p>
          </div>
          <div class="item-price price">¥{{ formatPrice(item.price) }}</div>
          <el-input-number
            v-model="item.quantity"
            :min="1"
            :max="item.stock"
            size="small"
            @change="handleQuantityChange(item)"
          />
          <div class="item-subtotal price">¥{{ formatPrice(item.price * item.quantity) }}</div>
          <el-button type="danger" link @click="handleDelete(item)">删除</el-button>
        </div>

        <div class="cart-footer">
          <div class="footer-left">
            <el-button type="danger" link @click="handleDeleteSelected">删除选中</el-button>
          </div>
          <div class="footer-right">
            <span>已选 <strong>{{ selectedCount }}</strong> 件，合计：</span>
            <span class="total-price price">¥{{ formatPrice(selectedTotal) }}</span>
            <el-button type="primary" size="large" :disabled="!selectedCount" @click="goCheckout">
              去结算
            </el-button>
          </div>
        </div>
      </template>

      <div v-else class="empty-state">
        <el-icon><ShoppingCart /></el-icon>
        <p>购物车是空的</p>
        <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { getCart, updateCartItem, deleteCartItem, deleteSelectedCart } from '@/api'
import { formatPrice } from '@/utils/format'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const cartData = ref({ items: [], totalAmount: 0 })

const selectedItems = computed(() => cartData.value.items?.filter((i) => i.selected) || [])
const selectedCount = computed(() => selectedItems.value.reduce((s, i) => s + i.quantity, 0))
const selectedTotal = computed(() => selectedItems.value.reduce((s, i) => s + i.price * i.quantity, 0))

const selectAll = computed({
  get: () => cartData.value.items?.length > 0 && cartData.value.items.every((i) => i.selected),
  set: () => {},
})

async function loadCart() {
  loading.value = true
  try {
    cartData.value = await getCart()
    await cartStore.refreshCount()
  } finally {
    loading.value = false
  }
}

async function handleSelectAll(val) {
  for (const item of cartData.value.items) {
    item.selected = val ? 1 : 0
    await updateCartItem(item.id, { selected: val })
  }
  await loadCart()
}

async function handleItemChange(item) {
  await updateCartItem(item.id, { selected: !!item.selected })
}

async function handleQuantityChange(item) {
  await updateCartItem(item.id, { quantity: item.quantity })
  await loadCart()
}

async function handleDelete(item) {
  await deleteCartItem(item.id)
  ElMessage.success('已删除')
  await loadCart()
}

async function handleDeleteSelected() {
  await ElMessageBox.confirm('确定删除选中的商品吗？', '提示')
  await deleteSelectedCart()
  ElMessage.success('已删除')
  await loadCart()
}

function goCheckout() {
  if (!selectedCount.value) return
  router.push('/checkout')
}

onMounted(loadCart)
</script>

<style scoped>
.cart-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--jd-border);
  margin-bottom: 16px;
}

.cart-title { font-size: 18px; font-weight: 600; }

.cart-item {
  display: grid;
  grid-template-columns: 40px 80px 1fr 100px 120px 100px 60px;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--jd-border);
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.item-info h3 {
  font-size: 14px;
  font-weight: normal;
  cursor: pointer;
  margin-bottom: 4px;
}

.item-info h3:hover { color: var(--jd-red); }
.sku-name, .merchant { font-size: 12px; color: var(--jd-text-light); }

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-price { font-size: 24px; }

.footer-right .el-button--primary {
  background: var(--jd-red);
  border-color: var(--jd-red);
}
</style>
