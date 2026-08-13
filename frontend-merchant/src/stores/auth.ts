import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/api/request'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  function setToken(val: string) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
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
  }

  async function logout() {
    clearAuth()
  }

  return {
    token,
    userInfo,
    setToken,
    clearAuth,
    login,
    fetchProfile,
    logout,
  }
})
