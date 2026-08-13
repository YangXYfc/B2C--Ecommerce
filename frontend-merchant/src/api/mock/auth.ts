// Mock: 认证相关 API
// 后端完成后替换为: import { post, get } from '@/api/request'

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export async function loginApi(data: { username: string; password: string }) {
  await delay()
  return {
    code: 200,
    message: '登录成功',
    data: { token: 'mock-token-merchant', role: 'MERCHANT' },
  }
}

export async function getProfile() {
  await delay()
  return {
    code: 200,
    data: {
      id: 2,
      username: 'merchant1',
      nickname: '数码旗舰店',
      avatar: 'https://img.jd-demo.com/shop/logo1.png',
      role: 'MERCHANT',
    },
  }
}
