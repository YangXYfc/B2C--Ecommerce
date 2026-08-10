// Mock: 退款仲裁

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const refunds: any[] = [
  { id: 3, refundNo: 'RFD20260707000003', orderNo: 'ORD20260707000003', userName: '李四', merchantName: '服饰优选店', amount: 159.00, reason: '尺码不符', description: '收到的M码实际偏小，与描述不符', status: 5, appealReason: '商家尺码表不清晰，支持用户退款', merchantAuditTime: '2026-07-06 18:00', merchantRemark: '尺码符合标准，拒绝退款', createdAt: '2026-07-06 15:00:00', appealTime: '2026-07-07 08:00' },
  { id: 4, refundNo: 'RFD20260707000004', orderNo: 'ORD20260707000006', userName: '李四', merchantName: '数码旗舰店', amount: 4299.00, reason: '性能不达标', description: '笔记本续航远低于宣传', status: 7, appealReason: '商品已拆封使用且参数符合描述', merchantAuditTime: '2026-07-05 14:00', merchantRemark: '产品符合宣传参数，拒绝退款', adminRemark: '经核实商品参数与宣传一致，不支持退款', createdAt: '2026-07-05 14:00:00', appealTime: '2026-07-06 09:00' },
  { id: 1, refundNo: 'RFD20260706000001', orderNo: 'ORD20260707000001', userName: '张三', merchantName: '数码旗舰店', amount: 4999.00, reason: '商品质量问题', status: 3, merchantAuditTime: '2026-07-06 10:00', merchantRemark: '同意退款', createdAt: '2026-07-06 08:00:00' },
  { id: 2, refundNo: 'RFD20260707000002', orderNo: 'ORD20260707000002', userName: '张三', merchantName: '数码旗舰店', amount: 129.00, reason: '不想要了', status: 0, createdAt: '2026-07-07 09:00:00' },
]

export async function getRefundList(params?: any) {
  await delay()
  let list = [...refunds]
  if (params?.refundNo) list = list.filter(r => r.refundNo.includes(params.refundNo))
  if (params?.status !== undefined && params?.status !== '') list = list.filter(r => r.status === Number(params.status))
  return { code: 200, data: { list, total: list.length } }
}

export async function getRefundDetail(id: number) {
  await delay()
  return { code: 200, data: refunds.find(r => r.id === id) }
}

export async function arbitrateRefund(id: number, data: { action: string; remark: string }) {
  await delay()
  const r = refunds.find(item => item.id === id)
  if (r) { r.status = data.action === 'approve' ? 6 : 7; r.adminRemark = data.remark }
  return { code: 200, message: data.action === 'approve' ? '已支持用户退款' : '已驳回申诉' }
}
