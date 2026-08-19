export function unwrapResult(response) {
  if (response == null) return response
  if (Object.prototype.hasOwnProperty.call(response, 'code')) {
    if (Number(response.code) !== 200) throw new Error(response.message || '请求失败')
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
