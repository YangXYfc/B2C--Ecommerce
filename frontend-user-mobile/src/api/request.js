import { unwrapResult } from './normalizers.js'
import { createUniStorage } from '../utils/storage.js'

const storage = createUniStorage('yuexuan')
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export function request({ url, method = 'GET', data }) {
  return new Promise((resolve, reject) => {
    const token = storage.get('token', '')
    const profile = storage.get('profile', null)
    const header = {}
    if (token) header.Authorization = `Bearer ${token}`
    if (profile?.id) header['X-User-Id'] = String(profile.id)
    uni.request({
      url: `${baseURL}${url}`,
      method,
      data,
      header,
      success(response) {
        if (response.statusCode === 401) {
          storage.remove('token')
          uni.reLaunch({ url: '/pages/auth/login' })
          reject(new Error('登录已过期'))
          return
        }
        try { resolve(unwrapResult(response.data)) } catch (error) { reject(error) }
      },
      fail() { reject(new Error('网络连接失败，请稍后重试')) },
    })
  })
}
