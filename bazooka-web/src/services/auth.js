import { requestPost, requestGet } from '@/utils/request';
const auth = {
  usergetPage: async function (params = {}) {
    return requestPost('/user/getPage', params);
  },
  userAdd: async function (params = {}) {
    return requestPost('/gateWay-pms/addUser', params);
  },
  userEdit: async function (params = {}) {
    return requestPost('/user/edit', params);
  },
  userDelete: async function (params = {}) {
    return requestPost('/user/delete', params);
  },
  modifyPassword: async function(params = {}) {
    return requestPost('/user/modifyPassword', params);
  },
  getUserPermissions: async function(params = {}) {
    return requestPost('/user/getUserMenuPermissions', params);
  },
  rolegetPage: async function (params = {}) {
    return requestPost('/role/getPage', params);
  },
  roleAdd: async function (params = {}) {
    return requestPost('/role/add', params);
  },
  roleEdit: async function (params = {}) {
    return requestPost('/role/edit', params);
  },
  roleDelete: async function (params = {}) {
    return requestPost('/role/delete', params);
  },
  permissiongetPage: async function (params = {}) {
    return requestPost('/permission/getPage', params);
  },
  permissionAdd: async function (params = {}) {
    return requestPost('/permission/add', params);
  },
  permissionEdit: async function (params = {}) {
    return requestPost('/permission/edit', params);
  },
  permissionDelete: async function (params = {}) {
    return requestPost('/permission/delete', params);
  },
  userIsAdmin: async function (params) {
    return requestPost('/user/isAdmin', params);
  },
}
export {
  auth
}