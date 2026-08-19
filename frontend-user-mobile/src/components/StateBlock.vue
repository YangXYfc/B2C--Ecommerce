<template>
  <view class="state"><text class="title">{{ title }}</text><text v-if="description" class="description">{{ description }}</text><button v-if="action" class="retry" @tap="$emit('action')">{{ action }}</button></view>
</template>
<script setup>
defineProps({ title: String, description: String, action: String })
defineEmits(['action'])
</script>
<style scoped>
.state { min-height: 320rpx; padding: 80rpx 40rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; }
.title { font-size: 30rpx; font-weight: 600; }
.description { margin-top: 14rpx; color: var(--muted); font-size: 25rpx; line-height: 38rpx; }
.retry { margin-top: 28rpx; min-width: 180rpx; border: 0; border-radius: 999rpx; background: var(--brand); color: #fff; font-size: 26rpx; }
</style>
