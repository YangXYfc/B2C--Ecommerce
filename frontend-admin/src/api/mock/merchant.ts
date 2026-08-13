// Mock: 商家审核

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const merchants: any[] = [
  { id: 1, userId: 2, userName: 'merchant1', shopName: '数码旗舰店', shopLogo: 'https://img.jd-demo.com/shop/logo1.png', description: '主营手机、电脑、数码配件，正品保障', contactPhone: '13800000002', status: 1, auditStatus: 1, auditRemark: '审核通过', createdAt: '2026-07-01 10:00:00' },
  { id: 2, userId: 3, userName: 'merchant2', shopName: '服饰优选店', shopLogo: 'https://img.jd-demo.com/shop/logo2.png', description: '潮流服饰，品质生活', contactPhone: '13800000003', status: 1, auditStatus: 1, auditRemark: '审核通过', createdAt: '2026-07-01 11:00:00' },
  { id: 3, userId: 7, userName: 'merchant3', shopName: '待审核商家', shopLogo: 'https://img.jd-demo.com/shop/logo3.png', description: '新入驻商家，等待审核', contactPhone: '13800000007', status: 0, auditStatus: 0, auditRemark: null, createdAt: '2026-07-05 09:00:00' },
]

export async function getMerchantList(params?: any) {
  await delay()
  let list = [...merchants]
  if (params?.shopName) list = list.filter(m => m.shopName.includes(params.shopName))
  if (params?.auditStatus !== undefined && params?.auditStatus !== '') list = list.filter(m => m.auditStatus === Number(params.auditStatus))
  return { code: 200, data: { list, total: list.length } }
}

export async function getMerchantDetail(id: number) {
  await delay()
  return { code: 200, data: merchants.find(m => m.id === id) }
}

export async function auditMerchant(id: number, data: { action: string; remark: string }) {
  await delay()
  const m = merchants.find(item => item.id === id)
  if (m) { m.auditStatus = data.action === 'approve' ? 1 : 2; m.auditRemark = data.remark }
  return { code: 200, message: data.action === 'approve' ? '审核通过' : '已驳回' }
}
