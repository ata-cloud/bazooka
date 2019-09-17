import { auth } from '@/services/auth';
const AuthModel = {
  namespace: 'auth',
  state: {
    userList: {},
    roleList: {},
    permissionList: {},
    userAll: {}
  },
  effects: {
    *userList({ payload }, { call, put }) {
      const response = yield call(auth.usergetPage, payload);
      yield put({
        type: 'usergetPage',
        payload: response,
      });
    },
    *roleList({ payload }, { call, put }) {
      const response = yield call(auth.rolegetPage, payload);
      yield put({
        type: 'rolegetPage',
        payload: response,
      });
    },
    *permissionList({ payload }, { call, put }) {
      const response = yield call(auth.permissiongetPage, payload);
      yield put({
        type: 'permissiongetPage',
        payload: response,
      });
    },
    *userAll({ payload }, { call, put }) {
      const response = yield call(auth.usergetPage, {pageNo: 1, pageSize: 200});
      yield put({
        type: 'usergetAll',
        payload: response,
      });
    },
  },
  reducers: {
    usergetPage(state, { payload = {} }) {
      return {
        ...state,
        userList: payload.data || {},
      };
    },
    usergetAll(state, { payload = {} }) {
      return {
        ...state,
        userAll: payload.data || {},
      };
    },
    rolegetPage(state, { payload = {} }) {
      return {
        ...state,
        roleList: payload.data || {},
      };
    },
    permissiongetPage(state, { payload = {} }) {
      return {
        ...state,
        permissionList: payload.data || {},
      };
    },
    clearData(state, { payload = {} }) {
      return {
        ...state,
        ...payload
      };
    },
  },
};
export default AuthModel;
