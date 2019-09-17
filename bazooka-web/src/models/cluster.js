import { cluster } from '@/services/cluster';
const ClusterModel = {
  namespace: 'cluster',
  state: {
    clusterList: {}
  },
  effects: {
    *clusterList({ payload }, { call, put }) {
      const response = yield call(cluster.getClusterPage, payload);
      yield put({
        type: 'getClusterPage',
        payload: response,
      });
    },
  },
  reducers: {
    getClusterPage(state, { payload = {} }) {
      return {
        ...state,
        clusterList: payload.data || {},
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
export default ClusterModel;
