// Mock: 商品审核

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const products: any[] = [
  { id: 1, name: '智选 Pro 5G 手机 12GB+256GB 钛空灰', mainImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&q=80', merchantId: 1, merchantName: '数码旗舰店', categoryName: '智能手机', price: 4999.00, status: 1, auditRemark: '审核通过', createdAt: '2026-07-01 10:00:00' },
  { id: 2, name: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', mainImage: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=100&q=80', merchantId: 1, merchantName: '数码旗舰店', categoryName: '智能手机', price: 1599.00, status: 1, auditRemark: '审核通过', createdAt: '2026-07-01 09:00:00' },
  { id: 3, name: '轻薄本 Air 14 锐龙版 16GB+512GB', mainImage: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=100&q=80', merchantId: 1, merchantName: '数码旗舰店', categoryName: '笔记本', price: 4299.00, status: 1, auditRemark: '审核通过', createdAt: '2026-07-01 08:00:00' },
  { id: 5, name: '纯棉短袖T恤 男款 100%新疆棉', mainImage: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=100&q=80', merchantId: 2, merchantName: '服饰优选店', categoryName: 'T恤', price: 59.00, status: 1, auditRemark: '审核通过', createdAt: '2026-07-02 10:00:00' },
  { id: 6, name: '法式碎花连衣裙 夏季新款', mainImage: 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=100&q=80', merchantId: 2, merchantName: '服饰优选店', categoryName: '连衣裙', price: 159.00, status: 1, auditRemark: '审核通过', createdAt: '2026-07-02 11:00:00' },
  { id: 8, name: '春秋夹克外套 男款防风', mainImage: 'https://images.unsplash.com/photo-1520975954732-35dd22299614?w=100&q=80', merchantId: 2, merchantName: '服饰优选店', categoryName: '夹克', price: 199.00, status: 0, auditRemark: null, createdAt: '2026-07-03 09:00:00' },
  { id: 9, name: '折叠屏手机 Flip 5G 12GB+512GB', mainImage: 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=100&q=80', merchantId: 1, merchantName: '数码旗舰店', categoryName: '智能手机', price: 7999.00, status: 3, auditRemark: '信息不完整', createdAt: '2026-07-03 10:00:00' },
]

export async function getProductList(params?: any) {
  await delay()
  let list = [...products]
  if (params?.name) list = list.filter(p => p.name.includes(params.name))
  if (params?.status !== undefined && params?.status !== '') list = list.filter(p => p.status === Number(params.status))
  return { code: 200, data: { list, total: list.length } }
}

export async function getProductDetail(id: number) {
  await delay()
  const p = products.find(item => item.id === id)
  return { code: 200, data: { ...p, description: '产品详细描述...', skus: [] } }
}

export async function auditProduct(id: number, data: { action: string; remark: string }) {
  await delay()
  const p = products.find(item => item.id === id)
  if (p) { p.status = data.action === 'approve' ? 1 : 3; p.auditRemark = data.remark }
  return { code: 200, message: data.action === 'approve' ? '审核通过' : '已驳回' }
}
