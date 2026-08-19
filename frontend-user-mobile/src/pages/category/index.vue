<template>
  <view class="category-page">
    <scroll-view class="roots" scroll-y>
      <view v-for="category in categories" :key="category.id" :class="['root', { active: current?.id === category.id }]" @tap="current = category">{{ category.name }}</view>
    </scroll-view>
    <scroll-view class="children" scroll-y>
      <view v-if="current" class="category-banner"><text>{{ current.scene }}</text><text class="category-sub">为生活挑选恰到好处的新品</text></view>
      <view class="child-grid">
        <view v-for="child in current?.children" :key="child.id" class="child" @tap="open(child.id)"><image class="child-image" :src="child.image" mode="aspectFill" /><text>{{ child.name }}</text></view>
      </view>
      <button v-if="current" class="all-button" @tap="open(current.id)">查看全部{{ current.name }}</button>
    </scroll-view>
  </view>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../../api/index.js'
const categories = ref([]); const current = ref(null)
onMounted(async () => { categories.value = await api.getCategories(); current.value = categories.value[0] })
function open(id) { uni.navigateTo({ url: `/pages/product/list?categoryId=${id}` }) }
</script>
<style scoped>
.category-page { height: calc(100vh - var(--window-top) - var(--window-bottom)); display: flex; background: #fff; }
.roots { width: 190rpx; height: 100%; background: #f6f6f6; }
.root { padding: 34rpx 18rpx; color: #666; text-align: center; font-size: 27rpx; border-left: 6rpx solid transparent; }
.root.active { background: #fff; color: var(--brand); font-weight: 700; border-left-color: var(--brand); }
.children { flex: 1; height: 100%; padding: 28rpx; }
.category-banner { padding: 38rpx 30rpx; border-radius: 20rpx; background: var(--soft-red); display: flex; flex-direction: column; font-size: 34rpx; font-weight: 700; }
.category-sub { margin-top: 10rpx; color: #777; font-size: 23rpx; font-weight: 400; }
.child-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 28rpx 16rpx; margin-top: 36rpx; }
.child { display: flex; flex-direction: column; align-items: center; gap: 12rpx; font-size: 24rpx; }
.child-image { width: 104rpx; height: 104rpx; border-radius: 50%; background: #f3f3f3; }
.all-button { margin: 50rpx auto; border: 2rpx solid var(--brand); border-radius: 999rpx; background: #fff; color: var(--brand); font-size: 26rpx; }
</style>
