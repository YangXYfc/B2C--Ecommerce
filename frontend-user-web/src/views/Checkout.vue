<template>
  <div class="page-container">
    <div class="page-card checkout-page" v-loading="loading">
      <h2>确认订单</h2>

      <!-- 收货地址 -->
      <section class="checkout-section">
        <div class="section-header">
          <h3>收货地址</h3>
          <el-button link type="primary" @click="$router.push('/addresses')">管理地址</el-button>
        </div>
        <div v-if="addresses.length" class="address-list">
          <div
            v-for="addr in addresses"
            :key="addr.id"
            class="address-item"
            :class="{ active: selectedAddressId === addr.id }"
            @click="selectedAddressId = addr.id"
          >
            <div class="addr-name">{{ addr.name }} {{ addr.phone }}</div>
            <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
            <el-tag v-if="addr.isDefault" size="small" type="danger">默认</el-tag>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>暂无收货地址</p>
          <el-button type="primary" @click="$router.push('/addresses/edit')">新增地址</el-button>
        </div>
      </section>

      <!-- 商品清单 -->
      <section class="checkout-section">
        <h3>商品清单</h3>
        <div v-for="item in selectedItems" :key="item.id" class="checkout-item">
          <img :src="item.productImage" />
          <div class="item-info">
            <p>{{ item.productName }}</p>
            <p class="sku">{{ item.skuName }}</p>
          </div>
          <span class="price">¥{{ formatPrice(item.price) }}</span>
          <span>x{{ item.quantity }}</span>
          <span class="price">¥{{ formatPrice(item.price * item.quantity) }}</span>
        </div>
      </section>

      <!-- 备注 -->
      <section class="checkout-section">
        <h3>订单备注</h3>
        <el-input v-model="remark" type="textarea" :rows="2" placeholder="选填：对本次交易的说明" />
      </section>

      <!-- 提交 -->
      <div class="checkout-footer">
        <span>共 {{ totalCount }} 件，合计：</span>
        <span class="total-price price">¥{{ formatPrice(totalAmount) }}</span>
        <el-button type="primary" size="large" :disabled="!selectedAddressId || !selectedItems.length" :loading="submitting" @click="handleSubmit">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart, getAddresses, createOrder } from '@/api'
import { formatPrice } from '@/utils/format'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const submitting = ref(false)
const addresses = ref([])
const selectedItems = ref([])
const selectedAddressId = ref(null)
const remark = ref('')

const totalAmount = computed(() => selectedItems.value.reduce((s, i) => s + i.price * i.quantity, 0))
const totalCount = computed(() => selectedItems.value.reduce((s, i) => s + i.quantity, 0))

async function loadData() {
  loading.value = true
  try {
    const [cart, addrList] = await Promise.all([getCart(), getAddresses()])
    selectedItems.value = cart.items.filter((i) => i.selected)
    addresses.value = addrList
    const defaultAddr = addrList.find((a) => a.isDefault) || addrList[0]
    selectedAddressId.value = defaultAddr?.id || null
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const orders = await createOrder({
      addressId: selectedAddressId.value,
      remark: remark.value,
      cartItemIds: selectedItems.value.map((i) => i.id),
    })
    await cartStore.refreshCount()
    ElMessage.success(`下单成功，共 ${orders.length} 个订单`)
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.checkout-page h2 { margin-bottom: 24px; }

.checkout-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--jd-border);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.checkout-section h3 { font-size: 16px; margin-bottom: 12px; }

.address-list { display: flex; flex-wrap: wrap; gap: 12px; }

.address-item {
  width: 280px;
  padding: 12px 16px;
  border: 2px solid var(--jd-border);
  border-radius: 4px;
  cursor: pointer;
}

.address-item.active { border-color: var(--jd-red); background: #fef0f0; }
.addr-name { font-weight: 500; margin-bottom: 4px; }
.addr-detail { font-size: 13px; color: var(--jd-text-light); margin-bottom: 4px; }

.checkout-item {
  display: grid;
  grid-template-columns: 60px 1fr 80px 60px 80px;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.checkout-item img { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.checkout-item .sku { font-size: 12px; color: var(--jd-text-light); }

.checkout-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  padding-top: 16px;
}

.total-price { font-size: 24px; }
.checkout-footer .el-button--primary { background: var(--jd-red); border-color: var(--jd-red); }
</style>
