import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  const userId = userStore.profile?.id || localStorage.getItem('jd_user_id')
  if (userId) config.headers['X-User-Id'] = String(userId)
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (Number(res.code) !== 200 && res.code !== 'SUCCESS') {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request
