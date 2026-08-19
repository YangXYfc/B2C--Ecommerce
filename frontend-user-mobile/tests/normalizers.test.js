import { describe, expect, it } from 'vitest'
import * as normalizers from '../src/api/normalizers.js'

const { normalizePage, unwrapResult } = normalizers

describe('API normalizers', () => {
  it('unwraps successful backend responses', () => {
    expect(unwrapResult({ code: 200, data: { id: 1 }, message: 'ok' })).toEqual({ id: 1 })
    expect(unwrapResult({ code: 'SUCCESS', data: { id: 2 }, message: 'ok' })).toEqual({ id: 2 })
  })

  it('normalizes both records and list pagination shapes', () => {
    expect(normalizePage({ records: [{ id: 1 }], total: 1 })).toEqual({ records: [{ id: 1 }], total: 1 })
    expect(normalizePage({ list: [{ id: 2 }], total: 1 })).toEqual({ records: [{ id: 2 }], total: 1 })
  })

  it('maps the real cart DTO to the mobile cart contract', () => {
    const result = normalizers.normalizeCart?.({
      items: [{
        id: 8, skuId: 3, productId: 1, productName: '智选 Pro 5G', skuName: '钛空灰',
        imageUrl: '/p1.jpg', unitPrice: 4999, stock: 20, quantity: 1, selected: true,
        merchantId: 2, merchantName: '数码旗舰店',
      }],
      selectedAmount: 4999,
    })

    expect(result).toEqual([{
      id: 8, skuId: 3, productId: 1, productName: '智选 Pro 5G', skuName: '钛空灰',
      productImage: '/p1.jpg', price: 4999, stock: 20, quantity: 1, selected: 1,
      merchantId: 2, merchantName: '数码旗舰店',
    }])
  })

  it('maps real order snapshots and item fields to mobile display fields', () => {
    const result = normalizers.normalizeOrder?.({
      id: 5,
      merchantId: 2,
      addressSnapshot: '{"name":"张三","phone":"13800000000","province":"广东省","city":"深圳市","district":"南山区","detail":"科技园1号"}',
      items: [{ productId: 1, skuId: 3, productName: '智选 Pro 5G', skuName: '钛空灰', productImage: '/p1.jpg', unitPrice: 4999, quantity: 1 }],
    })

    expect(result.address.name).toBe('张三')
    expect(result.merchantName).toBe('商家 2')
    expect(result.items[0]).toMatchObject({ productId: 1, skuId: 3, name: '智选 Pro 5G', image: '/p1.jpg', price: 4999 })
  })

  it('maps form and query fields to backend request names', () => {
    expect(normalizers.normalizeProductQuery?.({ sort: 'price_asc' })).toEqual({ sort: 'priceAsc' })
    expect(normalizers.normalizeMerchantApplication?.({ merchantName: '悦选店', contactPhone: '13800000000', description: '好店', licenseImage: '/logo.jpg' }))
      .toEqual({ shopName: '悦选店', contactPhone: '13800000000', description: '好店', shopLogo: '/logo.jpg' })
    expect(normalizers.normalizeReturnLogistics?.({ company: '顺丰', trackingNo: 'SF001' }))
      .toEqual({ logisticsCompany: '顺丰', logisticsNo: 'SF001' })
  })
})
