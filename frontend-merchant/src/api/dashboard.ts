import { get } from '@/api/request'

export function getDashboard() {
  return get('/merchant/dashboard')
}
