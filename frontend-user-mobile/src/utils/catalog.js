export function buildProductQuery(filters) {
  return Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== '' && value != null))
}

export function selectAvailableSku(skus = []) {
  return skus.find((sku) => Number(sku.stock) > 0) || skus[0] || null
}
