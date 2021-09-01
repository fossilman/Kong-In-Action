import { RouteConfigSingleView } from 'vue-router/types/router';

export interface TabList extends RouteConfigSingleView {
    tabName: string;
}

// 注：数组顺序就是tab显示顺序
const tablist: TabList[] = [
    {
        path: 'buildDetail', // build详情
        name: 'buildDetail',
        tabName: 'Build详情',
        meta: {
            isLogin: true,
            title: 'Build详情'
        },
        component: () => import(/* webpackChunkName: "buildDetail" */ '@/views/Project/BuildDetail/Index.vue')
    },
    {
        path: 'deployDetail', // deploy详情
        name: 'deployDetail',
        tabName: 'Deploy详情',
        meta: {
            isLogin: true,
            title: 'deploy详情'
        },
        component: () => import(/* webpackChunkName: "deployDetail" */ '@/views/Project/DeployDetail/Index.vue')
    },
    {
        path: 'routingCenter', // deploy详情
        name: 'routing',
        tabName: '路由中心',
        meta: {
            isLogin: true,
            title: 'routing Center'
        },
        component: () => import(/* webpackChunkName: "deployDetail" */ '@/views/RoutingCenter/Index.vue')
    }
];

export { tablist };
