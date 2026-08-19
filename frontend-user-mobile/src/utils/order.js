const STATUS = {
  0: { text: '待支付', tone: 'warning' },
  1: { text: '待发货', tone: 'accent' },
  2: { text: '待收货', tone: 'accent' },
  3: { text: '已收货', tone: 'success' },
  4: { text: '已完成', tone: 'success' },
  5: { text: '已取消', tone: 'muted' },
}

const ACTIONS = {
  0: ['pay', 'cancel'],
  1: ['refund'],
  2: ['confirm'],
  3: ['review', 'refund'],
  4: ['review', 'refund'],
  5: [],
}

export function getOrderStatus(status) {
  return STATUS[Number(status)] || { text: '未知状态', tone: 'muted' }
}

export function getOrderActions(status) {
  return [...(ACTIONS[Number(status)] || [])]
}
