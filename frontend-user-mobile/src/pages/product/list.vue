<template>
  <view class="page product-list-page">
    <view class="search-panel surface">
      <view class="search-row"><input v-model="filters.keyword" placeholder="搜索商品" confirm-type="search" @confirm="reload" /><button @tap="reload">搜索</button></view>
      <scroll-view class="sorts" scroll-x><view class="sort-row"><text v-for="item in sorts" :key="item.value" :class="{ active: filters.sort === item.value }" @tap="setSort(item.value)">{{ item.label }}</text></view></scroll-view>
    </view>
    <StateBlock v-if="loading" title="正在寻找好物…" />
    <StateBlock v-else-if="!products.length" title="没有找到相关商品" description="换个关键词或分类试试看" action="清除筛选" @action="clear" />
    <view v-else class="grid"><ProductCard v-for="product in products" :key="product.id" :product="product" /></view>
  </view>
</template>
<script setup>
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
import { reactive, ref } from 'vue'
import ProductCard from '../../components/ProductCard.vue'
import StateBlock from '../../components/StateBlock.vue'
import { api } from '../../api/index.js'
import { buildProductQuery } from '../../utils/catalog.js'
const products = ref([]); const loading = ref(false)
const filters = reactive({ keyword: '', categoryId: '', sort: 'sales', page: 1, size: 20 })
const sorts = [{ label: '综合', value: '' }, { label: '销量', value: 'sales' }, { label: '价格升序', value: 'price_asc' }, { label: '价格降序', value: 'price_desc' }]
onLoad((query) => { filters.keyword = query.keyword || ''; filters.categoryId = query.categoryId || ''; reload() })
onPullDownRefresh(async () => { await reload(); uni.stopPullDownRefresh() })
async function reload() { loading.value = true; try { products.value = (await api.getProducts(buildProductQuery(filters))).records } catch (error) { uni.showToast({ title: error.message, icon: 'none' }) } finally { loading.value = false } }
function setSort(value) { filters.sort = value; reload() }
function clear() { filters.keyword = ''; filters.categoryId = ''; filters.sort = ''; reload() }
</script>
<style scoped>
.search-panel { position: sticky; top: 0; z-index: 5; padding: 20rpx 24rpx 12rpx; }
.search-row { height: 76rpx; padding-left: 28rpx; border-radius: 999rpx; background: #f4f4f4; display: flex; align-items: center; }
.search-row input { flex: 1; font-size: 27rpx; }
.search-row button { width: 120rpx; height: 68rpx; margin: 0 4rpx; border: 0; border-radius: 999rpx; background: var(--brand); color: #fff; font-size: 25rpx; line-height: 68rpx; }
.sort-row { width: max-content; display: flex; gap: 54rpx; padding: 24rpx 10rpx 14rpx; color: #777; font-size: 25rpx; }
.sort-row .active { color: var(--brand); font-weight: 700; }
.grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 18rpx; padding: 22rpx; }
</style>
