import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCart } from '@/api'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  async function refreshCount() {
    try {
      const data = await getCart()
      count.value = data.items.reduce((sum, item) => sum + item.quantity, 0)
    } catch {
      count.value = 0
    }
  }

  return { count, refreshCount }
})
