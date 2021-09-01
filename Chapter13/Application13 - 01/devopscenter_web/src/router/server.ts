import layout from '../layout/Index.vue';
import { RouteConfig } from 'vue-router';

export const server: RouteConfig = {
  path: '/server', // 服务器
  component: layout,
  meta: {
    isLogin: true
  },
  children: [
    {
      path: '',
      redirect: '/server/list'
    },
    {
      path: 'list', // 服务器列表
      name: 'serversList',
      meta: {
        isLogin: true,
        title: '服务器列表'
      },
      component: () => import(/* webpackChunkName: "servers" */ '../views/Server/List/Index.vue')
    },
    {
      path: 'edit', // 服务器列表编辑
      name: 'serversEdit',
      meta: {
        isLogin: true,
        title: '服务器编辑'
      },
      component: () => import(/* webpackChunkName: "servers" */ '../views/Server/Edit/Index.vue')
    },
    {
      path: 'save', // 服务器列表添加
      name: 'serversSave',
      meta: {
        isLogin: true,
        title: '添加服务器'
      },
      component: () => import(/* webpackChunkName: "servers" */ '../views/Server/Save/Index.vue')
    }
  ]
};
