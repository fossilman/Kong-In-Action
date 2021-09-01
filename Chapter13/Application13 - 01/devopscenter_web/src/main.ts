import Vue from 'vue';
import App from './App.vue';

import { router } from './router/index';
import store from './store';
import routerCofing from '@/common/js/router';
import '@/common/js/filter.ts';


import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/common/sass/index.scss';

Vue.config.productionTip = false;

Vue.use(ElementUI);

routerCofing(router);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
