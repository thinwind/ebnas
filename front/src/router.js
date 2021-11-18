import BasicLayout from './layouts/BasicLayout'
import RouteView from './layouts/RouteView'
import NotFound from './views/404'
import Forbidden from './views/403'

const routers = [
    {
        path: '/',
        component: BasicLayout,
        children: [
            
            {
                path: '/',
                name: 'from',
                meta: { title: "集群管理", icon: "ios-people"},
                component: RouteView,
                children: [
                    {
                        path: '/',
                        name: 'basic',
                        meta: { title: "集群节点"},
                        component: (resolve) => require(['./views/form/Detail.vue'], resolve)
                    }
                ]
            },
        ]
    },
    {
        path: '/403',
        name: '403',
        hideInMenu: true,
        component: Forbidden
    },
    {
        path: '*',
        name: '404',
        hideInMenu: true,
        component: NotFound
    },
    // {
    //     path: '/',
    //     meta: {
    //         title: ''
    //     },
    //     component: (resolve) => require(['./views/index.vue'], resolve)
    // }
];



export default routers;