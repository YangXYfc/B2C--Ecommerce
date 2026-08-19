import { defineStore } from 'pinia'
import { api } from '../api/index.js'
import { createUniStorage } from '../utils/storage.js'

const storage = createUniStorage('yuexuan')

export const useUserStore = defineStore('user', {
  state: () => ({ token: storage.get('token', ''), profile: storage.get('profile', null) }),
  getters: { isLoggedIn: (state) => Boolean(state.token) },
  actions: {
    async login(form) {
      const session = await api.login(form)
      this.token = session.token
      this.profile = session.user
      storage.set('token', session.token)
      storage.set('profile', session.user)
    },
    async refreshProfile() {
      if (!this.token) return
      this.profile = await api.getProfile()
      storage.set('profile', this.profile)
    },
    logout() {
      this.token = ''
      this.profile = null
      storage.remove('token')
      storage.remove('profile')
    },
  },
})
