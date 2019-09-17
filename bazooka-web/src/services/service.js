import { requestPost, requestGet } from '@/utils/request';
const service = {
  appList: async function (params = {}) {
    return requestGet('/app/list', params);
  },
  //置顶
  topAdd: async function (params = {}) {
    return requestPost(`/app/top/add/${params.appId}`, params);
  },
  //取消置顶
  topDelete: async function (params = {}) {
    return requestPost(`/app/top/delete/${params.appId}`, params);
  },
  //新增应用
  appCreate: async function (params = {}) {
    return requestPost(`/app/create/${params.projectId}`, params);
  },
  //删除应用
  appDelete: async function (params = {}) {
    return requestPost(`/app/delete/${params.appId}`, params);
  },
  //更新应用
  appUpdate: async function (params = {}) {
    return requestPost(`/app/update/${params.appId}`, params);
  },
  //项目相关环境列表
  queryEnvInfoByProjectId: async function (params = {}) {
    return requestPost(`/gateWay-pms/queryEnvInfoByProjectId/${params.projectId}`, params);
  },
  //应用概览，运行状态
  appRunStatus: async function (params = {}) {
    return requestGet(`/app/run/status/${params.appId}/${params.envId}`, params);
  },
  //镜像版本信息
  appRunCurrentImage: async function (params = {}) {
    return requestGet(`/app/run/current-image/${params.appId}/${params.envId}`, params);
  },
  //应用访问地址，端口
  appRunEndpoint: async function (params = {}) {
    return requestGet(`/app/run/endpoint/${params.appId}/${params.envId}`, params);
  },
  //容器列表
  appRunContainers: async function (params = {}) {
    return requestGet(`/app/run/containers/${params.appId}/${params.envId}`, params);
  },
  //查询详情
  appGet: async function (params = {}) {
    return requestGet(`/app/get/${params.appId}`, params);
  },
  //分页查询应用发布配置
  appDeployConfigList: async function (params = {}) {
    return requestGet(`/app/deploy-config/list/${params.appId}/${params.page ? params.page : 1}/${params.size ? params.size : 10}`, params);
  },
  //查询全部应用发布配置
  appDeployConfigListAll: async function (params = {}) {
    return requestGet(`/app/deploy-config/list/${params.appId}`, params);
  },

  //新建发布配置
  appDeployConfigCreate: async function (params = {}) {
    return requestPost(`/app/deploy-config/create/${params.appId}`, params);
  },
  //修改发布配置
  appDeployConfigUpdate: async function (params = {}) {
    return requestPost(`/app/deploy-config/update/${params.appId}/${params.configId}`, params);
  },
  //删除发布配置
  appDeployConfigDelete: async function (params = {}) {
    return requestPost(`/app/deploy-config/delete/${params.appId}/${params.configId}`, params);
  },
  //发布配置详情
  appDeployConfigGet: async function (params = {}) {
    return requestGet(`/app/deploy-config/get/${params.configId}`, params);
  },
  //端口申请
  appDeployServicePort: async function (params = {}) {
    return requestGet(`/app/deploy-config/service-port/get/${params.appId}/${params.envId}/${params.containerPort}`, params);
  },
  //镜像列表
  dockerList: async function (params = {}) {
    return requestGet(`/docker-image/list/${params.appId}/${params.envId}/${params.page}/${params.size}`);
  },
  dockerListAll: async function (params = {}) {
    return requestGet(`/docker-image/list/${params.appId}/${params.envId}`);
  },
  // 启动服务: START 关闭服务: STOP 重启服务: RESTART 扩/缩容服务: SCALE 服务发布: DEPLOY 服务回滚: ROLLBACK
  // 推送镜像: PUSH_IMAGE 删除镜像: DELETE_IMAGE
  appOperate: async function (params = {}) {
    return requestPost(`/deploy/app/operate`, params);
  },
  //概览--所有操作记录
  appHistory: async function (params = {}) {
    return requestPost(`/deploy/app/operate/history`, params);
  },
  //操作详情
  appOperateLog: async function (params = {}) {
    return requestGet(`/deploy/app/operate/log/${params.eventId}`, params);
  },
  //构建发布--所有操作记录
  appHistoryMarathon: async function (params = {}) {
    return requestPost(`/deploy/app/operate/history/marathon`, params);
  },
  //操作详情
  appHistoryMarathonLog: async function (params = {}) {
    return requestGet(`/deploy/app/operate/history/marathon/detail/${params.eventId}`, params);
  },
  //根据发布配置选择代码分支
  appGetBranch: async function (params = {}) {
    return requestGet(`/app/deploy/repository/branch/${params.appId}/${params.configId}`);
  },
  //获取当前正在执行的操作（服务系列）
  appDeployStatus: async function (params = {}) {
    return requestGet(`/app/deploy-status/get/${params.appId}/${params.envId}`);
  },
  //操作记录状态(镜像系列)
  appOperateStatus: async function (params = {}) {
    return requestGet(`/deploy/app/operate/status/${params.eventId}`, params);
  },
  //获取正在发布的发布流
  appDeployFlow: async function (params = {}) {
    return requestGet(`/deploy/deploying-flow/${params.eventId}`);
  },
  //获取正在发布的配置信息
  appDeployFlowInfo: async function (params = {}) {
    return requestGet(`/deploy/deploying-flow/config/${params.appId}/${params.envId}`);
  },
  //根据集群id和slaveId查询容器实例日志信息
  getClusterDockerInstanceLog: async function (params = {}) {
    return requestPost(`/cluster/getClusterDockerInstanceLog`, params);
  },
  //判断是否存在自建gitlab
  isAtaGitlab: async function (params = {}) {
    return requestGet(`/gateWay-pms/project/isAtaGitlab`, params);
  },
}
export {
  service
}