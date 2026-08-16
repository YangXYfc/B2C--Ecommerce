import { get } from '@/api/request'

export async function getLogList(params?: any) {
  const res: any = await get('/admin/logs', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.records ?? d.list ?? [] } }
}