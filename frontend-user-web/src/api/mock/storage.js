import {
  mockUsers,
  getInitialAddresses,
  getInitialCart,
  getInitialOrders,
  getInitialOrderItems,
  getInitialRefunds,
} from './data'

const STORAGE_KEY = 'jd_ecommerce_mock_state'

let nextId = 1000

export function nextAutoId() {
  nextId += 1
  return nextId
}

function loadState() {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (raw) {
    try {
      return JSON.parse(raw)
    } catch {
      // ignore
    }
  }
  return null
}

export function getMockState() {
  const existing = loadState()
  if (existing) return existing

  const state = {
    users: [...mockUsers],
    addresses: getInitialAddresses(),
    cart: getInitialCart(),
    orders: getInitialOrders(),
    orderItems: getInitialOrderItems(),
    refunds: getInitialRefunds(),
    reviews: [],
    merchantApplications: [],
    currentUserId: null,
  }
  saveState(state)
  return state
}

export function saveState(state) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
}

export function persist(state) {
  saveState(state)
}

export function getCurrentUserId() {
  const token = localStorage.getItem('jd_token')
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.userId
  } catch {
    return getMockState().currentUserId
  }
}

export function createToken(user) {
  const payload = { userId: user.id, username: user.username, role: user.role }
  const header = btoa(JSON.stringify({ alg: 'mock', typ: 'JWT' }))
  const body = btoa(JSON.stringify(payload))
  return `${header}.${body}.mock-signature`
}

export function ok(data) {
  return Promise.resolve(data)
}

export function fail(message) {
  return Promise.reject(new Error(message))
}

export function requireLogin() {
  const userId = getCurrentUserId()
  if (!userId) {
    throw new Error('请先登录')
  }
  return userId
}

export function delay(ms = 300) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
