<script setup>
const props = defineProps({ modelValue: { type: Number, default: 1 }, max: { type: Number, default: 99 } })
const emit = defineEmits(['update:modelValue', 'change'])
const setValue = (value) => { const next = Math.max(1, Math.min(props.max, value)); emit('update:modelValue', next); emit('change', next) }
</script>

<template>
  <view class="stepper">
    <button :disabled="modelValue <= 1" @tap="setValue(modelValue - 1)">−</button>
    <text>{{ modelValue }}</text>
    <button :disabled="modelValue >= max" @tap="setValue(modelValue + 1)">＋</button>
  </view>
</template>

<style scoped>
.stepper { display: flex; align-items: center; border: 2rpx solid var(--line); border-radius: 10rpx; overflow: hidden; }
button { width: 58rpx; height: 54rpx; padding: 0; border-radius: 0; background: #fafafa; font-size: 28rpx; line-height: 52rpx; }
button::after { border: 0; }
text { width: 64rpx; text-align: center; font-size: 26rpx; }
</style>
