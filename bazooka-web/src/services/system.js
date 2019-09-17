import { requestPost, requestGet } from '@/utils/request';
const system = {
  //凭据列表
  credentialsList: async function (params = {}) {
    return requestGet('/credentials/get/list', params);
  },
  //添加凭据
  credentialsAdd: async function (params = {}) {
    return requestPost('/credentials/add', params);
  },
  //修改凭据
  credentialsUpdate: async function (params = {}) {
    return requestPost(`/credentials/update/${params.id}`, params);
  },
  //ATCLOUD系统服务组件
  clusterComponents: async function (params = {}) {
    return requestGet(`/cluster/components/${params.clusterName}`, params);
  },
}
export {
  system
}