import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getProfile } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('jd_token') || '')
  const profile = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  async function fetchProfile() {
    if (!token.value) return null
    try {
      profile.value = await getProfile()
      return profile.value
    } catch {
      logout()
      return null
    }
  }

  function setAuth(data) {
    token.value = data.token
    profile.value = data.user
    localStorage.setItem('jd_token', data.token)
  }

  function logout() {
    token.value = ''
    profile.value = null
    localStorage.removeItem('jd_token')
  }

  return { token, profile, isLoggedIn, fetchProfile, setAuth, logout }
})
