import { RouteConfig } from 'vue-router';
import layout from '../layout/Index.vue';
import { router } from '.';
import { Message } from 'element-ui';
import { tablist } from "@/config/applicationTabConfig";

// 路由path不能有代理配置中的词否则会404
export const application: RouteConfig = {
    path: '/application', // application
    component: layout,
    meta: {
        isLogin: true
    },
    children: [
        {
            path: '/application/:application/id/:id/:type', // 配置中心
            meta: {
                isLogin: true
            },
            component: () => import(/* webpackChunkName: "application" */ '@/views/Application/Index.vue'),
            children: [
                {
                    path: '',
                    redirect: tablist[0].path
                },
                ...tablist
            ]
        }
    ],
    beforeEnter: (to, from, next) => {
        if (
            typeof to.params.application === "string" &&
            to.params.application !== ""
        ) {
            if (
                Number.isFinite(parseInt(to.params.id))
            ) {
                next();
            } else {
                showMsg('id');
            }
        } else {
            showMsg('application');
        }
        function showMsg(name: string): void {
            Message({
                message: `缺少${name}参数`,
                type: 'error',
                onClose: () => {
                    router.back();
                }
            });
        }
    }
};
