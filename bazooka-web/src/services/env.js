import { requestPost } from '@/utils/request';
const env = {
  envList: async function (params = {}) {
    return requestPost('/ata-ops/envs/list', params);
  },
  envCreate: async function (params = {}) {
    return requestPost('/ata-ops/envs/create', params);
  },
  envUpdate: async function (params = {}) {
    return requestPost('/ata-ops/envs/update', params);
  },
  envDeltete: async function (params = {}) {
    return requestPost(`/ata-ops/envs/${params.envId}/delete`, params);
  },
}
export {
  env
}