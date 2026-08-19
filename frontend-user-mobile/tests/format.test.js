import { describe, expect, it } from 'vitest'
import { formatPrice, formatSales } from '../src/utils/format.js'

describe('formatPrice', () => {
  it('keeps two decimals only when needed', () => {
    expect(formatPrice(4999)).toBe('4999')
    expect(formatPrice(39.9)).toBe('39.90')
  })
})

describe('formatSales', () => {
  it('uses Chinese ten-thousand units for large counts', () => {
    expect(formatSales(9800)).toBe('9800')
    expect(formatSales(15200)).toBe('1.5万')
  })
})
