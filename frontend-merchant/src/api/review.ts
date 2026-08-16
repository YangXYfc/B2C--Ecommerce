import { get, put } from '@/api/request'

export async function getReviewList(params?: any) {
  const res: any = await get('/merchant/reviews', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.records ?? d.list ?? [] } }
}

export async function replyReview(id: number, reply: string) {
  return put(`/merchant/reviews/${id}/reply`, { reply })
}