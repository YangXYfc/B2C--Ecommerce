import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryStorage } from '../src/utils/storage.js'
import { createMockService } from '../src/api/mock/service.js'

let service

beforeEach(() => {
  service = createMockService(createMemoryStorage())
})

describe('mock shopping flow', () => {
  it('logs in with the documented demo account', async () => {
    const session = await service.login({ username: 'user1', password: '123456' })
    expect(session.token).toContain('mock-token')
    expect(session.user.nickname).toBe('张三')
  })

  it('creates and pays an order from selected cart lines', async () => {
    const created = await service.createOrder({ addressId: 1, cartItemIds: [1], remark: '' })
    expect(created.status).toBe(0)
    const paid = await service.payOrder(created.id)
    expect(paid.status).toBe(1)
  })

  it('creates a refund for an eligible order', async () => {
    const refund = await service.createRefund({ orderId: 1, reason: '商品质量问题', description: '无法正常使用', amount: 4999 })
    expect(refund.status).toBe(0)
    expect(refund.refundNo).toMatch(/^RFD/)
  })
})
