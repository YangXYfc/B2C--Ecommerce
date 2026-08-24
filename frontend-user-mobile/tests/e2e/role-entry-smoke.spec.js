import { test, expect } from '@playwright/test'
import { blockExternalImages } from './support/network.js'

const consumerWebUrl = process.env.CONSUMER_WEB_E2E_BASE_URL || 'http://localhost:5173'
const adminUrl = process.env.ADMIN_E2E_BASE_URL || 'http://localhost:5176'

test('consumer Web entry renders its brand and real catalog categories', async ({ page }) => {
  await blockExternalImages(page)
  await page.goto(consumerWebUrl)

  await expect(page).toHaveTitle('首页 - 悦选生活')
  await expect(page.locator('.logo')).toContainText('悦选生活')
  await expect(page.getByRole('heading', { name: '商品分类' })).toBeVisible()
  await expect(page.getByText('手机数码', { exact: true })).toBeVisible()
  await expect(page.getByText('家用电器', { exact: true })).toBeVisible()
})

test('consumer Web maps the real product page response to product cards', async ({ page }) => {
  test.fail(true, 'A 端读取 records，但真实商品分页字段为 list；缺陷修复后应变为 unexpected pass')
  await blockExternalImages(page)
  const catalogResponsePromise = page.waitForResponse((response) => {
    const url = new URL(response.url())
    return url.pathname === '/api/products' && response.request().method() === 'GET'
  })
  await page.goto(consumerWebUrl)

  const catalogResponse = await catalogResponsePromise
  expect(catalogResponse.ok()).toBe(true)
  const catalogBody = await catalogResponse.json()
  expect(catalogBody.data.list.length).toBeGreaterThan(0)
  await expect(page.locator('.product-card').first()).toBeVisible()

  const productImage = page.locator('.product-card img').first()
  await expect(productImage).toHaveAttribute('src', /\/media\//)
  expect(await productImage.evaluate((image) => image.naturalWidth)).toBeGreaterThan(0)

  await page.getByPlaceholder('搜索商品').fill('手机')
  const productResponse = page.waitForResponse((response) =>
    response.url().includes('/api/products') && response.request().method() === 'GET')
  await page.getByRole('button', { name: '搜索' }).click()
  expect((await productResponse).ok()).toBe(true)
  await expect(page).toHaveURL(/\/products\?keyword=/)
  await expect(page.locator('.product-card').first()).toBeVisible()

  await page.locator('.product-card').first().click()
  await expect(page).toHaveURL(/\/products\/\d+/)
  await expect(page.getByRole('button', { name: '加入购物车' })).toBeVisible()
})

test('platform administrator logs in and sees the real dashboard', async ({ page }) => {
  await page.goto(`${adminUrl}/#/dashboard`)
  await expect(page).toHaveURL(/#\/login/)
  await expect(page.getByText('悦选生活平台管理')).toBeVisible()

  await page.getByPlaceholder('用户名').fill('admin')
  await page.getByPlaceholder('密码').fill('123456')
  const loginResponse = page.waitForResponse((response) => response.url().endsWith('/api/auth/login'))
  await page.getByRole('button', { name: '登 录' }).click()
  expect((await loginResponse).ok()).toBe(true)

  await expect(page).toHaveURL(/#\/dashboard/)
  await expect(page.getByText('悦选生活平台管理')).toBeVisible()
  await expect(page.getByText('平台用户数')).toBeVisible()
  await expect(page.getByText('入驻商家数')).toBeVisible()
  await expect(page.getByText('总订单数')).toBeVisible()
  await expect(page.getByText('总销售额')).toBeVisible()
})
