import { routerRedux } from 'dva/router';
import { stringify, parse } from 'qs';
import { setAuthority } from '@/utils/authority';
import { reloadAuthorized } from '@/utils/Authorized';
import { userLogin, userLogout } from '@/services/login';
import { auth } from '@/services/auth';
import menu from '@/pages/auth/menu';
// import { fakeAccountLogin } from '@/services/user';
export function getPageQuery() {
  return parse(window.location.href.split('?')[1]);
}
const Model = {
  namespace: 'login',
  state: {
    status: undefined,
  },
  effects: {
    *login({ payload }, { call, put }) {
      const response = yield call(userLogin, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: {},
      });
      // Login successfully
      if (response && response.code == '1') {
        //获取菜单
        const res = yield call(auth.getUserPermissions);
        //获取用户是否是管理员
        const result = yield call(auth.userIsAdmin);
        //获取用户详细信息
        const userInfo = yield call(auth.usergetPage, { userId: response.data.userId });
        let currentUser = userInfo.data && userInfo.data.rows && userInfo.data.rows[0] || {};
        localStorage.setItem('user', JSON.stringify(currentUser));
        if (result && result.code == '1') {
          localStorage.setItem('isAdmin', result.data ? '1' : '0');
        }
        if (res && res.code == '1') {
          localStorage.setItem('menuList', JSON.stringify(res.data || []));
          yield put(routerRedux.replace('/service'));
        }
      }
      // if (response.status === 'ok') {
      //   reloadAuthorized();
      //   const urlParams = new URL(window.location.href);
      //   const params = getPageQuery();
      //   let { redirect } = params;
      //   if (redirect) {
      //     const redirectUrlParams = new URL(redirect);
      //     if (redirectUrlParams.origin === urlParams.origin) {
      //       redirect = redirect.substr(urlParams.origin.length);
      //       if (redirect.startsWith('/#')) {
      //         redirect = redirect.substr(2);
      //       }
      //     } else {
      //       window.location.href = redirect;
      //       return;
      //     }
      //   }
      //   yield put(routerRedux.replace('/'));
      // }
    },
    *logout({ payload }, { call, put }) {
      // const { redirect } = getPageQuery(); // redirect
      const response = yield call(userLogout, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: {},
      });

      if (response && response.code == '1') {
        localStorage.clear();
        yield put(routerRedux.replace('/user/login'));
      }
      // if (window.location.pathname !== '/user/login' && !redirect) {
      //   yield put(
      //     routerRedux.replace({
      //       pathname: '/user/login',
      //       search: stringify({
      //         redirect: window.location.href,
      //       }),
      //     }),
      //   );
      // }
    },

  },
  reducers: {
    changeLoginStatus(state, { payload = {} }) {
      // setAuthority(payload.currentAuthority);
      return { ...state, type: 'account' };
    },
    // *menu(state, { payload = {} }) {
    //   // setAuthority(payload.currentAuthority);
    //   return { 
    //     ...state, 
    //     menuList: payload
    //   };
    // },
  },
};
export default Model;
