import { get, put } from '@/api/request'

export async function getUserList(params?: any) {
  const res: any = await get('/admin/users', { page: 1, size: 100, ...params })
  const d = res.data
  return { ...res, data: { ...d, list: d.list ?? d.records ?? [] } }
}

export async function toggleUserStatus(id: number, status: number) {
  return put(`/admin/users/${id}/status`, { status })
}