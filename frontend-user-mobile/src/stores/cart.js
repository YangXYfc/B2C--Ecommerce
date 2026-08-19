import { defineStore } from 'pinia'
import { api } from '../api/index.js'

export const useCartStore = defineStore('cart', {
  state: () => ({ count: 0 }),
  actions: {
    async refresh() {
      const items = await api.getCart()
      this.count = items.reduce((sum, item) => sum + Number(item.quantity), 0)
      return items
    },
    async add(skuId, quantity = 1) {
      await api.addCartItem({ skuId, quantity })
      await this.refresh()
    },
  },
})
