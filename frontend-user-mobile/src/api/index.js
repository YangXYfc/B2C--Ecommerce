import { normalizePage } from './normalizers.js'
import { request } from './request.js'
import { createMockService } from './mock/service.js'
import { createUniStorage } from '../utils/storage.js'

const mock = createMockService(createUniStorage('yuexuan'))
const useMock = (import.meta.env.VITE_DATA_MODE || 'mock') !== 'api'

const real = {
  login: (data) => request({ url: '/api/auth/login', method: 'POST', data }),
  register: (data) => request({ url: '/api/auth/register', method: 'POST', data }),
  getBanners: () => request({ url: '/api/banners' }),
  getCategories: () => request({ url: '/api/categories' }),
  getProducts: async (query) => normalizePage(await request({ url: '/api/products', data: query })),
  getProduct: (id) => request({ url: `/api/products/${id}` }),
  getCart: () => request({ url: '/api/cart' }),
  addCartItem: (data) => request({ url: '/api/cart/items', method: 'POST', data }),
  updateCartItem: (id, data) => request({ url: `/api/cart/items/${id}`, method: 'PUT', data }),
  removeCartItem: (id) => request({ url: `/api/cart/items/${id}`, method: 'DELETE' }),
  clearSelectedCart: () => request({ url: '/api/cart/selected', method: 'DELETE' }),
  getAddresses: () => request({ url: '/api/addresses' }),
  saveAddress: (data) => request({ url: data.id ? `/api/addresses/${data.id}` : '/api/addresses', method: data.id ? 'PUT' : 'POST', data }),
  deleteAddress: (id) => request({ url: `/api/addresses/${id}`, method: 'DELETE' }),
  setDefaultAddress: (id) => request({ url: `/api/addresses/${id}/default`, method: 'PUT' }),
  createOrder: (data) => request({ url: '/api/orders', method: 'POST', data }),
  getOrders: async (query) => normalizePage(await request({ url: '/api/orders', data: query })),
  getOrder: (id) => request({ url: `/api/orders/${id}` }),
  payOrder: (id) => request({ url: `/api/orders/${id}/pay`, method: 'POST' }),
  cancelOrder: (id, data = {}) => request({ url: `/api/orders/${id}/cancel`, method: 'PUT', data }),
  confirmOrder: (id) => request({ url: `/api/orders/${id}/confirm-receipt`, method: 'PUT' }),
  createReview: (data) => request({ url: '/api/reviews', method: 'POST', data }),
  getRefunds: async (query) => normalizePage(await request({ url: '/api/refunds', data: query })),
  getRefund: (id) => request({ url: `/api/refunds/${id}` }),
  createRefund: (data) => request({ url: '/api/refunds', method: 'POST', data }),
  submitReturnLogistics: (id, data) => request({ url: `/api/refunds/${id}/return-logistics`, method: 'PUT', data }),
  appealRefund: (id, data) => request({ url: `/api/refunds/${id}/appeal`, method: 'PUT', data }),
  getProfile: () => request({ url: '/api/auth/profile' }),
  updateProfile: (data) => request({ url: '/api/users/profile', method: 'PUT', data }),
  changePassword: (data) => request({ url: '/api/users/password', method: 'PUT', data }),
  applyMerchant: (data) => request({ url: '/api/merchant/apply', method: 'POST', data }),
  uploadImage: (path) => new Promise((resolve, reject) => uni.uploadFile({ url: `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/files/images`, filePath: path, name: 'file', success: (res) => { try { resolve(JSON.parse(res.data).data) } catch (error) { reject(error) } }, fail: reject })),
}

export const api = useMock ? mock : real
export const dataMode = useMock ? 'mock' : 'api'
