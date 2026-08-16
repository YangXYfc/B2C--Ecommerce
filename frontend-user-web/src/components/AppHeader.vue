<template>
  <header class="app-header">
    <div class="header-top">
      <div class="page-container header-inner">
        <div class="logo" @click="$router.push('/')">
          <span class="logo-text">JD</span>
          <span class="logo-sub">商城</span>
        </div>

        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索商品"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>

        <div class="header-actions">
          <router-link to="/cart" class="action-item cart-link">
            <el-badge :value="cartStore.count" :hidden="!cartStore.count" :max="99">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
            <span>购物车</span>
          </router-link>

          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <span class="action-item user-link">
                <el-icon><User /></el-icon>
                {{ userStore.profile?.nickname || '我的' }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/refunds')">退款/售后</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/addresses')">收货地址</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="action-item">登录</router-link>
            <router-link to="/register" class="action-item register-link">注册</router-link>
          </template>
        </div>
      </div>
    </div>

    <nav class="header-nav">
      <div class="page-container nav-inner">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/products" class="nav-item">全部商品</router-link>
        <router-link to="/orders" class="nav-item" v-if="userStore.isLoggedIn">我的订单</router-link>
        <router-link to="/merchant-apply" class="nav-item" v-if="userStore.isLoggedIn">商家入驻</router-link>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, User, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')

onMounted(async () => {
  if (userStore.isLoggedIn) {
    await userStore.fetchProfile()
    await cartStore.refreshCount()
  }
})

function handleSearch() {
  router.push({ name: 'ProductList', query: { keyword: keyword.value || undefined } })
}

function handleLogout() {
  userStore.logout()
  cartStore.count = 0
  router.push('/')
}
</script>

<style scoped>
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-top {
  padding: 20px 0;
}

.header-inner {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: baseline;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-text {
  font-size: 36px;
  font-weight: bold;
  color: var(--jd-red);
  font-style: italic;
}

.logo-sub {
  font-size: 18px;
  color: var(--jd-text);
  margin-left: 4px;
}

.search-box {
  flex: 1;
  max-width: 520px;
}

.search-box :deep(.el-input-group__append) {
  background: var(--jd-red);
  color: #fff;
  border-color: var(--jd-red);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 14px;
  color: var(--jd-text);
}

.action-item:hover {
  color: var(--jd-red);
}

.register-link {
  color: var(--jd-red);
  font-weight: 500;
}

.cart-link {
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
}

.user-link {
  cursor: pointer;
}

.header-nav {
  background: var(--jd-red);
}

.nav-inner {
  display: flex;
  gap: 32px;
}

.nav-item {
  color: #fff;
  padding: 12px 0;
  font-size: 15px;
  transition: opacity 0.2s;
}

.nav-item:hover,
.nav-item.router-link-active {
  opacity: 0.85;
  font-weight: 500;
}
</style>
