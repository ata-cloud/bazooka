import { requestPost, requestGet } from '@/utils/request';
const project = {
   //获取负责+参与项目列表
  projectList: async function (params = {}) {
    return requestPost('/project/queryProjectList', params);
  },
  //获取负责项目列表
  projectListAdmin: async function (params = {}) {
    return requestPost('/project/queryProjectListForAdmin', params);
  },
  //创建项目
  createProject: async function (params = {}) {
    return requestPost('/project/createProject', params);
  },
  //置顶
  topProject: async function (params = {}) {
    return requestPost(`/project/topProject/${params.projectId}`, params);
  },
  //取消置顶
  deleteTopProject: async function (params = {}) {
    return requestPost(`/project/deleteTopProject/${params.projectId}`, params);
  },
  //项目信息
  queryProjectInfo: async function (params = {}) {
    return requestPost(`/project/queryProjectInfo/${params.projectId}`, params);
  },
  //更新项目基本信息
  updateProject: async function (params = {}) {
    return requestPost(`/project/updateProject/${params.projectId}`, params);
  },
  //项目资源
  queryEnvPortList: async function (params = {}) {
    return requestPost(`/gateWay-pms/queryEnvInfoByProjectId/${params.projectId}`, params);
  },
  //根据环境获取端口
  queryDistributePort: async function (params = {}) {
    return requestPost(`/project/queryDistributePort/${params.envId}`, params);
  },
  //项目参与人列表
  queryDevUser: async function (params = {}) {
    return requestPost(`/gateWay-pms/project/queryDevUser/${params.projectId}`, params);
  },
  //删除项目参与人
  deleteDevUser: async function (params = {}) {
    return requestPost(`/project/deleteDevUser/${params.projectId}`, params);
  },
  //删除项目参与人
  addDevUser: async function (params = {}) {
    return requestPost(`/project/addDevUser/${params.projectId}`, params);
  },
  //删除项目
  deleteProject: async function (params = {}) {
    return requestPost(`/project/deleteProject/${params.projectId}`, params);
  },
  //按日期统计项目下所有服务的发布次数
  deployCounts: async function (params = {}) {
    return requestGet(`/deploy/counts/${params.projectId}/${params.granularity}`);
  },
}
export {
  project
}