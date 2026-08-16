import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
      { path: 'products', name: 'ProductList', component: () => import('@/views/ProductList.vue'), meta: { title: '商品列表' } },
      { path: 'products/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue'), meta: { title: '商品详情' } },
      { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { title: '购物车', auth: true } },
      { path: 'checkout', name: 'Checkout', component: () => import('@/views/Checkout.vue'), meta: { title: '确认订单', auth: true } },
      { path: 'addresses', name: 'AddressList', component: () => import('@/views/AddressList.vue'), meta: { title: '收货地址', auth: true } },
      { path: 'addresses/edit/:id?', name: 'AddressEdit', component: () => import('@/views/AddressEdit.vue'), meta: { title: '编辑地址', auth: true } },
      { path: 'orders', name: 'OrderList', component: () => import('@/views/OrderList.vue'), meta: { title: '我的订单', auth: true } },
      { path: 'orders/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue'), meta: { title: '订单详情', auth: true } },
      { path: 'orders/:id/review', name: 'ReviewCreate', component: () => import('@/views/ReviewCreate.vue'), meta: { title: '发表评价', auth: true } },
      { path: 'refunds', name: 'RefundList', component: () => import('@/views/RefundList.vue'), meta: { title: '退款/售后', auth: true } },
      { path: 'refunds/:id', name: 'RefundDetail', component: () => import('@/views/RefundDetail.vue'), meta: { title: '退款详情', auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心', auth: true } },
      { path: 'password', name: 'Password', component: () => import('@/views/Password.vue'), meta: { title: '修改密码', auth: true } },
    ],
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录', guest: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { title: '注册', guest: true } },
  { path: '/merchant-apply', name: 'MerchantApply', component: () => import('@/views/MerchantApply.vue'), meta: { title: '商家入驻', auth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()

  if (to.meta.auth && !userStore.isLoggedIn) {
    return { name: 'Login', query: { redirect: to.fullPath } }
  }

  if (to.meta.guest && userStore.isLoggedIn) {
    return { name: 'Home' }
  }

  if (userStore.isLoggedIn && !userStore.profile) {
    await userStore.fetchProfile()
  }

  document.title = `${to.meta.title || '商城'} - 京东风格商城`
})

export default router
