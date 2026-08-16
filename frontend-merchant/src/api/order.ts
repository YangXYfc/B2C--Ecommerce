import { get, put } from '@/api/request'

export const orderStatusMap: Record<number, string> = {
  0: '待付款', 1: '待发货', 2: '已发货', 3: '已收货', 4: '已评价', 5: '已取消',
}

export async function getOrderList(params?: any) {
  const res: any = await get('/merchant/orders', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.records ?? d.list ?? [] } }
}

export async function getOrderDetail(id: number) {
  return get(`/merchant/orders/${id}`)
}

export async function shipOrder(id: number, data: { logisticsCompany: string; logisticsNo: string }) {
  return put(`/merchant/orders/${id}/ship`, data)
}