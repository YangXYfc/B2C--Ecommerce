// 基于文档附录 data.sql 的演示数据
export const mockUsers = [
  { id: 4, username: 'user1', password: '123456', phone: '13800000004', email: 'user1@jd-demo.com', nickname: '张三', gender: 1, avatar: null, role: 'USER' },
  { id: 5, username: 'user2', password: '123456', phone: '13800000005', email: 'user2@jd-demo.com', nickname: '李四', gender: 2, avatar: null, role: 'USER' },
  { id: 6, username: 'user3', password: '123456', phone: '13800000006', email: 'user3@jd-demo.com', nickname: '王五', gender: 1, avatar: null, role: 'USER' },
]

export const mockMerchants = [
  { id: 1, userId: 2, shopName: '数码旗舰店', shopLogo: '/media/shops/shop-01-digital.jpg' },
  { id: 2, userId: 3, shopName: '服饰优选店', shopLogo: '/media/shops/shop-02-fashion.jpg' },
]

export const mockCategories = [
  { id: 1, name: '手机数码', parentId: 0, sort: 1, icon: '/media/categories/category-01-digital.jpg' },
  { id: 2, name: '家用电器', parentId: 0, sort: 2, icon: '/media/categories/category-02-appliance.jpg' },
  { id: 3, name: '服饰鞋包', parentId: 0, sort: 3, icon: '/media/categories/category-03-fashion.jpg' },
  { id: 4, name: '食品生鲜', parentId: 0, sort: 4, icon: '/media/categories/category-04-food.jpg' },
  { id: 11, name: '手机通讯', parentId: 1, sort: 1 },
  { id: 12, name: '电脑办公', parentId: 1, sort: 2 },
  { id: 13, name: '数码配件', parentId: 1, sort: 3 },
  { id: 21, name: '大家电', parentId: 2, sort: 1 },
  { id: 22, name: '厨电', parentId: 2, sort: 2 },
  { id: 31, name: '男装', parentId: 3, sort: 1 },
  { id: 32, name: '女装', parentId: 3, sort: 2 },
  { id: 33, name: '鞋靴', parentId: 3, sort: 3 },
  { id: 111, name: '智能手机', parentId: 11, sort: 1 },
  { id: 121, name: '笔记本', parentId: 12, sort: 1 },
  { id: 311, name: 'T恤', parentId: 31, sort: 1 },
  { id: 321, name: '连衣裙', parentId: 32, sort: 1 },
]

export const mockProducts = [
  { id: 1, merchantId: 1, categoryId: 111, name: '智选 Pro 5G 手机 12GB+256GB 钛空灰', subtitle: '旗舰芯片 | 徕卡光学 | 120W快充', mainImage: '/media/products/product-01-phone.jpg', subImages: ['/media/products/product-02-phone.jpg', '/media/products/product-09-foldable.jpg'], description: '搭载最新旗舰处理器，6.7英寸OLED屏幕，5000mAh大电池，支持120W有线快充和50W无线快充。', detailHtml: '<p>产品详情：旗舰处理器，徕卡四摄系统，拍照体验出众。</p>', price: 4999, status: 1, salesCount: 1520 },
  { id: 2, merchantId: 1, categoryId: 111, name: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', subtitle: '大屏长续航 | 5000mAh', mainImage: '/media/products/product-02-phone.jpg', subImages: ['/media/products/product-01-phone.jpg'], description: '6.8英寸大屏，5000mAh超大电池，支持33W快充，后置6400万像素三摄。', detailHtml: '<p>大屏长续航，性价比之选。</p>', price: 1599, status: 1, salesCount: 3200 },
  { id: 3, merchantId: 1, categoryId: 121, name: '轻薄本 Air 14 锐龙版 16GB+512GB', subtitle: '14英寸2.8K屏 | 锐龙7 7840H', mainImage: '/media/products/product-03-laptop.jpg', subImages: ['/media/products/laptop-detail-01.jpg'], description: '14英寸2.8K OLED屏幕，AMD锐龙7 7840H处理器，16GB LPDDR5内存，512GB NVMe SSD。', detailHtml: '<p>1.2kg轻薄机身，办公学习利器。</p>', price: 4299, status: 1, salesCount: 860 },
  { id: 4, merchantId: 1, categoryId: 13, name: '65W GaN氮化镓充电器 三口快充', subtitle: '小巧便携 | 兼容多设备', mainImage: '/media/products/product-04-charger.jpg', subImages: [], description: '65W GaN氮化镓快充，支持PD/PPS/QC等多种协议。', detailHtml: '<p>双USB-C+USB-A三口设计，折叠插脚便携出行。</p>', price: 129, status: 1, salesCount: 5800 },
  { id: 5, merchantId: 2, categoryId: 311, name: '纯棉短袖T恤 男款 100%新疆棉', subtitle: '透气舒适 | 多色可选', mainImage: '/media/products/product-05-tshirt.jpg', subImages: ['/media/products/tshirt-detail-01.jpg'], description: '100%新疆长绒棉，260g重磅面料，精梳工艺。', detailHtml: '<p>领口加固不易变形，多色多码可选。</p>', price: 59, status: 1, salesCount: 8900 },
  { id: 6, merchantId: 2, categoryId: 321, name: '法式碎花连衣裙 夏季新款', subtitle: '显瘦版型 | 优雅气质', mainImage: '/media/products/product-06-dress.jpg', subImages: [], description: '法式方领设计，高腰A字版型显瘦，雪纺面料飘逸舒适。', detailHtml: '<p>适合日常和约会穿着。</p>', price: 159, status: 1, salesCount: 2300 },
  { id: 7, merchantId: 1, categoryId: 21, name: '4K激光投影电视 100英寸', subtitle: '影院级巨幕 | 护眼无屏闪', mainImage: '/media/products/product-07-projector.jpg', subImages: [], description: '4K分辨率激光投影，100英寸超大画面，3500ANSI流明。', detailHtml: '<p>MEMC运动补偿，哈曼卡顿音响。</p>', price: 8999, status: 1, salesCount: 120 },
]

export const mockSkus = [
  { id: 1, productId: 1, skuName: '智选Pro 5G 钛空灰 12GB+256GB', price: 4999, originalPrice: 5499, stock: 500, attributes: { 颜色: '钛空灰', 版本: '12GB+256GB' }, skuImage: '/media/products/product-01-phone.jpg' },
  { id: 2, productId: 1, skuName: '智选Pro 5G 雪山白 12GB+256GB', price: 4999, originalPrice: 5499, stock: 300, attributes: { 颜色: '雪山白', 版本: '12GB+256GB' }, skuImage: '/media/products/product-01-phone.jpg' },
  { id: 3, productId: 1, skuName: '智选Pro 5G 钛空灰 16GB+512GB', price: 5499, originalPrice: 5999, stock: 200, attributes: { 颜色: '钛空灰', 版本: '16GB+512GB' }, skuImage: '/media/products/product-01-phone.jpg' },
  { id: 5, productId: 2, skuName: '畅享Note 5G 幻夜黑 8GB+128GB', price: 1599, originalPrice: 1799, stock: 800, attributes: { 颜色: '幻夜黑', 版本: '8GB+128GB' }, skuImage: '/media/products/product-02-phone.jpg' },
  { id: 6, productId: 2, skuName: '畅享Note 5G 晨曦金 8GB+128GB', price: 1599, originalPrice: 1799, stock: 600, attributes: { 颜色: '晨曦金', 版本: '8GB+128GB' }, skuImage: '/media/products/product-02-phone.jpg' },
  { id: 8, productId: 3, skuName: 'Air14 锐龙版 16GB+512GB 银色', price: 4299, originalPrice: 4999, stock: 200, attributes: { 颜色: '银色', 配置: '16GB+512GB' }, skuImage: '/media/products/product-03-laptop.jpg' },
  { id: 10, productId: 4, skuName: '65W GaN充电器 白色', price: 129, originalPrice: 159, stock: 2000, attributes: { 颜色: '白色' }, skuImage: '/media/products/product-04-charger.jpg' },
  { id: 11, productId: 5, skuName: 'T恤 白色 L', price: 59, originalPrice: 89, stock: 500, attributes: { 颜色: '白色', 尺码: 'L' }, skuImage: '/media/products/product-05-tshirt.jpg' },
  { id: 12, productId: 5, skuName: 'T恤 白色 XL', price: 59, originalPrice: 89, stock: 500, attributes: { 颜色: '白色', 尺码: 'XL' }, skuImage: '/media/products/product-05-tshirt.jpg' },
  { id: 13, productId: 5, skuName: 'T恤 黑色 L', price: 59, originalPrice: 89, stock: 500, attributes: { 颜色: '黑色', 尺码: 'L' }, skuImage: '/media/products/product-05-tshirt.jpg' },
  { id: 16, productId: 6, skuName: '碎花连衣裙 S', price: 159, originalPrice: 259, stock: 200, attributes: { 颜色: '碎花', 尺码: 'S' }, skuImage: '/media/products/product-06-dress.jpg' },
  { id: 17, productId: 6, skuName: '碎花连衣裙 M', price: 159, originalPrice: 259, stock: 300, attributes: { 颜色: '碎花', 尺码: 'M' }, skuImage: '/media/products/product-06-dress.jpg' },
  { id: 19, productId: 7, skuName: '激光投影电视 100英寸', price: 8999, originalPrice: 10999, stock: 50, attributes: { 规格: '100英寸' }, skuImage: '/media/products/product-07-projector.jpg' },
]

export const mockBanners = [
  { id: 1, title: '618大促 全场低至5折', imageUrl: '/media/banners/banner-01-sale.jpg', linkUrl: '/products', sort: 1 },
  { id: 2, title: '手机数码 新品首发', imageUrl: '/media/banners/banner-02-digital.jpg', linkUrl: '/products?categoryId=1', sort: 2 },
  { id: 3, title: '服饰换新 夏日清凉', imageUrl: '/media/banners/banner-03-fashion.jpg', linkUrl: '/products?categoryId=3', sort: 3 },
  { id: 4, title: '家电焕新 以旧换新', imageUrl: '/media/banners/banner-04-appliance.jpg', linkUrl: '/products?categoryId=2', sort: 4 },
]

export const mockReviews = [
  { id: 1, orderId: 6, productId: 3, userId: 5, nickname: '李四', content: '笔记本很轻薄，屏幕素质非常好，2.8K OLED显示效果惊艳。续航日常使用7小时左右，整体满意。', rating: 5, images: [], merchantReply: '感谢您的支持！', createdAt: '2026-07-04 16:00:00' },
]

export function getInitialAddresses() {
  return [
    { id: 1, userId: 4, name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室', isDefault: 1 },
    { id: 2, userId: 4, name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '海淀区', detail: '中关村大街1号海龙大厦1502室', isDefault: 0 },
    { id: 3, userId: 5, name: '李四', phone: '13800000005', province: '上海市', city: '上海市', district: '浦东新区', detail: '世纪大道100号环球金融中心56层', isDefault: 1 },
    { id: 4, userId: 6, name: '王五', phone: '13800000006', province: '广东省', city: '深圳市', district: '南山区', detail: '科技园南区T3栋501室', isDefault: 1 },
  ]
}

export function getInitialCart() {
  return [
    { id: 1, userId: 4, productSkuId: 1, quantity: 1, selected: 1 },
    { id: 2, userId: 4, productSkuId: 11, quantity: 2, selected: 1 },
    { id: 3, userId: 4, productSkuId: 10, quantity: 1, selected: 0 },
  ]
}

export function getInitialOrders() {
  return [
    { id: 1, orderNo: 'ORD20260707000001', userId: 4, merchantId: 1, totalAmount: 4999, payAmount: 4999, status: 3, addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' }, remark: '请尽快发货', logisticsCompany: '顺丰速运', logisticsNo: 'SF1234567890', shipTime: '2026-07-05 10:00:00', receiveTime: '2026-07-06 14:00:00', payTime: '2026-07-04 09:00:00', createdAt: '2026-07-04 08:30:00' },
    { id: 2, orderNo: 'ORD20260707000002', userId: 4, merchantId: 1, totalAmount: 129, payAmount: 129, status: 1, addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' }, payTime: '2026-07-06 15:00:00', createdAt: '2026-07-06 14:50:00' },
    { id: 3, orderNo: 'ORD20260707000003', userId: 5, merchantId: 2, totalAmount: 159, payAmount: 159, status: 2, addressSnapshot: { name: '李四', phone: '13800000005', province: '上海市', city: '上海市', district: '浦东新区', detail: '世纪大道100号环球金融中心56层' }, logisticsCompany: '中通快递', logisticsNo: 'ZT9876543210', shipTime: '2026-07-06 16:00:00', payTime: '2026-07-05 11:00:00', createdAt: '2026-07-05 10:30:00' },
    { id: 4, orderNo: 'ORD20260707000004', userId: 6, merchantId: 1, totalAmount: 1599, payAmount: null, status: 0, addressSnapshot: { name: '王五', phone: '13800000006', province: '广东省', city: '深圳市', district: '南山区', detail: '科技园南区T3栋501室' }, createdAt: '2026-07-07 09:00:00' },
    { id: 5, orderNo: 'ORD20260707000005', userId: 4, merchantId: 2, totalAmount: 59, payAmount: null, status: 5, addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' }, cancelTime: '2026-07-03 12:00:00', cancelReason: '不想买了', createdAt: '2026-07-03 11:00:00' },
    { id: 6, orderNo: 'ORD20260707000006', userId: 5, merchantId: 1, totalAmount: 4299, payAmount: 4299, status: 4, addressSnapshot: { name: '李四', phone: '13800000005', province: '上海市', city: '上海市', district: '浦东新区', detail: '世纪大道100号环球金融中心56层' }, logisticsCompany: '京东物流', logisticsNo: 'JD5678901234', shipTime: '2026-07-03 09:00:00', receiveTime: '2026-07-04 15:00:00', payTime: '2026-07-02 10:00:00', createdAt: '2026-07-02 09:30:00' },
  ]
}

export function getInitialOrderItems() {
  return [
    { id: 1, orderId: 1, productSkuId: 1, productId: 1, productName: '智选 Pro 5G 手机 12GB+256GB 钛空灰', skuName: '智选Pro 5G 钛空灰 12GB+256GB', productImage: '/media/products/product-01-phone.jpg', quantity: 1, unitPrice: 4999, subtotal: 4999 },
    { id: 2, orderId: 2, productSkuId: 10, productId: 4, productName: '65W GaN氮化镓充电器 三口快充', skuName: '65W GaN充电器 白色', productImage: '/media/products/product-04-charger.jpg', quantity: 1, unitPrice: 129, subtotal: 129 },
    { id: 3, orderId: 3, productSkuId: 16, productId: 6, productName: '法式碎花连衣裙 夏季新款', skuName: '碎花连衣裙 S', productImage: '/media/products/product-06-dress.jpg', quantity: 1, unitPrice: 159, subtotal: 159 },
    { id: 4, orderId: 4, productSkuId: 5, productId: 2, productName: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', skuName: '畅享Note 5G 幻夜黑 8GB+128GB', productImage: '/media/products/product-02-phone.jpg', quantity: 1, unitPrice: 1599, subtotal: 1599 },
    { id: 5, orderId: 5, productSkuId: 11, productId: 5, productName: '纯棉短袖T恤 男款 100%新疆棉', skuName: 'T恤 白色 L', productImage: '/media/products/product-05-tshirt.jpg', quantity: 1, unitPrice: 59, subtotal: 59 },
    { id: 6, orderId: 6, productSkuId: 8, productId: 3, productName: '轻薄本 Air 14 锐龙版 16GB+512GB', skuName: 'Air14 锐龙版 16GB+512GB 银色', productImage: '/media/products/product-03-laptop.jpg', quantity: 1, unitPrice: 4299, subtotal: 4299 },
  ]
}

export function getInitialRefunds() {
  return [
    { id: 1, refundNo: 'RFD20260706000001', orderId: 1, userId: 4, merchantId: 1, reason: '商品质量问题', description: '手机屏幕有坏点，要求退款', amount: 4999, status: 3, merchantRemark: '同意退款，请寄回商品', returnLogisticsCompany: '顺丰速运', returnLogisticsNo: 'SF1112223334', completedTime: '2026-07-07 09:30:00', createdAt: '2026-07-06 08:00:00' },
    { id: 2, refundNo: 'RFD20260707000002', orderId: 2, userId: 4, merchantId: 1, reason: '不想要了', description: '买重复了，申请退款', amount: 129, status: 0, createdAt: '2026-07-07 10:00:00' },
  ]
}

export function getMerchantName(merchantId) {
  return mockMerchants.find((m) => m.id === merchantId)?.shopName || '未知店铺'
}

export function enrichProduct(product) {
  const merchant = mockMerchants.find((m) => m.id === product.merchantId)
  return {
    ...product,
    merchantName: merchant?.shopName,
    shopLogo: merchant?.shopLogo,
  }
}

export function enrichSku(sku) {
  const product = mockProducts.find((p) => p.id === sku.productId)
  return {
    ...sku,
    productName: product?.name,
    productImage: product?.mainImage,
    merchantId: product?.merchantId,
    merchantName: getMerchantName(product?.merchantId),
  }
}
