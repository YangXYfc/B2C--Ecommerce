import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/merchant/Dashboard.vue'),
        meta: { title: '数据看板' },
      },
      {
        path: 'shop',
        name: 'ShopSettings',
        component: () => import('@/views/merchant/ShopSettings.vue'),
        meta: { title: '店铺设置' },
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/merchant/ProductList.vue'),
        meta: { title: '商品管理' },
      },
      {
        path: 'products/create',
        name: 'ProductCreate',
        component: () => import('@/views/merchant/ProductForm.vue'),
        meta: { title: '发布商品' },
      },
      {
        path: 'products/:id/edit',
        name: 'ProductEdit',
        component: () => import('@/views/merchant/ProductForm.vue'),
        meta: { title: '编辑商品' },
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/merchant/OrderList.vue'),
        meta: { title: '订单管理' },
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/merchant/OrderDetail.vue'),
        meta: { title: '订单详情' },
      },
      {
        path: 'refunds',
        name: 'RefundList',
        component: () => import('@/views/merchant/RefundList.vue'),
        meta: { title: '退款处理' },
      },
      {
        path: 'reviews',
        name: 'ReviewList',
        component: () => import('@/views/merchant/ReviewList.vue'),
        meta: { title: '评价管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
