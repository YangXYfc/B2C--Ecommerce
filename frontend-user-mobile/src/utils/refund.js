const STATUS = {
  0: { text: '待商家审核', tone: 'warning' },
  1: { text: '商家已同意', tone: 'accent' },
  2: { text: '等待退货', tone: 'accent' },
  3: { text: '退款处理中', tone: 'warning' },
  4: { text: '退款完成', tone: 'success' },
  5: { text: '商家已拒绝', tone: 'danger' },
  6: { text: '平台仲裁中', tone: 'warning' },
  7: { text: '已关闭', tone: 'muted' },
}

export function getRefundStatus(status) {
  return STATUS[Number(status)] || { text: '未知状态', tone: 'muted' }
}

export function getRefundActions(status) {
  if (Number(status) === 2) return ['logistics']
  if (Number(status) === 5) return ['appeal']
  return []
}
