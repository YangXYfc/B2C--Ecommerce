const fallbackMediaPath = '/media/common/placeholder.webp'

export function resolveMediaUrl(value, baseURL = import.meta.env.VITE_API_BASE_URL || '') {
  const path = typeof value === 'string' && value.trim() ? value.trim() : fallbackMediaPath
  if (/^(https?:|data:|blob:)/i.test(path)) return path

  const normalizedPath = `/${path.replace(/^\/+/, '')}`
  const normalizedBase = String(baseURL || '').replace(/\/+$/, '')
  return normalizedBase ? `${normalizedBase}${normalizedPath}` : normalizedPath
}

export { fallbackMediaPath }
