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
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '统计看板' },
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/admin/UserList.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'merchants',
        name: 'MerchantAudit',
        component: () => import('@/views/admin/MerchantAudit.vue'),
        meta: { title: '商家审核' },
      },
      {
        path: 'products',
        name: 'ProductAudit',
        component: () => import('@/views/admin/ProductAudit.vue'),
        meta: { title: '商品审核' },
      },
      {
        path: 'refunds',
        name: 'RefundArbitrate',
        component: () => import('@/views/admin/RefundArbitrate.vue'),
        meta: { title: '退款仲裁' },
      },
      {
        path: 'banners',
        name: 'BannerManage',
        component: () => import('@/views/admin/BannerManage.vue'),
        meta: { title: '轮播图管理' },
      },
      {
        path: 'logs',
        name: 'OperationLog',
        component: () => import('@/views/admin/OperationLog.vue'),
        meta: { title: '操作日志' },
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
