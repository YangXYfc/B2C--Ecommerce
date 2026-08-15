import { get, put } from '@/api/request'

export async function getShop() {
  return get('/merchant/shop')
}

export async function updateShop(data: any) {
  return put('/merchant/shop', data)
}