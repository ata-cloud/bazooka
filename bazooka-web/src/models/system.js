import { system } from '@/services/system';
const SystemModel = {
  namespace: 'system',
  state: {
    credentialsList: [],
    clusterComponents: {}
  },
  effects: {
    *credentialsList({ payload }, { call, put }) {
      const response = yield call(system.credentialsList, payload);
      yield put({
        type: 'getCredentialsList',
        payload: response,
      });
    },
    *credentialsListTypes({ payload }, { call, put }) {
      const response = yield call(system.credentialsList, payload);
      yield put({
        type: 'getCredentialsListTypes',
        payload: {...response, ...payload},
      });
    },
    *clusterComponents({ payload }, { call, put }) {
      const response = yield call(system.clusterComponents, payload);
      yield put({
        type: 'getClusterComponents',
        payload: response,
      });
    },
  },
  reducers: {
    getCredentialsList(state, { payload = {} }) {
      return {
        ...state,
        credentialsList: payload.data || [],
      };
    },
    getCredentialsListTypes(state, { payload = {} }) {
      return {
        ...state,
        ['credentialsList'+payload.domain]: payload.data || [],
      };
    },
    getClusterComponents(state, { payload = {} }) {
      return {
        ...state,
        clusterComponents: payload.data || {},
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
export default SystemModel;
