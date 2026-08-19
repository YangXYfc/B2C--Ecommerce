/**
 * API 统一入口
 * 默认连接真实后端；仅在 VITE_USE_MOCK=true 时启用本地 Mock。
 */
import * as mockApi from './mock'
import request from './request'
import {
  normalizeCart,
  normalizeMerchantApplication,
  normalizeOrder,
  normalizeOrders,
  normalizeProduct,
  normalizeReviews,
} from './adapters'

export const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

// 认证
export const register = (data) => USE_MOCK ? mockApi.register(data) : request.post('/auth/register', data)
export const login = (data) => USE_MOCK ? mockApi.login(data) : request.post('/auth/login', data)
export const getProfile = () => USE_MOCK ? mockApi.getProfile() : request.get('/auth/profile')
export const updateProfile = (data) => USE_MOCK ? mockApi.updateProfile(data) : request.put('/users/profile', data)
export const updatePassword = (data) => USE_MOCK ? mockApi.updatePassword(data) : request.put('/users/password', data)
export const merchantApply = (data) => USE_MOCK ? mockApi.merchantApply(data) : request.post('/auth/merchant-apply', normalizeMerchantApplication(data))
export const uploadImage = (file) => {
  if (USE_MOCK) return mockApi.uploadImage(file)
  const form = new FormData()
  form.append('file', file)
  return request.post('/files/images', form)
}

// 商品
export const getBanners = () => USE_MOCK ? mockApi.getBanners() : request.get('/banners')
export const getCategories = () => USE_MOCK ? mockApi.getCategories() : request.get('/categories')
export const getProducts = (params = {}) => USE_MOCK ? mockApi.getProducts(params) : request.get('/products', { params: { ...params, sort: ({ price_asc: 'priceAsc', price_desc: 'priceDesc' })[params.sort] || params.sort } })
export const getProductDetail = async (id) => USE_MOCK ? mockApi.getProductDetail(id) : normalizeProduct(await request.get(`/products/${id}`))
export const getProductReviews = async (id) => USE_MOCK ? mockApi.getProductReviews(id) : normalizeReviews(await request.get(`/products/${id}/reviews`))

// 地址
export const getAddresses = () => USE_MOCK ? mockApi.getAddresses() : request.get('/addresses')
export const createAddress = (data) => USE_MOCK ? mockApi.createAddress(data) : request.post('/addresses', { ...data, isDefault: data.isDefault ? 1 : 0 })
export const updateAddress = (id, data) => USE_MOCK ? mockApi.updateAddress(id, data) : request.put(`/addresses/${id}`, { ...data, isDefault: data.isDefault ? 1 : 0 })
export const deleteAddress = (id) => USE_MOCK ? mockApi.deleteAddress(id) : request.delete(`/addresses/${id}`)
export const setDefaultAddress = (id) => USE_MOCK ? mockApi.setDefaultAddress(id) : request.put(`/addresses/${id}/default`)

// 购物车
export const getCart = async () => USE_MOCK ? mockApi.getCart() : normalizeCart(await request.get('/cart'))
export const addCartItem = (data) => USE_MOCK ? mockApi.addCartItem(data) : request.post('/cart/items', { skuId: data.skuId || data.productSkuId, quantity: data.quantity })
export const updateCartItem = (id, data) => USE_MOCK ? mockApi.updateCartItem(id, data) : request.put(`/cart/items/${id}`, data)
export const deleteCartItem = (id) => USE_MOCK ? mockApi.deleteCartItem(id) : request.delete(`/cart/items/${id}`)
export const deleteSelectedCart = () => USE_MOCK ? mockApi.deleteSelectedCart() : request.delete('/cart/selected')

// 订单
export const getOrders = async (params) => {
  if (USE_MOCK) return mockApi.getOrders(params)
  return normalizeOrders(await request.get('/orders', { params })).records
}
export const getOrderDetail = async (id) => USE_MOCK ? mockApi.getOrderDetail(id) : normalizeOrder(await request.get(`/orders/${id}`))
export const createOrder = async (data) => USE_MOCK ? mockApi.createOrder(data) : [normalizeOrder(await request.post('/orders', data))]
export const payOrder = (id) => USE_MOCK ? mockApi.payOrder(id) : request.post(`/orders/${id}/pay`)
export const cancelOrder = (id, data = {}) => USE_MOCK ? mockApi.cancelOrder(id, data) : request.put(`/orders/${id}/cancel`, { reason: data.reason || '用户取消订单' })
export const confirmReceipt = (id) => USE_MOCK ? mockApi.confirmReceipt(id) : request.put(`/orders/${id}/confirm-receipt`)

// 评价
export const createReview = (data) => USE_MOCK ? mockApi.createReview(data) : request.post('/reviews', { ...data, anonymous: data.anonymous ?? data.isAnonymous })

// 退款
export const getRefunds = async () => {
  if (USE_MOCK) return mockApi.getRefunds()
  const value = await request.get('/refunds')
  return value.records || value.list || []
}
export const getRefundDetail = async (id) => {
  if (USE_MOCK) return mockApi.getRefundDetail(id)
  const refund = await request.get(`/refunds/${id}`)
  const order = await getOrderDetail(refund.orderId)
  return { ...refund, order, merchantName: order.merchantName }
}
export const createRefund = (data) => USE_MOCK ? mockApi.createRefund(data) : request.post('/refunds', data)
export const submitReturnLogistics = (id, data) => USE_MOCK ? mockApi.submitReturnLogistics(id, data) : request.put(`/refunds/${id}/return-logistics`, data)
export const appealRefund = (id, data) => USE_MOCK ? mockApi.appealRefund(id, data) : request.put(`/refunds/${id}/appeal`, data)
