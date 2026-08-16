import { get, put } from '@/api/request'

export const refundStatusMap: Record<number, string> = {
  0: '待审核', 1: '已通过', 2: '寄回中', 3: '退款完成', 4: '已拒绝', 5: '申诉中', 6: '平台支持', 7: '平台驳回',
}

export async function getRefundList(params?: any) {
  const res: any = await get('/merchant/refunds', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.records ?? d.list ?? [] } }
}

export async function auditRefund(id: number, data: { approved: boolean; remark?: string }) {
  return put(`/merchant/refunds/${id}/audit`, data)
}

export async function confirmReturn(id: number) {
  return put(`/merchant/refunds/${id}/confirm-return`)
}