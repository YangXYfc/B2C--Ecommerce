// Mock: 退款处理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export const refundStatusMap: Record<number, string> = {
  0: '待审核', 1: '已通过', 2: '寄回中', 3: '退款完成', 4: '已拒绝', 5: '申诉中', 6: '平台支持', 7: '平台驳回',
}

const refunds: any[] = [
  { id: 1, refundNo: 'RFD20260706000001', orderNo: 'ORD20260707000001', userName: '张三', amount: 4999.00, reason: '商品质量问题', description: '手机屏幕有坏点，要求退款', status: 3, merchantRemark: '同意退款，请寄回商品', returnLogisticsCompany: '顺丰速运', returnLogisticsNo: 'SF1112223334', createdAt: '2026-07-06 08:00:00', merchantAuditTime: '2026-07-06 10:00' },
  { id: 2, refundNo: 'RFD20260707000002', orderNo: 'ORD20260707000002', userName: '张三', amount: 129.00, reason: '不想要了', description: '买重复了，申请退款', status: 0, createdAt: '2026-07-07 09:00:00' },
  { id: 3, refundNo: 'RFD20260707000003', orderNo: 'ORD20260707000003', userName: '李四', amount: 159.00, reason: '尺码不符', description: '收到的M码实际偏小，与描述不符', status: 6, merchantRemark: '尺码符合标准，拒绝退款', appealReason: '商家尺码表不清晰', adminRemark: '商家尺码描述不清晰，支持用户退款', createdAt: '2026-07-06 15:00:00' },
  { id: 4, refundNo: 'RFD20260707000004', orderNo: 'ORD20260707000006', userName: '李四', amount: 4299.00, reason: '性能不达标', description: '笔记本续航远低于宣传', status: 7, merchantRemark: '产品符合宣传参数，拒绝退款', appealReason: '商品已拆封使用且参数符合描述', adminRemark: '经核实商品参数与宣传一致，不支持退款', createdAt: '2026-07-05 14:00:00' },
]

export async function getRefundList(params?: any) {
  await delay()
  let list = [...refunds]
  if (params?.refundNo) list = list.filter(r => r.refundNo.includes(params.refundNo))
  if (params?.status !== undefined && params?.status !== '') list = list.filter(r => r.status === Number(params.status))
  return { code: 200, data: { list, total: list.length } }
}

export async function auditRefund(id: number, data: { action: string; remark: string }) {
  await delay()
  const r = refunds.find(item => item.id === id)
  if (r) {
    r.status = data.action === 'approve' ? 1 : 4
    r.merchantRemark = data.remark
    r.merchantAuditTime = new Date().toISOString()
  }
  return { code: 200, message: data.action === 'approve' ? '已同意退款' : '已拒绝退款' }
}

export async function confirmReturn(id: number) {
  await delay()
  const r = refunds.find(item => item.id === id)
  if (r) r.status = 3
  return { code: 200, message: '已确认收货，退款完成' }
}
