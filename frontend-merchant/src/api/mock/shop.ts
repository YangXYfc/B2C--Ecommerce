// Mock: 店铺设置

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

const shopInfo = {
  shopName: '数码旗舰店',
  shopLogo: 'https://img.jd-demo.com/shop/logo1.png',
  description: '主营手机、电脑、数码配件，正品保障',
  contactPhone: '13800000002',
  customerServicePhone: '400-100-1001',
  returnPolicy: '7天无理由退换货',
  freeShippingThreshold: 99,
}

export async function getShop() {
  await delay()
  return { code: 200, data: shopInfo }
}

export async function updateShop(data: any) {
  await delay()
  Object.assign(shopInfo, data)
  return { code: 200, message: '保存成功' }
}
