import { describe, expect, it } from 'vitest'
import { buildProductQuery, selectAvailableSku } from '../src/utils/catalog.js'

describe('catalog helpers', () => {
  it('omits empty filters from product queries', () => {
    expect(buildProductQuery({ keyword: '', categoryId: '', sort: 'sales', page: 1, size: 10 })).toEqual({ sort: 'sales', page: 1, size: 10 })
  })

  it('selects the first SKU that has stock', () => {
    expect(selectAvailableSku([{ id: 1, stock: 0 }, { id: 2, stock: 3 }]).id).toBe(2)
  })
})
