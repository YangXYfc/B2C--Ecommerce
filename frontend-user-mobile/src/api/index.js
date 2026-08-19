import {
  normalizeCart,
  normalizeMerchantApplication,
  normalizeOrder,
  normalizePage,
  normalizeProduct,
  normalizeProductQuery,
  normalizeReturnLogistics,
  unwrapResult,
} from './normalizers.js'
import { request } from './request.js'
import { createMockService } from './mock/service.js'
import { createUniStorage } from '../utils/storage.js'

const mock = createMockService(createUniStorage('yuexuan'))
const useMock = import.meta.env.VITE_DATA_MODE === 'mock'

const real = {
  login: (data) => request({ url: '/api/auth/login', method: 'POST', data }),
  register: (data) => request({ url: '/api/auth/register', method: 'POST', data }),
  getBanners: () => request({ url: '/api/banners' }),
  getCategories: () => request({ url: '/api/categories' }),
  getProducts: async (query) => normalizePage(await request({ url: '/api/products', data: normalizeProductQuery(query) })),
  getProduct: async (id) => {
    const [product, reviewPage] = await Promise.all([
      request({ url: `/api/products/${id}` }),
      request({ url: `/api/products/${id}/reviews` }),
    ])
    return {
      ...normalizeProduct(product),
      reviews: (reviewPage.records || []).map((review) => ({ ...review, nickname: review.nickname || `用户${review.userId}` })),
    }
  },
  getCart: async () => normalizeCart(await request({ url: '/api/cart' })),
  addCartItem: (data) => request({ url: '/api/cart/items', method: 'POST', data: { skuId: data.skuId || data.productSkuId, quantity: data.quantity } }),
  updateCartItem: (id, data) => request({ url: `/api/cart/items/${id}`, method: 'PUT', data }),
  removeCartItem: (id) => request({ url: `/api/cart/items/${id}`, method: 'DELETE' }),
  clearSelectedCart: () => request({ url: '/api/cart/selected', method: 'DELETE' }),
  getAddresses: () => request({ url: '/api/addresses' }),
  saveAddress: (data) => request({ url: data.id ? `/api/addresses/${data.id}` : '/api/addresses', method: data.id ? 'PUT' : 'POST', data }),
  deleteAddress: (id) => request({ url: `/api/addresses/${id}`, method: 'DELETE' }),
  setDefaultAddress: (id) => request({ url: `/api/addresses/${id}/default`, method: 'PUT' }),
  createOrder: async (data) => {
    let cartItemIds = data.cartItemIds || []
    if (!cartItemIds.length && data.skuId) {
      const cart = normalizeCart(await request({
        url: '/api/cart/items', method: 'POST', data: { skuId: data.skuId, quantity: data.quantity || 1 },
      }))
      const line = cart.find((item) => Number(item.skuId) === Number(data.skuId))
      if (!line) throw new Error('无法创建立即购买订单')
      cartItemIds = [line.id]
    }
    return normalizeOrder(await request({
      url: '/api/orders', method: 'POST', data: { addressId: data.addressId, cartItemIds, remark: data.remark },
    }))
  },
  getOrders: async (query) => {
    const page = normalizePage(await request({ url: '/api/orders', data: query }))
    return { ...page, records: page.records.map(normalizeOrder) }
  },
  getOrder: async (id) => normalizeOrder(await request({ url: `/api/orders/${id}` })),
  payOrder: (id) => request({ url: `/api/orders/${id}/pay`, method: 'POST' }),
  cancelOrder: (id, data = {}) => request({ url: `/api/orders/${id}/cancel`, method: 'PUT', data: { reason: data.reason || '用户取消订单' } }),
  confirmOrder: (id) => request({ url: `/api/orders/${id}/confirm-receipt`, method: 'PUT' }),
  createReview: (data) => request({ url: '/api/reviews', method: 'POST', data }),
  getRefunds: async (query) => normalizePage(await request({ url: '/api/refunds', data: query })),
  getRefund: (id) => request({ url: `/api/refunds/${id}` }),
  createRefund: (data) => request({ url: '/api/refunds', method: 'POST', data }),
  submitReturnLogistics: (id, data) => request({ url: `/api/refunds/${id}/return-logistics`, method: 'PUT', data: normalizeReturnLogistics(data) }),
  appealRefund: (id, data) => request({ url: `/api/refunds/${id}/appeal`, method: 'PUT', data }),
  getProfile: () => request({ url: '/api/auth/profile' }),
  updateProfile: (data) => request({ url: '/api/users/profile', method: 'PUT', data }),
  changePassword: (data) => request({ url: '/api/users/password', method: 'PUT', data }),
  applyMerchant: (data) => request({ url: '/api/auth/merchant-apply', method: 'POST', data: normalizeMerchantApplication(data) }),
  uploadImage: (path) => new Promise((resolve, reject) => {
    const token = createUniStorage('yuexuan').get('token', '')
    const profile = createUniStorage('yuexuan').get('profile', null)
    uni.uploadFile({
      url: `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/files/images`,
      filePath: path,
      name: 'file',
      header: { Authorization: `Bearer ${token}`, 'X-User-Id': String(profile?.id || '') },
      success: (res) => { try { resolve(unwrapResult(JSON.parse(res.data))) } catch (error) { reject(error) } },
      fail: reject,
    })
  }),
}

export const api = useMock ? mock : real
export const dataMode = useMock ? 'mock' : 'api'
