/**
 * API 统一入口
 * USE_MOCK=true 时使用临时 Mock 函数（后端未完成时）
 * 后端联调时将 USE_MOCK 改为 false，函数内部改为 request 调用
 */
import * as mockApi from './mock'
import request from './request'

export const USE_MOCK = true

// 认证
export const register = (data) => USE_MOCK ? mockApi.register(data) : request.post('/auth/register', data)
export const login = (data) => USE_MOCK ? mockApi.login(data) : request.post('/auth/login', data)
export const getProfile = () => USE_MOCK ? mockApi.getProfile() : request.get('/auth/profile')
export const updateProfile = (data) => USE_MOCK ? mockApi.updateProfile(data) : request.put('/users/profile', data)
export const updatePassword = (data) => USE_MOCK ? mockApi.updatePassword(data) : request.put('/users/password', data)
export const merchantApply = (data) => USE_MOCK ? mockApi.merchantApply(data) : request.post('/auth/merchant-apply', data)
export const uploadImage = (file) => USE_MOCK ? mockApi.uploadImage(file) : request.post('/files/images', file)

// 商品
export const getBanners = () => USE_MOCK ? mockApi.getBanners() : request.get('/banners')
export const getCategories = () => USE_MOCK ? mockApi.getCategories() : request.get('/categories')
export const getProducts = (params) => USE_MOCK ? mockApi.getProducts(params) : request.get('/products', { params })
export const getProductDetail = (id) => USE_MOCK ? mockApi.getProductDetail(id) : request.get(`/products/${id}`)
export const getProductReviews = (id) => USE_MOCK ? mockApi.getProductReviews(id) : request.get(`/products/${id}/reviews`)

// 地址
export const getAddresses = () => USE_MOCK ? mockApi.getAddresses() : request.get('/addresses')
export const createAddress = (data) => USE_MOCK ? mockApi.createAddress(data) : request.post('/addresses', data)
export const updateAddress = (id, data) => USE_MOCK ? mockApi.updateAddress(id, data) : request.put(`/addresses/${id}`, data)
export const deleteAddress = (id) => USE_MOCK ? mockApi.deleteAddress(id) : request.delete(`/addresses/${id}`)
export const setDefaultAddress = (id) => USE_MOCK ? mockApi.setDefaultAddress(id) : request.put(`/addresses/${id}/default`)

// 购物车
export const getCart = () => USE_MOCK ? mockApi.getCart() : request.get('/cart')
export const addCartItem = (data) => USE_MOCK ? mockApi.addCartItem(data) : request.post('/cart/items', data)
export const updateCartItem = (id, data) => USE_MOCK ? mockApi.updateCartItem(id, data) : request.put(`/cart/items/${id}`, data)
export const deleteCartItem = (id) => USE_MOCK ? mockApi.deleteCartItem(id) : request.delete(`/cart/items/${id}`)
export const deleteSelectedCart = () => USE_MOCK ? mockApi.deleteSelectedCart() : request.delete('/cart/selected')

// 订单
export const getOrders = (params) => USE_MOCK ? mockApi.getOrders(params) : request.get('/orders', { params })
export const getOrderDetail = (id) => USE_MOCK ? mockApi.getOrderDetail(id) : request.get(`/orders/${id}`)
export const createOrder = (data) => USE_MOCK ? mockApi.createOrder(data) : request.post('/orders', data)
export const payOrder = (id) => USE_MOCK ? mockApi.payOrder(id) : request.post(`/orders/${id}/pay`)
export const cancelOrder = (id, data) => USE_MOCK ? mockApi.cancelOrder(id, data) : request.put(`/orders/${id}/cancel`, data)
export const confirmReceipt = (id) => USE_MOCK ? mockApi.confirmReceipt(id) : request.put(`/orders/${id}/confirm-receipt`)

// 评价
export const createReview = (data) => USE_MOCK ? mockApi.createReview(data) : request.post('/reviews', data)

// 退款
export const getRefunds = () => USE_MOCK ? mockApi.getRefunds() : request.get('/refunds')
export const getRefundDetail = (id) => USE_MOCK ? mockApi.getRefundDetail(id) : request.get(`/refunds/${id}`)
export const createRefund = (data) => USE_MOCK ? mockApi.createRefund(data) : request.post('/refunds', data)
export const submitReturnLogistics = (id, data) => USE_MOCK ? mockApi.submitReturnLogistics(id, data) : request.put(`/refunds/${id}/return-logistics`, data)
export const appealRefund = (id, data) => USE_MOCK ? mockApi.appealRefund(id, data) : request.put(`/refunds/${id}/appeal`, data)
