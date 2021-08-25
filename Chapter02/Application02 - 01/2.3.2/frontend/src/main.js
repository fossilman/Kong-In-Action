import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import axios from 'axios';
import App from './App.vue';

const http = axios.create({
  // 如果是在Mac本地运行，修改地址为127.0.0.1
  baseURL: process.env.BACKEND_URL ? process.env.BACKEND_URL : 'http://127.0.0.1:8000/nginx/gateway/todos',
  // 如果在Linux系统中运行时，请填写公网IP。例如：106.14.248.21
  // baseURL: process.env.BACKEND_URL ? process.env.BACKEND_URL : 'http://106.14.248.21:8000/nginx/gateway/todos',
});

Vue.prototype.$http = http;


Vue.use(BootstrapVue);

Vue.config.productionTip = false;

new Vue({
  render: (h) => h(App),
}).$mount('#app');
