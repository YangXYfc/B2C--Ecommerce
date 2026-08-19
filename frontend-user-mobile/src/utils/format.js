export function formatPrice(value) {
  const amount = Number(value || 0)
  return Number.isInteger(amount) ? String(amount) : amount.toFixed(2)
}

export function formatSales(value) {
  const count = Number(value || 0)
  if (count < 10000) return String(count)
  return `${(count / 10000).toFixed(1).replace(/\.0$/, '')}万`
}

export function formatDate(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
