function clone(value) {
  return value == null ? value : JSON.parse(JSON.stringify(value))
}

export function createMemoryStorage() {
  const values = new Map()
  return {
    get(key, fallback = null) { return values.has(key) ? clone(values.get(key)) : clone(fallback) },
    set(key, value) { values.set(key, clone(value)) },
    remove(key) { values.delete(key) },
  }
}

export function createUniStorage(prefix = 'yuexuan') {
  return {
    get(key, fallback = null) {
      const value = uni.getStorageSync(`${prefix}:${key}`)
      return value === '' || value == null ? clone(fallback) : clone(value)
    },
    set(key, value) { uni.setStorageSync(`${prefix}:${key}`, clone(value)) },
    remove(key) { uni.removeStorageSync(`${prefix}:${key}`) },
  }
}
