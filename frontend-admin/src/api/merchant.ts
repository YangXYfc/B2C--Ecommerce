import { get, put } from '@/api/request'

export async function getMerchantList() {
  const res: any = await get('/admin/merchants/pending')
  const arr: any[] = res.data ?? []
  return { ...res, data: { list: arr, total: arr.length } }
}

export async function getMerchantDetail(id: number) {
  return get(`/admin/merchants/${id}`)
}

export async function auditMerchant(id: number, data: { approve: boolean; remark?: string }) {
  return put(`/admin/merchants/${id}/audit`, data)
}