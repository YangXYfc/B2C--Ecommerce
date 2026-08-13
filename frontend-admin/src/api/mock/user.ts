// Mock: 用户管理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const users: any[] = [
  { id: 1, username: 'admin', nickname: '系统管理员', phone: '13800000001', email: 'admin@jd-demo.com', role: 'ADMIN', status: 1, createdAt: '2026-07-01 00:00:00' },
  { id: 2, username: 'merchant1', nickname: '数码旗舰店', phone: '13800000002', email: 'merchant1@jd-demo.com', role: 'MERCHANT', status: 1, createdAt: '2026-07-01 01:00:00' },
  { id: 3, username: 'merchant2', nickname: '服饰优选店', phone: '13800000003', email: 'merchant2@jd-demo.com', role: 'MERCHANT', status: 1, createdAt: '2026-07-01 02:00:00' },
  { id: 4, username: 'user1', nickname: '张三', phone: '13800000004', email: 'user1@jd-demo.com', role: 'USER', status: 1, createdAt: '2026-07-01 03:00:00' },
  { id: 5, username: 'user2', nickname: '李四', phone: '13800000005', email: 'user2@jd-demo.com', role: 'USER', status: 1, createdAt: '2026-07-01 04:00:00' },
  { id: 6, username: 'user3', nickname: '王五', phone: '13800000006', email: 'user3@jd-demo.com', role: 'USER', status: 1, createdAt: '2026-07-01 05:00:00' },
  { id: 7, username: 'merchant3', nickname: '待审核商家', phone: '13800000007', email: 'merchant3@jd-demo.com', role: 'MERCHANT', status: 1, createdAt: '2026-07-01 06:00:00' },
]

export async function getUserList(params?: any) {
  await delay()
  let list = [...users]
  if (params?.username) list = list.filter(u => u.username.includes(params.username))
  if (params?.status !== undefined && params?.status !== '') list = list.filter(u => u.status === Number(params.status))
  return { code: 200, data: { list, total: list.length } }
}

export async function toggleUserStatus(id: number, status: number) {
  await delay()
  const u = users.find(item => item.id === id)
  if (u) u.status = status
  return { code: 200, message: status === 1 ? '已启用' : '已禁用' }
}
