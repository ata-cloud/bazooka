import { service } from '@/services/service';
const ServiceModel = {
  namespace: 'service',
  state: {
    appList: [],
    appRunStatus: {},
    appRunCurrentImage: {},
    appRunEndpoint: [],
    appRunContainers: [],
    appBaseInfo: {},
    appDeployConfigList: {},
    dockerList: {},
    dockerListAll: [],
    appHistory: {},
    appDeployConfigListAll: [],
    appDeployConfigListAllNoEnv: [],
    appHistoryMarathon: {},
    appDeployServicePort: {},
    appGetBranch: [],
    appDeployStatus: {},
    deployTypes: {// 0:禁用 1：可用  2：loading
      "START": 1,
      "RESTART": 1,
      "SCALE": 1,
      "STOP": 1,
      "DEPLOY": 1,
      "ROLLBACK": 1,
    },
    appDeployFlow: {},
    envWithPro: [],
    appOperate: {},
    appDeployFlowInfo: {}
  },
  effects: {
    *appList({ payload }, { call, put }) {
      const response = yield call(service.appList, payload);
      yield put({
        type: 'getAppList',
        payload: response,
      });
    },
    *envWithPro({ payload }, { call, put }) {
      const response = yield call(service.queryEnvInfoByProjectId, payload);
      yield put({
        type: 'getEnvWithPro',
        payload: response,
      });
    },
    *appRunStatus({ payload }, { call, put }) {
      const response = yield call(service.appRunStatus, payload);
      yield put({
        type: 'getAppRunStatus',
        payload: response,
      });
    },
    *appRunCurrentImage({ payload }, { call, put }) {
      const response = yield call(service.appRunCurrentImage, payload);
      yield put({
        type: 'getAppRunCurrentImage',
        payload: response,
      });
    },
    *appRunEndpoint({ payload }, { call, put }) {
      const response = yield call(service.appRunEndpoint, payload);
      yield put({
        type: 'getAppRunEndpoint',
        payload: response,
      });
    },
    *appRunContainers({ payload }, { call, put }) {
      const response = yield call(service.appRunContainers, payload);
      yield put({
        type: 'getAppRunContainers',
        payload: response,
      });
    },
    *appBaseInfo({ payload }, { call, put }) {
      const response = yield call(service.appGet, payload);
      yield put({
        type: 'getAppBaseInfo',
        payload: response,
      });
    },
    *appDeployConfigList({ payload }, { call, put }) {
      const response = yield call(service.appDeployConfigList, payload);
      yield put({
        type: 'getAppDeployConfigList',
        payload: response,
      });
    },
    *appDeployConfigListAll({ payload }, { call, put }) {
      const response = yield call(service.appDeployConfigListAll, payload);
      yield put({
        type: 'getAppDeployConfigListAll',
        payload: { ...response, ...payload },
      });
    },
    *dockerList({ payload }, { call, put }) {
      const response = yield call(service.dockerList, payload);
      yield put({
        type: 'getDockerList',
        payload: response,
      });
    },
    *dockerListAll({ payload }, { call, put }) {
      const response = yield call(service.dockerListAll, payload);
      yield put({
        type: 'getDockerListAll',
        payload: response,
      });
    },
    *appHistory({ payload }, { call, put }) {
      const response = yield call(service.appHistory, payload);
      yield put({
        type: 'getAppHistory',
        payload: response,
      });
    },
    *appHistoryMarathon({ payload }, { call, put }) {
      const response = yield call(service.appHistoryMarathon, payload);
      yield put({
        type: 'getAppHistoryMarathon',
        payload: response,
      });
    },
    *appDeployServicePort({ payload }, { call, put }) {
      const response = yield call(service.appDeployServicePort, payload);
      yield put({
        type: 'getAppDeployServicePort',
        payload: { ...response, ...payload },
      });
    },
    *appGetBranch({ payload }, { call, put }) {
      const response = yield call(service.appGetBranch, payload);
      yield put({
        type: 'getAppGetBranch',
        payload: response,
      });
    },
    *appDeployStatus({ payload }, { call, put }) {
      const response = yield call(service.appDeployStatus, payload);
      yield put({
        type: 'getAppDeployStatus',
        payload: response,
      });
    },
    *appDeployFlow({ payload }, { call, put }) {
      const response = yield call(service.appDeployFlow, payload);
      yield put({
        type: 'getAppDeployFlow',
        payload: response,
      });
    },
    *appDeployFlowInfo({ payload }, { call, put }) {
      const response = yield call(service.appDeployFlowInfo, payload);
      yield put({
        type: 'getAppDeployFlowInfo',
        payload: response,
      });
    },
    *appOperate({ payload }, { call, put }) {
      const response = yield call(service.appOperate, payload);
      yield put({
        type: 'getAppOperate',
        payload: response,
      });
    },
  },
  reducers: {
    getAppList(state, { payload = {} }) {
      return {
        ...state,
        appList: payload.data || {},
      };
    },
    getAppRunStatus(state, { payload = {} }) {
      let deployTypesCopy = { ...state.deployTypes };
      if (payload.data) {
        let serviceStatus = payload.data.status;
        switch (serviceStatus) {
          case '0': {
            deployTypesCopy = {
              "START": 0,
              "RESTART": 0,
              "SCALE": 1,
              "STOP": 0,
              "DEPLOY": 1,
              "ROLLBACK": 1,
            }
          } break;
          case '1': {
            deployTypesCopy = {
              "START": 1,
              "RESTART": 0,
              "SCALE": 1,
              "STOP": 0,
              "DEPLOY": 1,
              "ROLLBACK": 1,
            }
          } break;
          case '2': {
            deployTypesCopy = {
              "START": 0,
              "RESTART": 0,
              "SCALE": 1,
              "STOP": 0,
              "DEPLOY": 1,
              "ROLLBACK": 1,
            }
          } break;
          case '3': {
            deployTypesCopy = {
              "START": 0,
              "RESTART": 1,
              "SCALE": 1,
              "STOP": 1,
              "DEPLOY": 1,
              "ROLLBACK": 1,
            }
          } break;
        }
      }
      return {
        ...state,
        deployTypes: deployTypesCopy,
        appRunStatus: payload.data || {},
      };
    },
    getAppRunCurrentImage(state, { payload = {} }) {
      return {
        ...state,
        appRunCurrentImage: payload.data || {},
      };
    },
    getAppRunEndpoint(state, { payload = {} }) {
      return {
        ...state,
        appRunEndpoint: payload.data || [],
      };
    },
    getAppRunContainers(state, { payload = {} }) {
      return {
        ...state,
        appRunContainers: payload.data || [],
      };
    },
    getAppBaseInfo(state, { payload = {} }) {
      return {
        ...state,
        appBaseInfo: payload.data || {},
      };
    },
    getAppDeployConfigList(state, { payload = {} }) {
      return {
        ...state,
        appDeployConfigList: payload.data || {},
      };
    },
    getAppDeployConfigListAll(state, { payload = {} }) {
      let allData = payload.envId ? 'appDeployConfigListAll' : 'appDeployConfigListAllNoEnv';
      return {
        ...state,
        [allData]: payload.data || [],
      };
    },
    getDockerList(state, { payload = {} }) {
      return {
        ...state,
        dockerList: payload.data || {},
      };
    },
    getDockerListAll(state, { payload = {} }) {
      return {
        ...state,
        dockerListAll: payload.data || [],
      };
    },
    getAppHistory(state, { payload = {} }) {
      let list = payload.data || {};
      if (state.appHistory.pageNum && list.pageNum > 1 && list.pageNum > state.appHistory.pageNum) {
        list = {
          ...list,
          rows: [
            ...state.appHistory.rows,
            ...list.rows
          ]
        }
      }
      return {
        ...state,
        appHistory: list,
      };
    },
    getAppHistoryMarathon(state, { payload = {} }) {
      return {
        ...state,
        appHistoryMarathon: payload.data || {},
      };
    },
    getAppDeployServicePort(state, { payload = {} }) {
      return {
        ...state,
        appDeployServicePort: {
          ...state.appDeployServicePort,
          [payload.containerPort]: payload.data
        },
      };
    },
    getAppGetBranch(state, { payload = {} }) {
      return {
        ...state,
        appGetBranch: payload.data || []
      };
    },
    getAppDeployStatus(state, { payload = {} }) {
      let deployTypesCopy = { ...state.deployTypes };
      if (payload.data) {
        let data = payload.data;
        if (data.deploying) {
          deployTypesCopy = {
            "START": 0,
            "RESTART": 0,
            "SCALE": 0,
            "STOP": 0,
            "DEPLOY": 0,
            "ROLLBACK": 0,
            [data.deployType]: 2
          }
        }
      }
      return {
        ...state,
        deployTypes: deployTypesCopy,
        appDeployStatus: payload.data || {}
      }
    },
    getAppDeployFlow(state, { payload = {} }) {
      return {
        ...state,
        appDeployFlow: payload.data || {}
      };
    },
    getAppDeployFlowInfo(state, { payload = {} }) {
      return {
        ...state,
        appDeployFlowInfo: payload.data || {}
      };
    },
    getEnvWithPro(state, { payload = {} }) {
      return {
        ...state,
        envWithPro: payload.data || []
      };
    },
    getAppOperate(state, { payload = {} }) {
      return {
        ...state,
        appOperate: payload.data || {}
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
export default ServiceModel;
