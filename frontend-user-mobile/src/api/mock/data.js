export const seed = {
  users: [
    { id: 4, username: 'user1', password: '123456', phone: '13800000004', email: 'user1@demo.com', nickname: '张三', gender: 1, avatar: '', role: 'USER' },
  ],
  merchants: [
    { id: 1, name: '数码旗舰店' },
    { id: 2, name: '服饰优选店' },
    { id: 3, name: '生活美学馆' },
  ],
  categories: [
    { id: 1, name: '手机数码', scene: '数码焕新', children: [{ id: 11, name: '手机通讯', image: '/static/images/product-phone.jpg' }, { id: 12, name: '电脑办公', image: '/static/images/product-laptop.jpg' }, { id: 13, name: '数码配件', image: '/static/images/product-charger.jpg' }] },
    { id: 2, name: '家用电器', scene: '舒适居家', children: [{ id: 21, name: '大家电', image: '/static/images/scene-home.jpg' }, { id: 22, name: '生活电器', image: '/static/images/product-purifier.jpg' }] },
    { id: 3, name: '服饰鞋包', scene: '轻盈穿搭', children: [{ id: 31, name: '男装', image: '/static/images/product-shirt.jpg' }, { id: 32, name: '女装', image: '/static/images/product-dress.jpg' }] },
    { id: 4, name: '食品生鲜', scene: '新鲜厨房', children: [{ id: 41, name: '时令水果', image: '/static/images/scene-fruit.jpg' }, { id: 42, name: '乳品饮料', image: '/static/images/scene-home.jpg' }] },
  ],
  banners: [
    { id: 1, title: '夏日生活焕新', imageUrl: '/static/images/home-hero.png', linkUrl: '/pages/product/list' },
    { id: 2, title: '数码轻盈季', imageUrl: '/static/images/product-laptop.jpg', linkUrl: '/pages/product/list?categoryId=1' },
  ],
  products: [
    { id: 1, merchantId: 1, categoryId: 11, name: '智选 Pro 5G 手机 12GB+256GB 钛空灰', subtitle: '旗舰影像｜轻盈机身', mainImage: '/static/images/product-phone.jpg', description: '旗舰处理器与高素质 OLED 屏幕，兼顾影像、续航和轻盈手感。', price: 4999, salesCount: 15200, featured: true },
    { id: 2, merchantId: 1, categoryId: 12, name: '轻薄本 Air 14 锐龙版 16GB+512GB', subtitle: '轻约1.4kg｜长续航', mainImage: '/static/images/product-laptop.jpg', description: '2.8K 高色域屏幕与全天候续航，适合学习和移动办公。', price: 4299, salesCount: 8600, featured: true },
    { id: 3, merchantId: 2, categoryId: 32, name: '法式碎花连衣裙 夏季新款', subtitle: '轻盈透气｜收腰显瘦', mainImage: '/static/images/product-dress.jpg', description: '柔软雪纺面料与自然收腰剪裁，适合日常通勤。', price: 199, salesCount: 21000, featured: false },
    { id: 4, merchantId: 3, categoryId: 22, name: '静音空气净化器 4 Lite', subtitle: '高效除醛｜低噪睡眠', mainImage: '/static/images/product-purifier.jpg', description: '小巧机身适合卧室与书房，智能监测室内空气。', price: 699, salesCount: 48000, featured: false },
    { id: 5, merchantId: 1, categoryId: 13, name: '65W GaN 氮化镓三口充电器', subtitle: '小巧便携｜兼容多设备', mainImage: '/static/images/product-charger.jpg', description: '双 USB-C 加 USB-A，满足手机与电脑同时快充。', price: 129, salesCount: 58000, featured: false },
    { id: 6, merchantId: 3, categoryId: 41, name: '海南妃子笑荔枝 2斤装', subtitle: '果肉饱满｜清甜多汁', mainImage: '/static/images/scene-fruit.jpg', description: '产地直发，新鲜采摘，冷链送达。', price: 39.9, salesCount: 52000, featured: false },
    { id: 7, merchantId: 2, categoryId: 31, name: '纯棉短袖T恤 男款基础白', subtitle: '透气舒适｜百搭版型', mainImage: '/static/images/product-shirt.jpg', description: '精梳棉面料，耐洗不易变形。', price: 59, salesCount: 89000, featured: false },
  ],
  skus: [
    { id: 1, productId: 1, name: '钛空灰 12GB+256GB', price: 4999, stock: 500 },
    { id: 2, productId: 1, name: '雪山白 12GB+256GB', price: 4999, stock: 300 },
    { id: 3, productId: 2, name: '银色 16GB+512GB', price: 4299, stock: 200 },
    { id: 4, productId: 3, name: '碎花 M', price: 199, stock: 300 },
    { id: 5, productId: 4, name: '标准版 白色', price: 699, stock: 160 },
    { id: 6, productId: 5, name: '白色 65W', price: 129, stock: 2000 },
    { id: 7, productId: 6, name: '净重2斤', price: 39.9, stock: 800 },
    { id: 8, productId: 7, name: '白色 L', price: 59, stock: 500 },
  ],
  reviews: [
    { id: 1, productId: 1, nickname: '小悦同学', rating: 5, content: '手感轻，拍照也很自然，配送速度很快。', createdAt: '2026-08-12 14:20' },
  ],
  addresses: [
    { id: 1, name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城1号楼1801', isDefault: 1 },
    { id: 2, name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '海淀区', detail: '中关村大街1号1502', isDefault: 0 },
  ],
  cart: [
    { id: 1, skuId: 1, quantity: 1, selected: 1 },
    { id: 2, skuId: 8, quantity: 2, selected: 1 },
    { id: 3, skuId: 6, quantity: 1, selected: 0 },
  ],
  orders: [
    { id: 1, orderNo: 'ORD202608120001', merchantId: 1, totalAmount: 4999, payAmount: 4999, status: 4, addressId: 1, remark: '', createdAt: '2026-08-12 09:30', itemIds: [1] },
    { id: 2, orderNo: 'ORD202608180002', merchantId: 2, totalAmount: 118, payAmount: null, status: 0, addressId: 1, remark: '', createdAt: '2026-08-18 18:10', itemIds: [8] },
  ],
  refunds: [],
}
