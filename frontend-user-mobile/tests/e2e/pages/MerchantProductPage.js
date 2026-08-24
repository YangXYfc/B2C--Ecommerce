export class MerchantProductPage {
  constructor(page, baseUrl) {
    this.page = page
    this.baseUrl = baseUrl
    this.mainUploader = page.getByTestId('product-main-uploader')
  }

  async login(username, password) {
    await this.page.goto(`${this.baseUrl}/#/login`)
    await this.page.getByPlaceholder('用户名').fill(username)
    await this.page.getByPlaceholder('密码').fill(password)
    const response = this.page.waitForResponse((value) => value.url().endsWith('/api/auth/login'))
    await this.page.getByRole('button', { name: '登 录' }).click()
    return response
  }

  async openCreateForm() {
    await this.page.goto(`${this.baseUrl}/#/products/create`)
  }

  async uploadMainImage(file) {
    const response = this.page.waitForResponse((value) =>
      value.url().endsWith('/api/files/images') && value.request().method() === 'POST')
    await this.mainUploader.locator('input[type="file"]').setInputFiles(file)
    return response
  }

  async fillMinimumProduct(name) {
    await this.page.getByPlaceholder('请输入商品名称').fill(name)
    await this.page.getByRole('combobox', { name: /分类/ }).press('ArrowDown')
    await this.page.getByRole('option').filter({ hasText: '智能手机' }).click()
    await this.page.getByRole('textbox', { name: '规格名称' }).fill('测试规格')
    await this.page.getByRole('spinbutton', { name: '售价' }).fill('99')
  }

  async submit() {
    await this.page.getByRole('button', { name: '发布', exact: true }).click()
  }
}
