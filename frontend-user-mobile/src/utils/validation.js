const PHONE = /^1\d{10}$/

export function validateLogin(form) {
  if (!form.username?.trim()) return '请输入账号'
  if (!form.password) return '请输入密码'
  return ''
}

export function validateRegister(form) {
  if (!form.username?.trim()) return '请输入账号'
  if (!PHONE.test(form.phone || '')) return '请输入正确的手机号'
  if ((form.password || '').length < 6) return '密码至少需要6位'
  if (form.password !== form.confirmPassword) return '两次输入的密码不一致'
  return ''
}

export function validateAddress(form) {
  if (!form.name?.trim()) return '请输入收货人'
  if (!PHONE.test(form.phone || '')) return '请输入正确的手机号'
  if (!form.province || !form.city || !form.district) return '请选择所在地区'
  if (!form.detail?.trim()) return '请输入详细地址'
  return ''
}
