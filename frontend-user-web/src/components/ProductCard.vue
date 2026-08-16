<template>
  <div class="product-card" @click="$router.push(`/products/${product.id}`)">
    <div class="product-image">
      <img :src="product.mainImage" :alt="product.name" loading="lazy" />
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-subtitle" v-if="product.subtitle">{{ product.subtitle }}</p>
      <div class="product-bottom">
        <span class="price">
          <span class="price-symbol">¥</span>{{ formatPrice(product.price) }}
        </span>
        <span class="sales">已售 {{ product.salesCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatPrice } from '@/utils/format'

defineProps({
  product: { type: Object, required: true },
})
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  aspect-ratio: 1;
  overflow: hidden;
  background: #fafafa;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-subtitle {
  font-size: 12px;
  color: var(--jd-text-light);
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.price {
  font-size: 18px;
}

.sales {
  font-size: 12px;
  color: var(--jd-text-light);
}
</style>
