import { project } from '@/services/project';
const ProjectModel = {
  namespace: 'project',
  state: {
    projectList: [],
    proInfo: {},//项目信息
    proEnvSoruce: [],//项目资源
    devUserList: {},//参与人列表
    deployCounts:[],
    projectListAdmin: []
  },
  effects: {
    *projectList({ payload }, { call, put }) {
      const response = yield call(project.projectList, payload);
      yield put({
        type: 'getProjectList',
        payload: response,
      });
    },
    *projectListAdmin({ payload }, { call, put }) {
      const response = yield call(project.projectListAdmin, payload);
      yield put({
        type: 'getProjectListAdmin',
        payload: response,
      });
    },
    *proInfo({ payload }, { call, put }) {
      const response = yield call(project.queryProjectInfo, payload);
      yield put({
        type: 'getProInfo',
        payload: response,
      });
    },
    *proEnvSoruce({ payload }, { call, put }) {
      const response = yield call(project.queryEnvPortList, payload);
      yield put({
        type: 'getProEnvSoruce',
        payload: response,
      });
    },
    *devUserList({ payload }, { call, put }) {
      const response = yield call(project.queryDevUser, payload);
      yield put({
        type: 'getDevUserList',
        payload: response,
      });
    },
    *deployCounts({ payload }, { call, put }) {
      const response = yield call(project.deployCounts, payload);
      yield put({
        type: 'getDeployCounts',
        payload: response,
      });
    },
  },
  reducers: {
    getProjectList(state, { payload = {} }) {
      return {
        ...state,
        projectList: payload.data || [],
      };
    },
    getProjectListAdmin(state, { payload = {} }) {
      return {
        ...state,
        projectListAdmin: payload.data || [],
      };
    },
    getProInfo(state, { payload = {} }) {
      return {
        ...state,
        proInfo: payload.data || {},
      };
    },
    getProEnvSoruce(state, { payload = {} }) {
      return {
        ...state,
        proEnvSoruce: payload.data || [],
      };
    },
    getDevUserList(state, { payload = {} }) {
      return {
        ...state,
        devUserList: payload.data || [],
      };
    },
    getDeployCounts(state, { payload = {} }) {
      return {
        ...state,
        deployCounts: payload.data || [],
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
export default ProjectModel;
