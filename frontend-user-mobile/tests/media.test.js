import { describe, expect, it } from 'vitest'

describe('resolveMediaUrl', () => {
  it('resolves portable media paths and preserves browser-local URLs', async () => {
    let media = {}
    try {
      media = await import('../src/utils/media.js')
    } catch {
      // The first RED run intentionally reaches this assertion before the module exists.
    }

    expect(media.resolveMediaUrl).toBeTypeOf('function')
    expect(media.resolveMediaUrl('/media/products/a.jpg', 'http://localhost:8080'))
      .toBe('http://localhost:8080/media/products/a.jpg')
    expect(media.resolveMediaUrl('/upload/20260824/a.webp', 'http://server'))
      .toBe('http://server/upload/20260824/a.webp')
    expect(media.resolveMediaUrl('https://example.com/a.jpg', 'http://server'))
      .toBe('https://example.com/a.jpg')
    expect(media.resolveMediaUrl('data:image/png;base64,a', 'http://server'))
      .toBe('data:image/png;base64,a')
    expect(media.resolveMediaUrl('', 'http://server'))
      .toBe('http://server/media/common/placeholder.webp')
  })
})
