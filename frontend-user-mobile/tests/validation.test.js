import { describe, expect, it } from 'vitest'
import { validateAddress, validateLogin, validateRegister } from '../src/utils/validation.js'

describe('mobile form validation', () => {
  it('requires login credentials', () => {
    expect(validateLogin({ username: '', password: '' })).toBe('请输入账号')
  })

  it('requires matching registration passwords', () => {
    expect(validateRegister({ username: 'demo', password: '123456', confirmPassword: '654321', phone: '13800000000' })).toBe('两次输入的密码不一致')
  })

  it('requires a complete address', () => {
    expect(validateAddress({ name: '张三', phone: '13800000004', province: '', city: '', district: '', detail: '' })).toBe('请选择所在地区')
  })
})
