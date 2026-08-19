export function calculateSelectedTotal(items = []) {
  return items.filter((item) => Number(item.selected) === 1)
    .reduce((total, item) => total + Number(item.price) * Number(item.quantity), 0)
}

export function groupItemsByMerchant(items = []) {
  const groups = new Map()
  for (const item of items) {
    const id = Number(item.merchantId)
    if (!groups.has(id)) groups.set(id, { id, name: item.merchantName, items: [] })
    groups.get(id).items.push(item)
  }
  return [...groups.values()]
}

export function normalizeQuantity(quantity, stock) {
  return Math.min(Math.max(1, Number(quantity) || 1), Math.max(1, Number(stock) || 1))
}
