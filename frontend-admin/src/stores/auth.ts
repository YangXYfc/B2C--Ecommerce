import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/api/request'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  role: string
  phone?: string
  email?: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const adminId = ref<number | null>(Number(localStorage.getItem('adminId')) || null)

  function setToken(val: string) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setAdminId(val: number | null) {
    adminId.value = val
    if (val) localStorage.setItem('adminId', String(val))
    else localStorage.removeItem('adminId')
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    adminId.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('adminId')
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
    if (res.data.role === 'ADMIN') {
      setAdminId(res.data.id)
    }
  }

  async function logout() {
    clearAuth()
  }

  return {
    token,
    userInfo,
    adminId,
    setToken,
    setAdminId,
    clearAuth,
    login,
    fetchProfile,
    logout,
  }
})