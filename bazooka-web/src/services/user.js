import { requestPost, requestGet } from '@/utils/request';
export async function query() {
  return requestGet('/api/users');
}
export async function queryCurrent() {
  return requestGet('/api/currentUser');
}
export async function queryNotices() {
  return requestGet('/api/notices');
}
export async function fakeAccountLogin(params) {
  return requestPost('/api/login/account', params);
}
