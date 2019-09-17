const routes = [
  {
    path: '/',
    component: '../layouts/BlankLayout',
    routes: [
      {
        path: '/user',
        component: '../layouts/UserLayout',
        routes: [
          {
            path: '/user',
            redirect: '/user/login',
          },
          {
            name: 'login',
            path: '/user/login',
            component: './user/login',
          },
        ],
      },
      {
        path: '/',
        component: '../layouts/BasicLayout',
        Routes: ['src/pages/Authorized'],
        routes: [
          {
            path: '/',
            name: 'welcome',
            // component: './Welcome',
            redirect: '/service'
          },
          {
            path: '/auth',
            name: 'auth',
            component: './auth/index',
            routes: [
              {
                path: '/auth/user',
                name: 'user',
                component: './auth/user/index',
              },
              // {
              //   path: '/auth/user/edit',
              //   name: 'user',
              //   component: './auth/user/Edit',
              // },
              // {
              //   path: '/auth/role',
              //   name: 'role',
              //   component: './auth/role/index',
              // },
              // {
              //   path: '/auth/role/edit',
              //   name: 'user',
              //   component: './auth/role/RoleEdit',
              // },
              // {
              //   path: '/auth/menu',
              //   name: 'menu',
              //   component: './auth/menu/index',
              // },
              // {
              //   path: '/auth/menu/edit',
              //   name: 'user',
              //   component: './auth/menu/MenuEdit',
              // },
            ],
          },
          {
            path: '/service',
            name: 'service',
            component: './service/index',
          },
          {
            path: '/service/add',
            name: 'serviceAdd',
            component: './service/addService/index'
          },
          {
            path: '/service/detail',
            name: 'serviceDetail',
            component: './service/details',
          },
          {
            path: '/project',
            name: 'project',
            component: './project/index',
          },
          {
            path: '/project/detail',
            name: 'projectDetail',
            component: './project/details',
          },
          {
            path: '/environment',
            name: 'env',
            component: './env/index',
          },
          {
            path: '/cluster',
            name: 'cluster',
            component: './cluster/index',
          },
          {
            path: '/cluster/detail',
            name: 'clusterDetail',
            component: './cluster/detail',
          },
          {
            path: '/system',
            name: 'system',
            component: './system/index',
          },
          {
            path: '/exception403',
            name: 'exception403',
            component: './Exception/403',
          },
        ],
      },
    ],
  },
];

export default routes;
