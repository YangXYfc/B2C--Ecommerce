import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器 - 添加 token 和管理员身份头
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    const adminId = localStorage.getItem('adminId')
    if (adminId) {
      config.headers['X-Admin-Id'] = adminId
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 - 统一处理错误
instance.interceptors.response.use(
  (response: AxiosResponse) => {
    const data = response.data
    // D 模块 Result: { code: 200 }；E 模块 ApiResponse: { code: "SUCCESS" }
    if (data.code === 0 || data.code === 200 || data.code === 'SUCCESS') {
      return data
    }
    if (data.code) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (error) => {
    if (error.response) {
      const { status, config, data } = error.response
      const serverMessage = data?.message
      switch (status) {
        case 401:
          if (config?.url?.includes('/auth/login')) {
            ElMessage.error(serverMessage || '用户名或密码错误')
          } else {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            localStorage.removeItem('adminId')
            window.location.href = '/login'
          }
          break
        case 403:
          ElMessage.error(serverMessage || '没有权限执行此操作')
          break
        case 404:
          ElMessage.error(serverMessage || '请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误，请稍后重试')
          break
        default:
          ElMessage.error(serverMessage || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return instance.get(url, { params, ...config }) as Promise<T>
}

export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return instance.post(url, data, config) as Promise<T>
}

export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return instance.put(url, data, config) as Promise<T>
}

export function del<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return instance.delete(url, config) as Promise<T>
}

export default instance