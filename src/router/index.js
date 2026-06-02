import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/search', // 默认打开检索页
    children: [
      {
        path: 'search',
        name: 'Search',
        component: () => import('../components/Search.vue'),
        meta: { title: '配件检索' }
      },
      {
        path: 'manage',
        name: 'Manage',
        component: () => import('../components/Manage.vue'),
        meta: { title: '配件管理' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('../components/Stats.vue'),
        meta: { title: '数据统计与分析' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router