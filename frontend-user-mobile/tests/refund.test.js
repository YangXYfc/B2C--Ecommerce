import { describe, expect, it } from 'vitest'
import { getRefundActions, getRefundStatus } from '../src/utils/refund.js'

describe('refund presentation', () => {
  it('maps refund status and next actions', () => {
    expect(getRefundStatus(0).text).toBe('待商家审核')
    expect(getRefundActions(2)).toEqual(['logistics'])
    expect(getRefundActions(5)).toEqual(['appeal'])
    expect(getRefundActions(6)).toEqual([])
  })
})
