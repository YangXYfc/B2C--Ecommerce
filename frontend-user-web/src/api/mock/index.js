import {
  mockCategories,
  mockProducts,
  mockSkus,
  mockBanners,
  mockReviews,
  mockMerchants,
  enrichProduct,
  enrichSku,
  getMerchantName,
} from './data'
import { getMockState, getCurrentUserId, createToken, persist, nextAutoId, ok, fail, delay, requireLogin } from './storage'

// ========== 认证 ==========

export async function register(data) {
  await delay()
  const state = getMockState()
  if (state.users.some((u) => u.username === data.username)) {
    return fail('用户名已存在')
  }
  const user = {
    id: nextAutoId(),
    username: data.username,
    password: data.password,
    phone: data.phone,
    email: data.email || '',
    nickname: data.nickname || data.username,
    gender: 0,
    avatar: null,
    role: 'USER',
  }
  state.users.push(user)
  persist(state)
  return ok({ message: '注册成功' })
}

export async function login(data) {
  await delay()
  const state = getMockState()
  const user = state.users.find((u) => u.username === data.username && u.password === data.password)
  if (!user) {
    return fail('用户名或密码错误')
  }
  state.currentUserId = user.id
  persist(state)
  const token = createToken(user)
  localStorage.setItem('jd_token', token)
  const { password, ...profile } = user
  return ok({ token, user: profile })
}

export async function getProfile() {
  await delay()
  const userId = getCurrentUserId()
  if (!userId) return fail('未登录')
  const state = getMockState()
  const user = state.users.find((u) => u.id === userId)
  if (!user) return fail('用户不存在')
  const { password, ...profile } = user
  return ok(profile)
}

export async function updateProfile(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const user = state.users.find((u) => u.id === userId)
  Object.assign(user, { nickname: data.nickname, phone: data.phone, email: data.email, gender: data.gender, avatar: data.avatar })
  persist(state)
  const { password, ...profile } = user
  return ok(profile)
}

export async function updatePassword(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const user = state.users.find((u) => u.id === userId)
  if (user.password !== data.oldPassword) {
    return fail('旧密码不正确')
  }
  user.password = data.newPassword
  persist(state)
  return ok({ message: '密码修改成功' })
}

export async function merchantApply(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  state.merchantApplications.push({
    id: nextAutoId(),
    userId,
    shopName: data.shopName,
    contactPhone: data.contactPhone,
    description: data.description,
    status: 0,
    createdAt: new Date().toISOString(),
  })
  persist(state)
  return ok({ message: '商家入驻申请已提交，请等待审核' })
}

export async function uploadImage(file) {
  await delay(500)
  requireLogin()
  const url = URL.createObjectURL(file)
  return ok({ url })
}

// ========== 商品与分类 ==========

export async function getBanners() {
  await delay()
  return ok(mockBanners.filter((b) => b.status !== 0))
}

export async function getCategories() {
  await delay()
  const topLevel = mockCategories.filter((c) => c.parentId === 0)
  return ok(topLevel.map((cat) => ({
    ...cat,
    children: mockCategories.filter((c) => c.parentId === cat.id),
  })))
}

export async function getProducts(params = {}) {
  await delay()
  let list = mockProducts.filter((p) => p.status === 1)

  if (params.categoryId) {
    const catId = Number(params.categoryId)
    const childIds = mockCategories.filter((c) => c.parentId === catId).map((c) => c.id)
    const grandChildIds = mockCategories.filter((c) => childIds.includes(c.parentId)).map((c) => c.id)
    const allIds = [catId, ...childIds, ...grandChildIds]
    list = list.filter((p) => allIds.includes(p.categoryId))
  }

  if (params.keyword) {
    const kw = params.keyword.toLowerCase()
    list = list.filter((p) => p.name.toLowerCase().includes(kw) || p.subtitle?.toLowerCase().includes(kw))
  }

  if (params.sort === 'price_asc') {
    list = [...list].sort((a, b) => a.price - b.price)
  } else if (params.sort === 'price_desc') {
    list = [...list].sort((a, b) => b.price - a.price)
  } else if (params.sort === 'sales') {
    list = [...list].sort((a, b) => b.salesCount - a.salesCount)
  }

  const page = Number(params.page) || 1
  const size = Number(params.size) || 12
  const total = list.length
  const start = (page - 1) * size
  const records = list.slice(start, start + size).map(enrichProduct)

  return ok({ records, total, page, size })
}

export async function getProductDetail(id) {
  await delay()
  const product = mockProducts.find((p) => p.id === Number(id))
  if (!product || product.status !== 1) {
    return fail('商品不存在或已下架')
  }
  const skus = mockSkus.filter((s) => s.productId === product.id)
  const merchant = mockMerchants.find((m) => m.id === product.merchantId)
  return ok({
    ...enrichProduct(product),
    skus,
    merchant,
  })
}

export async function getProductReviews(id) {
  await delay()
  const reviews = mockReviews.filter((r) => r.productId === Number(id))
  return ok(reviews)
}

// ========== 地址 ==========

export async function getAddresses() {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  return ok(state.addresses.filter((a) => a.userId === userId))
}

export async function createAddress(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  if (data.isDefault) {
    state.addresses.forEach((a) => {
      if (a.userId === userId) a.isDefault = 0
    })
  }
  const address = { id: nextAutoId(), userId, ...data, isDefault: data.isDefault ? 1 : 0 }
  state.addresses.push(address)
  persist(state)
  return ok(address)
}

export async function updateAddress(id, data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const address = state.addresses.find((a) => a.id === Number(id) && a.userId === userId)
  if (!address) return fail('地址不存在')
  if (data.isDefault) {
    state.addresses.forEach((a) => {
      if (a.userId === userId) a.isDefault = 0
    })
  }
  Object.assign(address, data, { isDefault: data.isDefault ? 1 : 0 })
  persist(state)
  return ok(address)
}

export async function deleteAddress(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  state.addresses = state.addresses.filter((a) => !(a.id === Number(id) && a.userId === userId))
  persist(state)
  return ok({ message: '删除成功' })
}

export async function setDefaultAddress(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  state.addresses.forEach((a) => {
    if (a.userId === userId) a.isDefault = a.id === Number(id) ? 1 : 0
  })
  persist(state)
  return ok({ message: '已设为默认地址' })
}

// ========== 购物车 ==========

function buildCartItem(cartRow) {
  const sku = mockSkus.find((s) => s.id === cartRow.productSkuId)
  if (!sku) return null
  const product = mockProducts.find((p) => p.id === sku.productId)
  return {
    id: cartRow.id,
    productSkuId: cartRow.productSkuId,
    productId: sku.productId,
    productName: product?.name,
    skuName: sku.skuName,
    productImage: sku.skuImage || product?.mainImage,
    price: sku.price,
    stock: sku.stock,
    quantity: cartRow.quantity,
    selected: cartRow.selected,
    merchantId: product?.merchantId,
    merchantName: getMerchantName(product?.merchantId),
  }
}

export async function getCart() {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const items = state.cart
    .filter((c) => c.userId === userId)
    .map(buildCartItem)
    .filter(Boolean)
  const selectedItems = items.filter((i) => i.selected)
  const totalAmount = selectedItems.reduce((sum, i) => sum + i.price * i.quantity, 0)
  return ok({ items, totalAmount, selectedCount: selectedItems.length })
}

export async function addCartItem(data) {
  await delay()
  const userId = requireLogin()
  const sku = mockSkus.find((s) => s.id === data.productSkuId)
  if (!sku) return fail('商品规格不存在')
  if (sku.stock < (data.quantity || 1)) return fail('库存不足')

  const state = getMockState()
  const existing = state.cart.find((c) => c.userId === userId && c.productSkuId === data.productSkuId)
  if (existing) {
    existing.quantity += data.quantity || 1
    existing.selected = 1
  } else {
    state.cart.push({
      id: nextAutoId(),
      userId,
      productSkuId: data.productSkuId,
      quantity: data.quantity || 1,
      selected: 1,
    })
  }
  persist(state)
  return ok({ message: '已加入购物车' })
}

export async function updateCartItem(id, data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const item = state.cart.find((c) => c.id === Number(id) && c.userId === userId)
  if (!item) return fail('购物车项不存在')

  if (data.quantity !== undefined) {
    const sku = mockSkus.find((s) => s.id === item.productSkuId)
    if (data.quantity > sku.stock) return fail('超出库存')
    item.quantity = data.quantity
  }
  if (data.selected !== undefined) {
    item.selected = data.selected ? 1 : 0
  }
  persist(state)
  return ok(buildCartItem(item))
}

export async function deleteCartItem(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  state.cart = state.cart.filter((c) => !(c.id === Number(id) && c.userId === userId))
  persist(state)
  return ok({ message: '已删除' })
}

export async function deleteSelectedCart() {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  state.cart = state.cart.filter((c) => !(c.userId === userId && c.selected))
  persist(state)
  return ok({ message: '已删除选中商品' })
}

// ========== 订单 ==========

function buildOrderDetail(order, state) {
  const items = state.orderItems.filter((i) => i.orderId === order.id)
  return {
    ...order,
    merchantName: getMerchantName(order.merchantId),
    items,
  }
}

export async function getOrders(params = {}) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  let list = state.orders.filter((o) => o.userId === userId)
  if (params.status !== undefined && params.status !== '' && params.status !== null) {
    list = list.filter((o) => o.status === Number(params.status))
  }
  list = list.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  return ok(list.map((o) => buildOrderDetail(o, state)))
}

export async function getOrderDetail(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === Number(id) && o.userId === userId)
  if (!order) return fail('订单不存在')
  return ok(buildOrderDetail(order, state))
}

export async function createOrder(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()

  const address = state.addresses.find((a) => a.id === data.addressId && a.userId === userId)
  if (!address) return fail('请选择收货地址')

  let cartItems = []
  if (data.cartItemIds?.length) {
    cartItems = state.cart.filter((c) => c.userId === userId && data.cartItemIds.includes(c.id))
  } else {
    cartItems = state.cart.filter((c) => c.userId === userId && c.selected)
  }
  if (!cartItems.length) return fail('请选择要结算的商品')

  const enriched = cartItems.map(buildCartItem).filter(Boolean)
  const merchantGroups = {}
  enriched.forEach((item) => {
    if (!merchantGroups[item.merchantId]) merchantGroups[item.merchantId] = []
    merchantGroups[item.merchantId].push(item)
  })

  const createdOrders = []
  Object.entries(merchantGroups).forEach(([merchantId, items]) => {
    const totalAmount = items.reduce((sum, i) => sum + i.price * i.quantity, 0)
    const orderId = nextAutoId()
    const orderNo = `ORD${Date.now()}${orderId}`
    const order = {
      id: orderId,
      orderNo,
      userId,
      merchantId: Number(merchantId),
      totalAmount,
      payAmount: null,
      status: 0,
      addressSnapshot: {
        name: address.name,
        phone: address.phone,
        province: address.province,
        city: address.city,
        district: address.district,
        detail: address.detail,
      },
      remark: data.remark || '',
      createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
    }
    state.orders.push(order)
    items.forEach((item) => {
      state.orderItems.push({
        id: nextAutoId(),
        orderId,
        productSkuId: item.productSkuId,
        productId: item.productId,
        productName: item.productName,
        skuName: item.skuName,
        productImage: item.productImage,
        quantity: item.quantity,
        unitPrice: item.price,
        subtotal: item.price * item.quantity,
      })
      const sku = mockSkus.find((s) => s.id === item.productSkuId)
      if (sku) sku.stock -= item.quantity
    })
    createdOrders.push(buildOrderDetail(order, state))
  })

  const settledIds = enriched.map((i) => i.id)
  state.cart = state.cart.filter((c) => !settledIds.includes(c.id))
  persist(state)
  return ok(createdOrders)
}

export async function payOrder(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === Number(id) && o.userId === userId)
  if (!order) return fail('订单不存在')
  if (order.status !== 0) return fail('订单状态不允许支付')
  order.status = 1
  order.payAmount = order.totalAmount
  order.payTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  persist(state)
  return ok(buildOrderDetail(order, state))
}

export async function cancelOrder(id, data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === Number(id) && o.userId === userId)
  if (!order) return fail('订单不存在')
  if (order.status !== 0) return fail('只能取消待付款订单')
  order.status = 5
  order.cancelTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  order.cancelReason = data?.reason || '用户取消'
  const items = state.orderItems.filter((i) => i.orderId === order.id)
  items.forEach((item) => {
    const sku = mockSkus.find((s) => s.id === item.productSkuId)
    if (sku) sku.stock += item.quantity
  })
  persist(state)
  return ok(buildOrderDetail(order, state))
}

export async function confirmReceipt(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === Number(id) && o.userId === userId)
  if (!order) return fail('订单不存在')
  if (order.status !== 2) return fail('订单状态不允许确认收货')
  order.status = 3
  order.receiveTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  persist(state)
  return ok(buildOrderDetail(order, state))
}

// ========== 评价 ==========

export async function createReview(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === data.orderId && o.userId === userId)
  if (!order) return fail('订单不存在')
  if (![3, 4].includes(order.status)) return fail('订单未完成，无法评价')

  const user = state.users.find((u) => u.id === userId)
  const review = {
    id: nextAutoId(),
    orderId: data.orderId,
    productId: data.productId,
    userId,
    nickname: data.isAnonymous ? '匿名用户' : user.nickname,
    content: data.content,
    rating: data.rating,
    images: data.images || [],
    isAnonymous: data.isAnonymous ? 1 : 0,
    createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
  }
  mockReviews.push(review)
  order.status = 4
  persist(state)
  return ok(review)
}

// ========== 退款 ==========

export async function getRefunds() {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const list = state.refunds.filter((r) => r.userId === userId)
  return ok(list.map((r) => ({ ...r, merchantName: getMerchantName(r.merchantId) })))
}

export async function getRefundDetail(id) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const refund = state.refunds.find((r) => r.id === Number(id) && r.userId === userId)
  if (!refund) return fail('退款单不存在')
  const order = state.orders.find((o) => o.id === refund.orderId)
  return ok({ ...refund, merchantName: getMerchantName(refund.merchantId), order })
}

export async function createRefund(data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const order = state.orders.find((o) => o.id === data.orderId && o.userId === userId)
  if (!order) return fail('订单不存在')
  if (![1, 2, 3].includes(order.status)) return fail('当前订单状态不可申请退款')

  const existing = state.refunds.find((r) => r.orderId === order.id && ![3, 7].includes(r.status))
  if (existing) return fail('该订单已有进行中的退款申请')

  const refund = {
    id: nextAutoId(),
    refundNo: `RFD${Date.now()}`,
    orderId: order.id,
    userId,
    merchantId: order.merchantId,
    reason: data.reason,
    description: data.description,
    amount: order.payAmount || order.totalAmount,
    status: 0,
    createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
  }
  state.refunds.push(refund)
  persist(state)
  return ok(refund)
}

export async function submitReturnLogistics(id, data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const refund = state.refunds.find((r) => r.id === Number(id) && r.userId === userId)
  if (!refund) return fail('退款单不存在')
  if (refund.status !== 1) return fail('当前状态不可填写退货物流')
  refund.status = 2
  refund.returnLogisticsCompany = data.logisticsCompany
  refund.returnLogisticsNo = data.logisticsNo
  refund.returnShipTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  persist(state)
  return ok(refund)
}

export async function appealRefund(id, data) {
  await delay()
  const userId = requireLogin()
  const state = getMockState()
  const refund = state.refunds.find((r) => r.id === Number(id) && r.userId === userId)
  if (!refund) return fail('退款单不存在')
  if (refund.status !== 4) return fail('当前状态不可申诉')
  refund.status = 5
  refund.appealReason = data.reason
  refund.appealTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  persist(state)
  return ok(refund)
}
