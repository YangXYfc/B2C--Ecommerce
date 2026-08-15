import { get, post, put } from '@/api/request'

export async function getProductList(params?: any) {
  const res: any = await get('/merchant/products', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.list ?? d.records ?? [] } }
}

export async function getProductDetail(id: number) {
  return get(`/merchant/products/${id}`)
}

export async function createProduct(data: any) {
  return post('/merchant/products', data)
}

export async function updateProduct(id: number, data: any) {
  return put(`/merchant/products/${id}`, data)
}

export async function offShelfProduct(id: number) {
  return put(`/merchant/products/${id}/off-shelf`)
}

export async function updateStock(skuId: number, stock: number) {
  return put(`/merchant/skus/${skuId}/stock`, { stock })
}