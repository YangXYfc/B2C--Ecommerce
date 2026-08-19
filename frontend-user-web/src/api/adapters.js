function parseAddressSnapshot(value) {
  if (!value || typeof value === 'object') return value || {}
  try { return JSON.parse(value) } catch { return { detail: value } }
}

export function normalizeCart(value = {}) {
  return {
    items: (value.items || []).map((item) => ({
      id: item.id,
      skuId: item.skuId,
      productId: item.productId,
      productName: item.productName,
      skuName: item.skuName,
      productImage: item.imageUrl,
      price: item.unitPrice,
      stock: item.stock,
      quantity: item.quantity,
      selected: Boolean(item.selected),
      merchantId: item.merchantId,
      merchantName: item.merchantName,
    })),
    totalAmount: value.selectedAmount || 0,
  }
}

export function normalizeProduct(value = {}) {
  return { ...value, skus: (value.skus || []).map((sku) => ({ ...sku, name: sku.skuName })) }
}

export function normalizeOrder(value = {}) {
  return {
    ...value,
    merchantName: value.merchantName || `商家 ${value.merchantId}`,
    addressSnapshot: parseAddressSnapshot(value.addressSnapshot),
    items: (value.items || []).map((item) => ({ ...item, price: item.unitPrice, image: item.productImage })),
  }
}

export function normalizeOrders(value = {}) {
  const page = value.records ? value : { records: value.list || [], total: value.total || 0 }
  return { ...page, records: page.records.map(normalizeOrder) }
}

export function normalizeReviews(value = {}) {
  const rows = value.records || value.list || value || []
  return rows.map((review) => ({ ...review, nickname: review.nickname || `用户${review.userId}` }))
}

export function normalizeMerchantApplication(value = {}) {
  return {
    shopName: value.shopName,
    contactPhone: value.contactPhone,
    description: value.description,
    shopLogo: value.shopLogo,
  }
}
