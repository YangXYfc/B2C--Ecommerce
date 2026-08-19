import { describe, expect, it } from 'vitest'
import { getOrderActions, getOrderStatus } from '../src/utils/order.js'

describe('order presentation', () => {
  it('maps every documented state to readable text', () => {
    expect([0, 1, 2, 3, 4, 5].map((status) => getOrderStatus(status).text)).toEqual([
      '待支付', '待发货', '待收货', '已收货', '已完成', '已取消',
    ])
  })

  it('only exposes actions allowed by the current state', () => {
    expect(getOrderActions(0)).toEqual(['pay', 'cancel'])
    expect(getOrderActions(2)).toEqual(['confirm'])
    expect(getOrderActions(4)).toEqual(['review', 'refund'])
    expect(getOrderActions(5)).toEqual([])
  })
})
