// Mock: 操作日志

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const actionMap: Record<string, string> = {
  MERCHANT_AUDIT: '商家审核',
  PRODUCT_AUDIT: '商品审核',
  REFUND_ARBITRATE: '退款仲裁',
  USER_DISABLE: '用户管理',
}

const logs: any[] = [
  { id: 1, adminName: '系统管理员', action: 'MERCHANT_AUDIT', targetType: 'MERCHANT', targetId: '1', detail: '{"action":"approve","remark":"审核通过"}', ipAddress: '192.168.1.100', createdAt: '2026-07-01 10:30:00' },
  { id: 2, adminName: '系统管理员', action: 'MERCHANT_AUDIT', targetType: 'MERCHANT', targetId: '2', detail: '{"action":"approve","remark":"审核通过"}', ipAddress: '192.168.1.100', createdAt: '2026-07-01 11:00:00' },
  { id: 3, adminName: '系统管理员', action: 'PRODUCT_AUDIT', targetType: 'PRODUCT', targetId: '8', detail: '{"action":"pending","remark":"待审核"}', ipAddress: '192.168.1.100', createdAt: '2026-07-03 09:30:00' },
  { id: 4, adminName: '系统管理员', action: 'PRODUCT_AUDIT', targetType: 'PRODUCT', targetId: '9', detail: '{"action":"reject","remark":"信息不完整"}', ipAddress: '192.168.1.100', createdAt: '2026-07-03 10:15:00' },
  { id: 5, adminName: '系统管理员', action: 'REFUND_ARBITRATE', targetType: 'REFUND', targetId: '3', detail: '{"action":"approve","remark":"商家尺码描述不清晰，支持用户退款"}', ipAddress: '192.168.1.100', createdAt: '2026-07-07 10:00:00' },
  { id: 6, adminName: '系统管理员', action: 'REFUND_ARBITRATE', targetType: 'REFUND', targetId: '4', detail: '{"action":"reject","remark":"商品参数与宣传一致，不支持退款"}', ipAddress: '192.168.1.100', createdAt: '2026-07-06 16:00:00' },
]

export async function getLogList(params?: any) {
  await delay()
  let list = [...logs]
  if (params?.action) list = list.filter(l => l.action === params.action)
  return { code: 200, data: { list, total: list.length } }
}
