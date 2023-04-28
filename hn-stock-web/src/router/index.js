import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import Layout from "@/components/layout"

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: {title: 'Dashboard', icon: 'el-icon-menu'},
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/pages/dashboard/index'),
      meta: {title: 'Dashboard', icon: 'el-icon-menu'}
    }]
  },

  {
    path: '/indiv',
    component: Layout,
    redirect: '/indiv/market',
    name: '个股',
    meta: {title: '个股', icon: 'el-icon-s-help'},
    children: [
      {
        path: 'market',
        name: '行情',
        component: () => import('@/pages/indiv/market'),
        meta: {title: '行情', icon: 'el-icon-s-grid'},
        children: [
          {
            path: 'center',
            name: '行情中心',
            component: () => import('@/pages/indiv/market/center'),
            meta: {title: '行情中心', icon: 'el-icon-s-grid'},
          },
          {
            path: 'indivMsg',
            name: '个股信息',
            component: () => import('@/pages/indiv/market/indivMsg'),
            meta: {title: '个股信息', icon: 'el-icon-s-grid'},
          },
          {
            path: 'shareChart',
            name: '分时图',
            component: () => import('@/pages/indiv/market/shareChart'),
            meta: {title: '分时图', icon: 'el-icon-share'},
          },
          {
            path: 'ChartLine',
            name: 'K线图',
            component: () => import('@/pages/indiv/market/ChartLine'),
            meta: {title: 'K线图', icon: 'el-icon-s-grid'},
          }
        ]
      },
      {
        path: 'analysis',
        name: '分析',
        component: () => import('@/pages/indiv/analysis'),
        meta: {title: '分析', icon: 'el-icon-s-grid'},
        children: [
          {
            path: 'kChart',
            name: 'k线图',
            component: () => import('@/pages/indiv/analysis/kChart'),
            meta: {title: 'k线图', icon: 'el-icon-s-grid'},
          },
          {
            path: 'volratio',
            name: '量比',
            component: () => import('@/pages/indiv/analysis/volratio'),
            meta: {title: '量比', icon: 'el-icon-s-grid'},
          }
        ]
      }
    ]
  },
  {
    path: 'external-link',
    component: Layout,
    meta: {title: 'External Link', icon: 'link'},
    children: [
      {
        path: 'https://panjiachen.github.io/vue-element-admin-site/#/',
        meta: {title: 'External Link'}
      }
    ]
  }
]

const router = new Router({
  routes,
})


export {router, routes}
