import Vue from 'vue';
import Router from 'vue-router';

import { server } from './server';
import { project } from './project';
import { application } from './application';

// 屏蔽相同路由时报错
const originalPush: any = Router.prototype.push;
Router.prototype.push = function push(location: any): any {
  // @ts-ignore
  return originalPush.call(this, location).catch(err => err);
};

Vue.use(Router);

export const router: Router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      redirect: '/project'
    },
    project,
    server,
    application,
    {
      path: '/notfound',
      name: 'notFound',
      meta: {
        title: '权限'
      },
      component: () => import(/* webpackChunkName: "notFound" */ '../views/NotFound/Index.vue')
    },
    {
      path: '*',
      name: 'notFound',
      component: () => import(/* webpackChunkName: "notFound" */ '../views/NotFound/Index.vue')
    }
  ]
});
