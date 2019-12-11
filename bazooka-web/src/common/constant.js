import { IMAGE } from '@/assets/index';
const CARD_TITLE_BG = {
  backgroundColor: '#f5f5f5'
};
//集群类型
const CLUSTER_TYPE_O = {
  '0': 'Mesos集群',
  '1': 'Kubernetes集群',
  '2': 'Bazooka单节点集群'
};
const CLUSTER_TYPE_O_ALL = {
  '0': {
    name: 'Mesos集群',
    buildName: 'Mesos构建发布',
    buildType: 'MARATHON_BUILD'
  },
  '1': {
    name: 'Kubernetes集群',
    buildName: 'Kubernetes构建发布',
    buildType: 'KUBERNETES_BUILD'
  },
  '2': {
    name: 'Bazooka单节点集群',
    buildName: '单节点构建发布',
    buildType: 'NODE_BUILD'
  }
};
//构建发布类型
const DEPLOY_WITH_BUILD = {
  "NODE_BUILD": {
    deployType: 'NODE_BUILD_DEPLOY'
  },
  "MARATHON_BUILD": {
    deployType: 'MARATHON_BUILD_DEPLOY'
  },
  "KUBERNETES_BUILD": {
    deployType: 'KUBERNETES_BUILD_DEPLOY'
  },
  "DOCKER_IMAGE": {
    deployType: 'IMAGE_DEPLOY'
  },
};
const CLUSTER_STATUS = {
  '0': {
    type: 'success',
    icon: 'smile',
    color: 'successColor',
    text: '正常',
    colorValue: '#52c41a',
    style: 'successCircle'
  },
  '1': {
    type: 'warning',
    icon: 'meh',
    color: 'waringColor',
    text: '警告',
    colorValue: '#edb233',
    style: 'waringCircle'
  },
  '2': {
    type: 'error',
    icon: 'frown',
    color: 'errorColor',
    text: '异常',
    colorValue: '#f5222d',
    style: 'errorCircle'
  },
};
const ENV_STATUS_OBJECT = {
  '0': {
    color: 'successColor',
    text: '正常使用',
  },
  '1': {
    color: 'waringColor',
    text: '暂停发布',
  },
  '2': {
    color: 'errorColor',
    text: '集群异常',
  },
};
const ENV_STATUS_ARR = [
  {
    color: 'successColor',
    text: '正常使用',
    value: '0'
  },
  {
    color: 'successColor',
    text: '暂停发布',
    value: '1'
  },
];
const APP_STATUS = {
  '0': {
    text: '未发布',
    color: 'disabledColor'
  },
  '1': {
    text: '已关闭',
    color: 'errorColor'
  },
  '2': {
    text: '启动中',
    color: 'waringColor'
  },
  '3': {
    text: '运行中',
    color: 'successColor'
  },
}
const COLOR_SHOW_OLD = ['#b1d5a1', '#abcbf2', '#e19999', '#f4c988', '#e3e1e1'];
const COLOR_SHOW = ['#f5f5fe', '#f7ded3', '#f3fff6', '#ffeaeb', '#def5fe'];
const SCALE_CPU = [0.1, 0.3, 0.5, 1, 2, 3, 4, 8, 16];
const SCALE_MEN = [128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768];
const SCALE_CASE = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];
const CONTAINTER_STATUS_O = {
  'TASK_STAGING': {
    text: '准备中',
    color: 'waringColor'
  },
  'TASK_STARTING': {
    text: '启动中'
  },
  'TASK_RUNNING': {
    text: '运行中'
  },
  'TASK_UNREACHABLE': {
    text: '无法访问',
    color: 'errorColor'
  },
  'TASK_KILLING': {
    text: '关闭中'
  },
  'TASK_FINISHED': {
    text: '已完成',
    color: 'disabledColor'
  },
  'TASK_KILLED': {
    text: '已关闭',
    color: 'disabledColor'
  },
  'TASK_FAILED': {
    text: '失败',
    color: 'errorColor'
  },
  'TASK_LOST': {
    text: '丢失',
    color: 'errorColor'
  },
};
const HEALTH_STATUS_O = {
  '0': {
    text: '健康',
    bgColor: 'successCircle'
  },
  '1': {
    text: '不健康',
    bgColor: 'errorCircle'
  },
  '2': {
    text: '没有健康检查',
    bgColor: 'disabledCircle'
  },
  '3': {
    text: '不存在健康',
    bgColor: 'hollowCircle'
  },
};
const SERVICE_LOG_RESULT_O = {
  "SUCCESS": {
    color: 'successColor',
    text: '成功'
  },
  "PROCESS": {
    // color: 'successColor',
    text: '进行中'
  },
  "FAILURE": {
    color: 'errorColor',
    text: '失败'
  }
}
const APP_KINDS_O = {
  "OPS_GITLAB": {
    type: 'OPS_GITLAB',
    text: 'OPS托管Gitlab',
    icon: IMAGE.GITLAB
  },
  "GIT_REPOSITORY": {
    type: 'GIT_REPOSITORY',
    text: 'Git代码仓库',
    icon: IMAGE.GIT
  }
};
//凭据类型
const GIT_DOMAIN_O = {
  GIT_SERVER: '代码仓库凭据',
  DOCKER_REGISTRY: '镜像库凭据',
  NODE_LOGIN: '节点登录凭据'
};

export {
  CARD_TITLE_BG,
  SCALE_CPU,
  SCALE_MEN,
  SCALE_CASE,
  CLUSTER_TYPE_O,
  CLUSTER_STATUS,
  ENV_STATUS_OBJECT,
  ENV_STATUS_ARR,
  COLOR_SHOW,
  APP_STATUS,
  CONTAINTER_STATUS_O,
  SERVICE_LOG_RESULT_O,
  HEALTH_STATUS_O,
  APP_KINDS_O,
  CLUSTER_TYPE_O_ALL,
  GIT_DOMAIN_O,
  DEPLOY_WITH_BUILD
}














