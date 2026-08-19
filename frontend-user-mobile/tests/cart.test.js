import { describe, expect, it } from 'vitest'
import { calculateSelectedTotal, groupItemsByMerchant, normalizeQuantity } from '../src/utils/cart.js'

const items = [
  { id: 1, merchantId: 1, merchantName: '数码旗舰店', selected: 1, quantity: 2, price: 100 },
  { id: 2, merchantId: 2, merchantName: '服饰优选店', selected: 0, quantity: 3, price: 50 },
]

describe('cart calculations', () => {
  it('totals selected lines only', () => {
    expect(calculateSelectedTotal(items)).toBe(200)
  })

  it('groups lines by merchant', () => {
    expect(groupItemsByMerchant(items).map((group) => group.name)).toEqual(['数码旗舰店', '服饰优选店'])
  })

  it('clamps quantity to stock and at least one', () => {
    expect(normalizeQuantity(0, 8)).toBe(1)
    expect(normalizeQuantity(12, 8)).toBe(8)
  })
})
