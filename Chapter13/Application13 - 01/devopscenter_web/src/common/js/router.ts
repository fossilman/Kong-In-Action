import VueRouter from 'vue-router';


/**
 * 路由前卫，基本设置
 */
export default function configRouter(router: VueRouter): void {
    router.beforeEach((to, from, next) => {
        // if (!isLogin() && to.meta.isLogin) {
        //   router.push({path: '/login'});
        //   return;
        // } else {
        //   if (to.name === 'login' && isLogin() ) {
        //     router.push({path: '/'});
        //   } else {
        //     matomo.swiperURL(to.path, to.meta.title);
        //   }
        // }

        // // 访问权限控制
        // const name: string = to.name ? to.name : '';
        // if (name) {
        //   if (!routerAuthor(name)) {
        //     router.push({path: '/notfound'});
        //   } else {
        //     matomo.swiperURL(to.path, to.meta.title);
        //     next();
        //   }
        // }

        next();
    });

}
