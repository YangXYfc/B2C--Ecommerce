import { test, expect } from '@playwright/test'

const baseUrl = process.env.E2E_BASE_URL || 'http://127.0.0.1:5173'

test.use({ viewport: { width: 390, height: 844 }, locale: 'zh-CN' })

test('consumer can search, open a product, add it to cart, and reach checkout', async ({ page }) => {
  await page.goto(`${baseUrl}/#/pages/auth/login`)
  const loginResponse = page.waitForResponse((response) => response.url().endsWith('/api/auth/login'))
  await page.locator('.primary-button').click()
  expect((await loginResponse).ok()).toBe(true)
  await expect(page).toHaveURL(/pages\/profile\/index/)

  await page.goto(baseUrl)
  await expect(page.getByText('发现你的生活好物')).toBeVisible()

  await page.locator('.search input').fill('手机')
  await page.locator('.search-action').click()
  await expect(page).toHaveURL(/pages\/product\/list/)

  await expect(page.locator('.product').first()).toBeVisible()
  await page.locator('.product').first().click()
  await expect(page).toHaveURL(/pages\/product\/detail/)

  const addCartResponse = page.waitForResponse((response) => response.url().endsWith('/api/cart/items') && response.request().method() === 'POST')
  await page.locator('.secondary-button').click()
  expect((await addCartResponse).ok()).toBe(true)

  await page.goto(`${baseUrl}/#/pages/cart/index`)
  await expect(page.getByText('数码旗舰店', { exact: true })).toBeVisible()
  await page.locator('.bar .primary-button').click()

  await expect(page).toHaveURL(/pages\/checkout\/index/)
  await expect(page.getByText('提交订单', { exact: true })).toBeVisible()
})
