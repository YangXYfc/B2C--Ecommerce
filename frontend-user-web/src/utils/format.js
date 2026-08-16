export const ORDER_STATUS = {
  0: { label: '待付款', type: 'warning' },
  1: { label: '待发货', type: 'primary' },
  2: { label: '已发货', type: 'info' },
  3: { label: '已收货', type: 'success' },
  4: { label: '已评价', type: 'success' },
  5: { label: '已取消', type: 'info' },
}

export const REFUND_STATUS = {
  0: { label: '待商家审核', type: 'warning' },
  1: { label: '商家已通过', type: 'primary' },
  2: { label: '退货寄回中', type: 'info' },
  3: { label: '退款完成', type: 'success' },
  4: { label: '商家已拒绝', type: 'danger' },
  5: { label: '平台介入中', type: 'warning' },
  6: { label: '平台支持退款', type: 'success' },
  7: { label: '平台拒绝退款', type: 'danger' },
}

export function formatPrice(value) {
  const num = Number(value)
  if (Number.isNaN(num)) return '0.00'
  return num.toFixed(2)
}

export function formatDate(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}
