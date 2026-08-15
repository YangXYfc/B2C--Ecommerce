import { get, post, put, del } from '@/api/request'

export async function getBannerList() {
  const res: any = await get('/admin/banners')
  const arr: any[] = res.data ?? []
  return { ...res, data: { list: arr, total: arr.length } }
}

export async function createBanner(data: any) {
  return post('/admin/banners', data)
}

export async function updateBanner(id: number, data: any) {
  return put(`/admin/banners/${id}`, data)
}

export async function deleteBanner(id: number) {
  return del(`/admin/banners/${id}`)
}