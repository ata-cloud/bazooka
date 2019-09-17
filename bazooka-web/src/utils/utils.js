/* eslint no-useless-escape:0 import/prefer-default-export:0 */
import moment from 'moment';
const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/;

const isUrl = path => {
  return reg.test(path);
};

const isAntDesignPro = () => {
  if (ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION === 'site') {
    return true;
  }

  return window.location.hostname === 'preview.pro.ant.design';
}; // 给官方演示站点用，用于关闭真实开发环境不需要使用的特性

const isAntDesignProOrDev = () => {
  const { NODE_ENV } = process.env;

  if (NODE_ENV === 'development') {
    return true;
  }

  return isAntDesignPro();
};
//根据用户Id获取用户名
const getUserName = (userAll = [], userId) => {
  let userInfo = userAll.find(item => item.userId == userId) || {};
  return userInfo
}
//单位MB准换为GB
const MformG = (value, isParseInt) => {
  if (!value) {
    return 0
  }
  let currentValue = 0;
  if (isParseInt) {
    currentValue = parseInt(value / 1024)
  } else {
    currentValue = (value / 1024).toFixed(1)
    return Number(currentValue)
  }
}
const Percent = (value, total) => {
  if (!total || !value) {
    return 0
  }
  let percent = parseInt(value / total * 100)
  return percent
}
//相对时间
const getCurrentTime = (time) => {
  if (!time) {
    return
  }
  moment.locale('en',
    {
      relativeTime: {
        past: "%s前",
        s: "%d秒",
        m: "1分钟",
        mm: "%d分钟",
        h: "1小时",
        hh: "%d小时",
        d: "1天",
        dd: "%d天",
        M: "1个月",
        MM: "%d个月",
        y: "1年",
        yy: "%d年"
      }
    }
  )
  return moment(time).fromNow();
}
const isAdmin = () => {
  let isAdmin = localStorage.getItem('isAdmin'), result = true;
  if (isAdmin === '0') {
    result = false
  }
  return result
}
const toHref = (url) => {
  if (!url) {
    return
  }
  let currentUrl = url.indexOf('http') > -1 ? url : `http://${url}`;
  return currentUrl
}
const NoGitUrl = (url) => {
  let reg = /.git\b/, currentUrl=url;
  if(reg.test(url)) {
    currentUrl = url.substring(0, url.length-4)
  }
  return currentUrl
}
export { isAntDesignProOrDev, isAntDesignPro, isUrl, getUserName, MformG, getCurrentTime, isAdmin, toHref, Percent, NoGitUrl };
