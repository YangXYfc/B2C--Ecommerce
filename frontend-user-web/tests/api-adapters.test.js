import assert from 'node:assert/strict'
import test from 'node:test'

const adapters = await import('../src/api/adapters.js').catch(() => ({}))

test('real cart DTO is mapped to the consumer web contract', () => {
  const result = adapters.normalizeCart?.({
    items: [{
      id: 8, skuId: 3, productId: 1, productName: '智选 Pro 5G', skuName: '钛空灰',
      imageUrl: '/p1.jpg', unitPrice: 4999, stock: 20, quantity: 1, selected: true,
      merchantId: 2, merchantName: '数码旗舰店',
    }],
    selectedAmount: 4999,
  })

  assert.deepEqual(result, {
    items: [{
      id: 8, skuId: 3, productId: 1, productName: '智选 Pro 5G', skuName: '钛空灰',
      productImage: '/p1.jpg', price: 4999, stock: 20, quantity: 1, selected: true,
      merchantId: 2, merchantName: '数码旗舰店',
    }],
    totalAmount: 4999,
  })
})

test('real order DTO is mapped to address and review fields used by the web UI', () => {
  const result = adapters.normalizeOrder?.({
    id: 5,
    merchantId: 2,
    addressSnapshot: '{"name":"张三","phone":"13800000000"}',
    items: [{ productId: 1, skuId: 3, productName: '智选 Pro 5G', productImage: '/p1.jpg', unitPrice: 4999, quantity: 1 }],
  })

  assert.equal(result.addressSnapshot.name, '张三')
  assert.equal(result.merchantName, '商家 2')
  assert.equal(result.items[0].productId, 1)
})

test('real reviews page and merchant application are mapped to backend contracts', () => {
  assert.deepEqual(adapters.normalizeReviews?.({ records: [{ id: 1, userId: 4, rating: 5 }], total: 1 }), [{ id: 1, userId: 4, nickname: '用户4', rating: 5 }])
  assert.deepEqual(
    adapters.normalizeMerchantApplication?.({ shopName: '悦选店', contactPhone: '13800000000', description: '好店' }),
    { shopName: '悦选店', contactPhone: '13800000000', description: '好店', shopLogo: undefined },
  )
})
