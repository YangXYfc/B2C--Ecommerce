// Mock: 商品管理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export const categories = [
  { id: 111, name: '手机通讯 / 智能手机' },
  { id: 121, name: '电脑办公 / 笔记本' },
  { id: 13, name: '数码配件' },
  { id: 21, name: '大家电' },
]

const products: any[] = [
  { id: 1, name: '智选 Pro 5G 手机 12GB+256GB 钛空灰', subtitle: '旗舰芯片 | 徕卡光学 | 120W快充', categoryId: 111, categoryName: '智能手机', mainImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200&q=80', price: 4999.00, status: 1, salesCount: 1520, skuCount: 4, totalStock: 1150, createdAt: '2026-07-01 10:00:00' },
  { id: 2, name: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', subtitle: '大屏长续航 | 5000mAh', categoryId: 111, categoryName: '智能手机', mainImage: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=200&q=80', price: 1599.00, status: 1, salesCount: 3200, skuCount: 3, totalStock: 1800, createdAt: '2026-07-01 09:00:00' },
  { id: 3, name: '轻薄本 Air 14 锐龙版 16GB+512GB', subtitle: '14英寸2.8K屏 | 锐龙7 7840H', categoryId: 121, categoryName: '笔记本', mainImage: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=200&q=80', price: 4299.00, status: 1, salesCount: 860, skuCount: 2, totalStock: 300, createdAt: '2026-07-01 08:00:00' },
  { id: 4, name: '65W GaN氮化镓充电器 三口快充', subtitle: '小巧便携 | 兼容多设备', categoryId: 13, categoryName: '数码配件', mainImage: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=200&q=80', price: 129.00, status: 1, salesCount: 5800, skuCount: 1, totalStock: 2000, createdAt: '2026-07-01 07:00:00' },
  { id: 7, name: '4K激光投影电视 100英寸', subtitle: '影院级巨幕 | 护眼无屏闪', categoryId: 21, categoryName: '大家电', mainImage: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=200&q=80', price: 8999.00, status: 1, salesCount: 120, skuCount: 1, totalStock: 50, createdAt: '2026-07-02 10:00:00' },
  { id: 9, name: '折叠屏手机 Flip 5G 12GB+512GB', subtitle: '内外双屏 | 悬停自拍', categoryId: 111, categoryName: '智能手机', mainImage: 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=200&q=80', price: 7999.00, status: 3, salesCount: 0, skuCount: 1, totalStock: 100, createdAt: '2026-07-03 09:00:00' },
]

let nextId = 10

export async function getProductList(params?: any) {
  await delay()
  let list = [...products]
  if (params?.keyword) {
    list = list.filter(p => p.name.includes(params.keyword))
  }
  if (params?.categoryId) {
    list = list.filter(p => p.categoryId === params.categoryId)
  }
  if (params?.status !== undefined && params?.status !== '') {
    list = list.filter(p => p.status === Number(params.status))
  }
  return { code: 200, data: { list, total: list.length } }
}

export async function getProductDetail(id: number) {
  await delay()
  const p = products.find(item => item.id === id)
  if (!p) return { code: 404, message: '商品不存在' }
  return {
    code: 200,
    data: {
      ...p,
      description: '产品详细介绍内容...',
      detailHtml: '<p>产品详情HTML内容...</p>',
      subImages: [],
      skus: [
        { id: 1, skuName: '默认规格', price: p.price, originalPrice: p.price * 1.1, stock: 100, attributes: '{}', status: 1 },
      ],
    },
  }
}

export async function createProduct(data: any) {
  await delay()
  const p = { id: nextId++, ...data, status: 0, salesCount: 0, createdAt: new Date().toISOString(), categoryName: '待审核' }
  products.unshift(p)
  return { code: 200, message: '发布成功，等待审核' }
}

export async function updateProduct(id: number, data: any) {
  await delay()
  const idx = products.findIndex(p => p.id === id)
  if (idx > -1) Object.assign(products[idx], data)
  return { code: 200, message: '保存成功' }
}

export async function offShelfProduct(id: number) {
  await delay()
  const p = products.find(item => item.id === id)
  if (p) p.status = 2
  return { code: 200, message: '已下架' }
}

export async function onShelfProduct(id: number) {
  await delay()
  const p = products.find(item => item.id === id)
  if (p) p.status = 1
  return { code: 200, message: '已上架' }
}

export async function updateStock(skuId: number, stock: number) {
  await delay()
  return { code: 200, message: '库存更新成功' }
}
