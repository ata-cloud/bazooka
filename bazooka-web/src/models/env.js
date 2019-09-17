import { env } from '@/services/env';
const EnvModel = {
  namespace: 'env',
  state: {
    envList: []
  },
  effects: {
    *envList({ payload }, { call, put }) {
      const response = yield call(env.envList, payload);
      yield put({
        type: 'getEnvList',
        payload: response,
      });
    },
  },
  reducers: {
    getEnvList(state, { payload = {} }) {
      return {
        ...state,
        envList: payload.data || [],
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
export default EnvModel;
