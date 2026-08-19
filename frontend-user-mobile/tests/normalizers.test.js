import { describe, expect, it } from 'vitest'
import { normalizePage, unwrapResult } from '../src/api/normalizers.js'

describe('API normalizers', () => {
  it('unwraps successful backend responses', () => {
    expect(unwrapResult({ code: 200, data: { id: 1 }, message: 'ok' })).toEqual({ id: 1 })
  })

  it('normalizes both records and list pagination shapes', () => {
    expect(normalizePage({ records: [{ id: 1 }], total: 1 })).toEqual({ records: [{ id: 1 }], total: 1 })
    expect(normalizePage({ list: [{ id: 2 }], total: 1 })).toEqual({ records: [{ id: 2 }], total: 1 })
  })
})
