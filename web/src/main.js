import Vue from 'vue'
import App from './App.vue'
import Axios from 'axios'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import eventManager from './js/eventManager'
import Constant from "@/js/Constant"

Vue.config.productionTip = false
const axios = Axios.create({
    baseURL: process.env.NODE_ENV !== 'production' ? '/api/' : '',
    crossDomain: true,
})
// 如果是开发环境，则加上api,触发代理
axios.interceptors.response.use(
    response => {
        if (response.data === Constant.GO_LOGIN) {
            eventManager.emit(Constant.GO_LOGIN)
            return Promise.reject("you should login")
        }
        return response;
    },
    error => {
        return Promise.reject(error.response.data)
        // 返回接口返回的错误信息
    })
Vue.prototype.axios = axios

Vue.use(ElementUI);
new Vue({
    render: h => h(App),
}).$mount('#app')
