import { seed } from './data.js'

const clone = (value) => JSON.parse(JSON.stringify(value))
const now = () => new Date().toISOString().replace('T', ' ').slice(0, 16)

export function createMockService(storage) {
  const key = 'mock-state-v1'
  let state = storage.get(key, clone(seed))
  const save = () => storage.set(key, state)
  const nextId = (items) => Math.max(0, ...items.map((item) => Number(item.id))) + 1
  const merchant = (id) => state.merchants.find((item) => Number(item.id) === Number(id))
  const sku = (id) => state.skus.find((item) => Number(item.id) === Number(id))
  const product = (id) => state.products.find((item) => Number(item.id) === Number(id))

  const enrichCart = (line) => {
    const currentSku = sku(line.skuId)
    const currentProduct = product(currentSku.productId)
    const currentMerchant = merchant(currentProduct.merchantId)
    return {
      ...clone(line),
      stock: currentSku.stock,
      skuName: currentSku.name,
      price: currentSku.price,
      productId: currentProduct.id,
      productName: currentProduct.name,
      productImage: currentProduct.mainImage,
      merchantId: currentMerchant.id,
      merchantName: currentMerchant.name,
    }
  }

  const enrichOrder = (order) => {
    const items = order.itemIds.map((skuId) => {
      const currentSku = sku(skuId)
      const currentProduct = product(currentSku.productId)
      return { skuId, productId: currentProduct.id, name: currentProduct.name, image: currentProduct.mainImage, skuName: currentSku.name, price: currentSku.price, quantity: order.quantities?.[skuId] || 1 }
    })
    return { ...clone(order), merchantName: merchant(order.merchantId)?.name, address: clone(state.addresses.find((item) => item.id === order.addressId)), items }
  }

  return {
    reset() { state = clone(seed); save() },
    async login(form) {
      const user = state.users.find((item) => item.username === form.username && item.password === form.password)
      if (!user) throw new Error('账号或密码错误')
      const safeUser = clone(user); delete safeUser.password
      return { token: `mock-token-${user.id}`, user: safeUser }
    },
    async register(form) {
      if (state.users.some((item) => item.username === form.username)) throw new Error('账号已存在')
      const user = { id: nextId(state.users), username: form.username, password: form.password, phone: form.phone, nickname: form.nickname || form.username, email: '', gender: 0, avatar: '', role: 'USER' }
      state.users.push(user); save()
      return { id: user.id, username: user.username }
    },
    async getBanners() { return clone(state.banners) },
    async getCategories() { return clone(state.categories) },
    async getProducts(query = {}) {
      let values = [...state.products]
      if (query.keyword) values = values.filter((item) => `${item.name}${item.subtitle}`.includes(query.keyword))
      if (query.categoryId) {
        const category = state.categories.find((item) => item.id === Number(query.categoryId))
        const ids = category ? [category.id, ...category.children.map((child) => child.id)] : [Number(query.categoryId)]
        values = values.filter((item) => ids.includes(item.categoryId))
      }
      if (query.sort === 'sales') values.sort((a, b) => b.salesCount - a.salesCount)
      if (query.sort === 'price_asc') values.sort((a, b) => a.price - b.price)
      if (query.sort === 'price_desc') values.sort((a, b) => b.price - a.price)
      const page = Number(query.page || 1); const size = Number(query.size || 10)
      return { records: clone(values.slice((page - 1) * size, page * size)), total: values.length }
    },
    async getProduct(id) {
      const value = product(id)
      if (!value) throw new Error('商品不存在')
      return { ...clone(value), merchantName: merchant(value.merchantId)?.name, skus: clone(state.skus.filter((item) => item.productId === value.id)), reviews: clone(state.reviews.filter((item) => item.productId === value.id)) }
    },
    async getCart() { return state.cart.map(enrichCart) },
    async addCartItem({ skuId, quantity = 1 }) {
      const currentSku = sku(skuId)
      if (!currentSku) throw new Error('商品规格不存在')
      const existing = state.cart.find((item) => item.skuId === Number(skuId))
      if (existing) existing.quantity = Math.min(currentSku.stock, existing.quantity + Number(quantity))
      else state.cart.push({ id: nextId(state.cart), skuId: Number(skuId), quantity: Math.min(currentSku.stock, Number(quantity)), selected: 1 })
      save(); return this.getCart()
    },
    async updateCartItem(id, patch) {
      const line = state.cart.find((item) => item.id === Number(id))
      if (!line) throw new Error('购物车商品不存在')
      if (patch.quantity != null) line.quantity = Math.max(1, Math.min(sku(line.skuId).stock, Number(patch.quantity)))
      if (patch.selected != null) line.selected = Number(Boolean(patch.selected))
      save(); return enrichCart(line)
    },
    async removeCartItem(id) { state.cart = state.cart.filter((item) => item.id !== Number(id)); save() },
    async clearSelectedCart() { state.cart = state.cart.filter((item) => !item.selected); save() },
    async getAddresses() { return clone(state.addresses) },
    async saveAddress(form) {
      const value = { ...clone(form), id: form.id ? Number(form.id) : nextId(state.addresses), isDefault: Number(Boolean(form.isDefault)) }
      if (value.isDefault) state.addresses.forEach((item) => { item.isDefault = 0 })
      const index = state.addresses.findIndex((item) => item.id === value.id)
      if (index >= 0) state.addresses[index] = value; else state.addresses.push(value)
      save(); return clone(value)
    },
    async deleteAddress(id) { state.addresses = state.addresses.filter((item) => item.id !== Number(id)); save() },
    async setDefaultAddress(id) { state.addresses.forEach((item) => { item.isDefault = Number(item.id === Number(id)) }); save() },
    async createOrder({ addressId, cartItemIds = [], skuId, quantity = 1, remark = '' }) {
      let lines = cartItemIds.length ? state.cart.filter((item) => cartItemIds.includes(item.id)).map(enrichCart) : []
      if (skuId) lines = [enrichCart({ id: 0, skuId: Number(skuId), quantity: Number(quantity), selected: 1 })]
      if (!state.addresses.some((item) => item.id === Number(addressId))) throw new Error('请选择收货地址')
      if (!lines.length) throw new Error('请选择结算商品')
      const groups = new Map()
      lines.forEach((line) => { if (!groups.has(line.merchantId)) groups.set(line.merchantId, []); groups.get(line.merchantId).push(line) })
      const created = []
      for (const [merchantId, group] of groups) {
        const id = nextId(state.orders)
        const quantities = Object.fromEntries(group.map((line) => [line.skuId, line.quantity]))
        const totalAmount = group.reduce((sum, line) => sum + line.price * line.quantity, 0)
        const order = { id, orderNo: `ORD${Date.now()}${id}`, merchantId, totalAmount, payAmount: null, status: 0, addressId: Number(addressId), remark, createdAt: now(), itemIds: group.map((line) => line.skuId), quantities }
        state.orders.unshift(order); created.push(enrichOrder(order))
      }
      if (cartItemIds.length) state.cart = state.cart.filter((item) => !cartItemIds.includes(item.id))
      save(); return created.length === 1 ? created[0] : created
    },
    async getOrders(query = {}) {
      const values = query.status === '' || query.status == null ? state.orders : state.orders.filter((item) => item.status === Number(query.status))
      return { records: values.map(enrichOrder), total: values.length }
    },
    async getOrder(id) { const value = state.orders.find((item) => item.id === Number(id)); if (!value) throw new Error('订单不存在'); return enrichOrder(value) },
    async payOrder(id) { const value = state.orders.find((item) => item.id === Number(id)); if (value.status !== 0) throw new Error('订单状态不允许支付'); value.status = 1; value.payAmount = value.totalAmount; save(); return enrichOrder(value) },
    async cancelOrder(id) { const value = state.orders.find((item) => item.id === Number(id)); if (value.status !== 0) throw new Error('订单状态不允许取消'); value.status = 5; save(); return enrichOrder(value) },
    async confirmOrder(id) { const value = state.orders.find((item) => item.id === Number(id)); if (value.status !== 2) throw new Error('订单状态不允许确认收货'); value.status = 4; save(); return enrichOrder(value) },
    async createReview(form) { const review = { id: nextId(state.reviews), nickname: '张三', createdAt: now(), ...clone(form) }; state.reviews.unshift(review); save(); return clone(review) },
    async getRefunds() { return { records: clone(state.refunds), total: state.refunds.length } },
    async getRefund(id) { const value = state.refunds.find((item) => item.id === Number(id)); if (!value) throw new Error('退款记录不存在'); return clone(value) },
    async createRefund(form) { const id = nextId(state.refunds); const value = { id, refundNo: `RFD${Date.now()}${id}`, status: 0, createdAt: now(), ...clone(form) }; state.refunds.unshift(value); save(); return clone(value) },
    async submitReturnLogistics(id, form) { const value = state.refunds.find((item) => item.id === Number(id)); value.status = 3; Object.assign(value, clone(form)); save(); return clone(value) },
    async appealRefund(id, form) { const value = state.refunds.find((item) => item.id === Number(id)); value.status = 6; value.appealReason = form.reason; save(); return clone(value) },
    async getProfile() { const user = clone(state.users[0]); delete user.password; return user },
    async updateProfile(form) { Object.assign(state.users[0], clone(form)); save(); return this.getProfile() },
    async changePassword(form) { if (state.users[0].password !== form.oldPassword) throw new Error('原密码错误'); state.users[0].password = form.newPassword; save() },
    async applyMerchant(form) { return { id: Date.now(), status: 0, ...clone(form) } },
    async uploadImage(path) { return { url: path } },
  }
}
