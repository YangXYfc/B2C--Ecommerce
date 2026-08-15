import { get, put } from '@/api/request'

export async function getProductList() {
  const res: any = await get('/admin/products/pending')
  const arr: any[] = res.data ?? []
  return { ...res, data: { list: arr, total: arr.length } }
}

export async function getProductDetail(id: number) {
  return get(`/admin/products/${id}`)
}

export async function auditProduct(id: number, data: { approve: boolean; remark?: string }) {
  return put(`/admin/products/${id}/audit`, data)
}