export function unwrapResult(response) {
  if (response == null) return response
  if (Object.prototype.hasOwnProperty.call(response, 'code')) {
    if (Number(response.code) !== 200 && response.code !== 'SUCCESS') throw new Error(response.message || '请求失败')
    return response.data
  }
  return response.data ?? response
}

export function normalizePage(value = {}) {
  return {
    records: value.records || value.list || [],
    total: Number(value.total || 0),
  }
}

export function normalizeCart(value = {}) {
  return (value.items || []).map((item) => ({
    id: item.id,
    skuId: item.skuId,
    productId: item.productId,
    productName: item.productName,
    skuName: item.skuName,
    productImage: item.imageUrl,
    price: item.unitPrice,
    stock: item.stock,
    quantity: item.quantity,
    selected: item.selected ? 1 : 0,
    merchantId: item.merchantId,
    merchantName: item.merchantName,
  }))
}

export function normalizeProduct(value = {}) {
  return {
    ...value,
    skus: (value.skus || []).map((sku) => ({ ...sku, name: sku.skuName })),
  }
}

function parseAddressSnapshot(value) {
  if (!value || typeof value === 'object') return value || {}
  try { return JSON.parse(value) } catch { return { detail: value } }
}

export function normalizeOrder(value = {}) {
  return {
    ...value,
    merchantName: value.merchantName || `商家 ${value.merchantId}`,
    address: parseAddressSnapshot(value.addressSnapshot),
    items: (value.items || []).map((item) => ({
      ...item,
      name: item.productName,
      image: item.productImage,
      price: item.unitPrice,
    })),
  }
}

export function normalizeProductQuery(query = {}) {
  const sortMap = { price_asc: 'priceAsc', price_desc: 'priceDesc' }
  return { ...query, sort: sortMap[query.sort] || query.sort || undefined }
}

export function normalizeMerchantApplication(value = {}) {
  return {
    shopName: value.shopName || value.merchantName,
    contactPhone: value.contactPhone,
    description: value.description,
    shopLogo: value.shopLogo || value.licenseImage,
  }
}

export function normalizeReturnLogistics(value = {}) {
  return {
    logisticsCompany: value.logisticsCompany || value.company,
    logisticsNo: value.logisticsNo || value.trackingNo,
  }
}
