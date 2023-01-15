import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false

// 如果是开发环境，则加上api,触发代理
Vue.prototype.axios = axios.create({
  baseURL: process.env.NODE_ENV !== 'production' ? '/api/' : '',
  crossDomain: true,
});

Vue.use(ElementUI);
new Vue({
  render: h => h(App),
}).$mount('#app')
