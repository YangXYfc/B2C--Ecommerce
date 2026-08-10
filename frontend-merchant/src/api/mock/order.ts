// Mock: 订单管理

const delay = (ms = 300) => new Promise(r => setTimeout(r, ms))

export const orderStatusMap: Record<number, string> = {
  0: '待支付', 1: '待发货', 2: '已发货', 3: '已收货', 4: '已评价', 5: '已取消',
}

const orders: any[] = [
  { id: 1, orderNo: 'ORD20260707000001', totalAmount: 4999.00, payAmount: 4999.00, status: 3,
    addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' },
    logisticsCompany: '顺丰速运', logisticsNo: 'SF1234567890', shipTime: '2026-07-05 10:00', receiveTime: '2026-07-06 14:00', payTime: '2026-07-04 09:00',
    items: [{ productName: '智选 Pro 5G 手机 12GB+256GB 钛空灰', skuName: '钛空灰 12GB+256GB', productImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&q=80', quantity: 1, unitPrice: 4999.00, subtotal: 4999.00 }],
    createdAt: '2026-07-04 09:00:00' },
  { id: 2, orderNo: 'ORD20260707000002', totalAmount: 129.00, payAmount: 129.00, status: 1,
    addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' },
    logisticsCompany: null, logisticsNo: null, payTime: '2026-07-06 15:00',
    items: [{ productName: '65W GaN氮化镓充电器 三口快充', skuName: '白色', productImage: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=100&q=80', quantity: 1, unitPrice: 129.00, subtotal: 129.00 }],
    createdAt: '2026-07-06 15:00:00' },
  { id: 6, orderNo: 'ORD20260707000006', totalAmount: 4299.00, payAmount: 4299.00, status: 4,
    addressSnapshot: { name: '李四', phone: '13800000005', province: '上海市', city: '上海市', district: '浦东新区', detail: '世纪大道100号环球金融中心56层' },
    logisticsCompany: '京东物流', logisticsNo: 'JD5678901234', shipTime: '2026-07-03 09:00', receiveTime: '2026-07-04 15:00', payTime: '2026-07-02 10:00',
    items: [{ productName: '轻薄本 Air 14 锐龙版 16GB+512GB', skuName: '16GB+512GB 银色', productImage: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=100&q=80', quantity: 1, unitPrice: 4299.00, subtotal: 4299.00 }],
    createdAt: '2026-07-02 10:00:00' },
  { id: 4, orderNo: 'ORD20260707000004', totalAmount: 1599.00, payAmount: null, status: 0,
    addressSnapshot: { name: '王五', phone: '13800000006', province: '广东省', city: '深圳市', district: '南山区', detail: '科技园南区T3栋501室' },
    logisticsCompany: null, logisticsNo: null,
    items: [{ productName: '畅享 Note 5G 手机 8GB+128GB 幻夜黑', skuName: '幻夜黑 8GB+128GB', productImage: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=100&q=80', quantity: 1, unitPrice: 1599.00, subtotal: 1599.00 }],
    createdAt: '2026-07-07 08:00:00' },
  { id: 5, orderNo: 'ORD20260707000005', totalAmount: 59.00, payAmount: null, status: 5, cancelReason: '不想买了',
    addressSnapshot: { name: '张三', phone: '13800000004', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号现代城SOHO 1号楼1801室' },
    logisticsCompany: null, logisticsNo: null,
    items: [{ productName: '纯棉短袖T恤 男款 100%新疆棉', skuName: '白色 L', productImage: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=100&q=80', quantity: 1, unitPrice: 59.00, subtotal: 59.00 }],
    createdAt: '2026-07-03 12:00:00' },
]

export async function getOrderList(params?: any) {
  await delay()
  let list = [...orders]
  if (params?.orderNo) list = list.filter(o => o.orderNo.includes(params.orderNo))
  if (params?.status !== undefined && params?.status !== '') list = list.filter(o => o.status === Number(params.status))
  return { code: 200, data: { list, total: list.length } }
}

export async function getOrderDetail(id: number) {
  await delay()
  return { code: 200, data: orders.find(o => o.id === id) }
}

export async function shipOrder(id: number, data: { logisticsCompany: string; logisticsNo: string }) {
  await delay()
  const o = orders.find(item => item.id === id)
  if (o) { o.status = 2; o.logisticsCompany = data.logisticsCompany; o.logisticsNo = data.logisticsNo; o.shipTime = new Date().toISOString() }
  return { code: 200, message: '发货成功' }
}
