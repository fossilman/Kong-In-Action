import layout from '../layout/Index.vue';
import { RouteConfig } from 'vue-router';

export const project: RouteConfig = {
  path: '/project',
  component: layout,
  meta: {
    isLogin: true
  },
  children: [
    {
      path: '',
      redirect: '/project/index' // 首页
    },
    {
      path: 'index',
      name: 'homeList',
      meta: {
        isLogin: true,
        title: '项目列表'
      },
      component: () => import(/* webpackChunkName: "project" */ '../views/Project/List/Index.vue')
    },
    {
      path: 'save', // 添加项目
      name: 'homeSave',
      meta: {
        isLogin: true,
        title: '添加项目'
      },
      component: () => import(/* webpackChunkName: "project" */ '../views/Project/Save/Index.vue')
    }
  ]
};
