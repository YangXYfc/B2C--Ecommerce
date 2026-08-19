<template>
  <view class="brand-header">
    <view class="brand-line">
      <image class="brand-mark" src="/static/images/brand-mark.png" mode="aspectFit" />
      <text class="brand-name">悦选</text>
      <text class="brand-divider">｜</text>
      <text class="brand-slogan">发现你的生活好物</text>
    </view>
    <view class="search" @tap="submit">
      <input v-model="keyword" confirm-type="search" placeholder="搜索商品" @confirm="submit" />
      <text class="search-action" @tap.stop="submit">搜索</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const keyword = ref('')
function submit() {
  uni.navigateTo({ url: `/pages/product/list?keyword=${encodeURIComponent(keyword.value)}` })
}
</script>

<style scoped>
.brand-header { padding: calc(24rpx + env(safe-area-inset-top)) 28rpx 22rpx; background: #fff; }
.brand-line { height: 64rpx; display: flex; align-items: center; }
.brand-mark { width: 54rpx; height: 54rpx; margin-right: 10rpx; }
.brand-name { color: var(--brand); font-size: 44rpx; font-weight: 800; letter-spacing: 2rpx; }
.brand-divider { margin: 0 12rpx; color: #d5d5d5; }
.brand-slogan { color: #555; font-size: 26rpx; }
.search { margin-top: 14rpx; height: 84rpx; border: 2rpx solid var(--brand); border-radius: 999rpx; padding: 0 18rpx 0 30rpx; display: flex; align-items: center; gap: 16rpx; }
.search input { flex: 1; font-size: 28rpx; }
.search-action { color: var(--brand); font-size: 26rpx; font-weight: 600; padding: 18rpx; }
</style>
