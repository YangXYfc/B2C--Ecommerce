import { test, expect } from '@playwright/test'
import { unlink } from 'node:fs/promises'
import { resolve } from 'node:path'
import { MerchantProductPage } from './pages/MerchantProductPage.js'
import { blockExternalImages } from './support/network.js'

const merchantBaseUrl = process.env.MERCHANT_E2E_BASE_URL || 'http://localhost:5175'
const testPng = Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wl2nQAAAABJRU5ErkJggg==', 'base64')
let uploadedRelativeUrl = ''

test.afterEach(async () => {
  if (!uploadedRelativeUrl.startsWith('/upload/')) return
  const uploadFile = resolve(process.cwd(), '..', 'backend', uploadedRelativeUrl.replace(/^\//, ''))
  await unlink(uploadFile).catch((error) => {
    if (error.code !== 'ENOENT') throw error
  })
  uploadedRelativeUrl = ''
})

test('merchant uploads a local image and submits its relative path', async ({ page }) => {
  await blockExternalImages(page)
  const productPage = new MerchantProductPage(page, merchantBaseUrl)
  const loginResponse = productPage.login('merchant1', '123456')
  expect((await loginResponse).ok()).toBe(true)
  await expect(page).toHaveURL(/dashboard/)

  await productPage.openCreateForm()
  await expect(page.getByRole('heading', { name: '发布商品' })).toBeVisible()

  const uploadResponse = productPage.uploadMainImage({
    name: 'playwright-product.png',
    mimeType: 'image/png',
    buffer: testPng,
  })
  const uploaded = await uploadResponse
  expect(uploaded.ok()).toBe(true)
  uploadedRelativeUrl = (await uploaded.json()).data.url
  expect(uploadedRelativeUrl).toMatch(/^\/upload\//)

  const preview = productPage.mainUploader.getByRole('img')
  await expect(preview).toHaveAttribute('src', /\/upload\//)
  expect(await preview.evaluate((image) => image.naturalWidth)).toBeGreaterThan(0)

  await productPage.fillMinimumProduct('Playwright 本地图片测试商品')

  let submittedPayload
  await page.route('**/api/merchant/products', async (route) => {
    if (route.request().method() !== 'POST') return route.continue()
    submittedPayload = route.request().postDataJSON()
    return route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ code: 200, message: '创建成功', data: { id: 99999 } }),
    })
  })

  await productPage.submit()
  await expect(page).toHaveURL(/products$/)
  expect(submittedPayload.mainImage).toBe(uploadedRelativeUrl)
})
