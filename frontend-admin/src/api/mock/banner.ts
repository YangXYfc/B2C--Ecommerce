// Mock: 轮播图管理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const banners: any[] = [
  { id: 1, title: '618大促 全场低至5折', imageUrl: '/media/banners/banner-01-sale.jpg', linkUrl: '/promotion/618', sort: 1, status: 1 },
  { id: 2, title: '手机数码 新品首发', imageUrl: '/media/banners/banner-02-digital.jpg', linkUrl: '/category/111', sort: 2, status: 1 },
  { id: 3, title: '服饰换新 夏日清凉', imageUrl: '/media/banners/banner-03-fashion.jpg', linkUrl: '/category/3', sort: 3, status: 1 },
  { id: 4, title: '家电焕新 以旧换新', imageUrl: '/media/banners/banner-04-appliance.jpg', linkUrl: '/category/2', sort: 4, status: 1 },
]

let nextId = 5

export async function getBannerList() {
  await delay()
  return { code: 200, data: { list: banners, total: banners.length } }
}

export async function createBanner(data: any) {
  await delay()
  banners.push({ id: nextId++, ...data })
  return { code: 200, message: '创建成功' }
}

export async function updateBanner(id: number, data: any) {
  await delay()
  const b = banners.find(item => item.id === id)
  if (b) Object.assign(b, data)
  return { code: 200, message: '保存成功' }
}

export async function deleteBanner(id: number) {
  await delay()
  const idx = banners.findIndex(item => item.id === id)
  if (idx > -1) banners.splice(idx, 1)
  return { code: 200, message: '删除成功' }
}
