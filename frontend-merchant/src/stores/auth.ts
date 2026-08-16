import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/api/request'

interface MerchantInfo {
  id: number
  shopName: string
  shopLogo: string
  auditStatus: number
}

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  role: string
  phone?: string
  email?: string
  merchant?: MerchantInfo | null
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const merchantId = ref<number | null>(Number(localStorage.getItem('merchantId')) || null)

  function setToken(val: string) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setMerchantId(val: number | null) {
    merchantId.value = val
    if (val) localStorage.setItem('merchantId', String(val))
    else localStorage.removeItem('merchantId')
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    merchantId.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('merchantId')
  }

  async function login(username: string, password: string) {
    const res: any = await post('/auth/login', { username, password })
    setToken(res.data.token)
    await fetchProfile()
    return res
  }

  async function fetchProfile() {
    const res: any = await get('/auth/profile')
    userInfo.value = res.data
    if (res.data.role === 'MERCHANT' && res.data.merchant) {
      setMerchantId(res.data.merchant.id)
    }
  }

  async function logout() {
    clearAuth()
  }

  return {
    token,
    userInfo,
    merchantId,
    setToken,
    setMerchantId,
    clearAuth,
    login,
    fetchProfile,
    logout,
  }
})