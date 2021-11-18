import Vue from 'vue';
import ViewUI from 'view-design';
import VueRouter from 'vue-router';
import Routers from './router';
import Util from './libs/util';
import App from './app.vue';
import 'view-design/dist/styles/iview.css';
import findLast from 'lodash/findLast';
import { check, isLogin } from './utils/auth'; 
import { Notice } from 'view-design';
import Authorized from './components/Authorized';

Vue.use(ViewUI);
Vue.use(VueRouter);
Vue.component('Authorized', Authorized);

// 路由配置
const RouterConfig = {
    mode: 'history',
    routes: Routers
};
const router = new VueRouter(RouterConfig);

router.beforeEach((to, from, next) => {
    ViewUI.LoadingBar.start();
    Util.title(to.meta.title);
    const record = findLast(to.matched, record => record.meta.authority);
    if(record && !check(record.meta.authority)){
        if(!isLogin() && to.path !== '/user/login'){
            next({
                path: '/user/login'
            })
        } else if(to.path !== '/403') {
            Notice.error({
                title: '403',
                desc: '没有权限，请联系管理员'
            });
            next({
                path: '/403'
            });
        }
        ViewUI.LoadingBar.finish();
    }
    next();
});

router.afterEach((to, from, next) => {
    ViewUI.LoadingBar.finish();
    window.scrollTo(0, 0);
});

new Vue({
    el: '#app',
    router: router,
    render: h => h(App)
});
