// Mock: 管理员认证

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export async function loginApi(_data: { username: string; password: string }) {
  await delay()
  return {
    code: 200,
    message: '登录成功',
    data: { token: 'mock-token-admin', role: 'ADMIN' },
  }
}

export async function getProfile() {
  await delay()
  return {
    code: 200,
    data: {
      id: 1,
      username: 'admin',
      nickname: '系统管理员',
      avatar: '',
      role: 'ADMIN',
    },
  }
}
