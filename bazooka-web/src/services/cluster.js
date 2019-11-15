import { requestPost } from '@/utils/request';
const cluster = {
  getClusterPage: async function (params = {}) {
    return requestPost('/cluster/getClusterPage', params);
  },
  getClusterDetail: async function (params = {}) {
    return requestPost('/cluster/getClusterDetail', params);
  },
  getClusterNodePage: async function (params = {}) {
    return requestPost('/clusterNode/getClusterNodePage', params);
  },
  getAvailableResource: async function (params = {}) {
    return requestPost(`/cluster/${params.clusterId}/available-resource`, params);
  },
  createMesosCluster: async function (params = {}) {
    return requestPost(`/cluster/createMesosCluster`, params);
  },
  createSingleNodeCluster: async function (params = {}) {
    return requestPost(`/cluster/createSingleNodeCluster`, params);
  },
}
export {
  cluster
}