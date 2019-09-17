/**
 * Ant Design Pro v4 use `@ant-design/pro-layout` to handle Layout.
 * You can view component api by:
 * https://github.com/ant-design/ant-design-pro-layout
 */
import RightContent from '@/components/GlobalHeader/RightContent';
import { connect } from 'dva';
import React, { useState } from 'react';
import logo from '../assets/ata_logo.png';
import Authorized from '@/utils/Authorized';
import { formatMessage } from 'umi-plugin-react/locale';
import { isAntDesignPro } from '@/utils/utils';
import { BasicLayout as ProLayoutComponents } from '@ant-design/pro-layout';
import Link from 'umi/link';
import router from 'umi/router';

/**
 * use Authorized check all menu item
 */
// const menuDataRender = menuList => {
//   return menuList.map(item => {
//     console.log('menuList-->', menuList)
//     const localItem = { ...item, children: item.children ? menuDataRender(item.children) : [] };
//     return Authorized.check(item.authority, localItem, null);
//   });
// };
// const menu = [
//   {
//     path: '/service',
//     name: '服务',
//     icon: 'unordered-list',
//   },
//   {
//     path: '/project',
//     name: '项目',
//     icon: 'project',
//   },
//   {
//     path: '/environment',
//     name: '环境',
//     icon: 'table',
//   },
//   {
//     path: '/cluster',
//     name: '集群',
//     icon: 'cluster',
//   },
//   {
//     path: '/auth',
//     name: '用户',
//     icon: 'user',
//   },
//   {
//     path: '/system',
//     name: '系统',
//     icon: 'profile',
//   },
// ];
// const menu = localStorage.getItem('menuList') ? JSON.parse(localStorage.getItem('menuList')):[] ;
// function formatter(data) {
//   return data.map(item => {
//     let localItem = {};
//     localItem = { ...item, children: item.childrenMenu ? formatter(item.childrenMenu) : [] };
//     return Authorized.check(item.authority, localItem, null);
//   });
// }
// const menuDataRender = () => {
//   return formatter(menu);
// };
// const footerRender = (_, defaultDom) => {
//   if (!isAntDesignPro()) {
//     return;
//   }
//   return (
//     <>
//       {/* {defaultDom} */}
//       <div
//         style={{
//           padding: '0px 24px 24px',
//           textAlign: 'center',
//         }}
//       >
//         <a href="https://www.netlify.com" target="_blank">
//           <img
//             src="https://www.netlify.com/img/global/badges/netlify-color-bg.svg"
//             width="82px"
//             alt="netlify logo"
//           />
//         </a>
//       </div>
//     </>
//   );
// };

class BasicLayout extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menu: []
    };
  }
  componentDidMount() {
    const { dispatch } = this.props;
    let user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {}
    if (!user.userId) {
      router.replace('/user');
    }
    let menu = localStorage.getItem('menuList') ? JSON.parse(localStorage.getItem('menuList')) : [];
    this.setState({
      menu
    })
    this.onFetchUserAll()
  }
  onFetchUserAll = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'auth/userAll',
      payload: {}
    })
  }
  onHome=()=>{
    router.push('/service')
  }
  formatter = (data) => {
    return data && data.length && data.map(item => {
      let localItem = {};
      localItem = { ...item, icon: null, children: item.childrenMenu ? formatter(item.childrenMenu) : [] };
      return Authorized.check(item.authority, localItem, null);
    });
  }
  footerRender = (_, defaultDom) => {
    if (!isAntDesignPro()) {
      return;
    }
    return (
      <>
        {/* {defaultDom} */}
        <div
          style={{
            padding: '0px 24px 24px',
            textAlign: 'center',
          }}
        >
          <a href="https://www.netlify.com" target="_blank">
            <img
              src="https://www.netlify.com/img/global/badges/netlify-color-bg.svg"
              width="82px"
              alt="netlify logo"
            />
          </a>
        </div>
      </>
    );
  };

  menuDataRender = () => {
    const { menu } = this.state;
    let res = this.formatter(menu) || [];
    return res
  };
  handleMenuCollapse = payload => {
    const { dispatch } = this.props;
    if (dispatch) {
      return dispatch({
        type: 'global/changeLayoutCollapsed',
        payload,
      });
    }
  };
  render() {
    const { dispatch, children, settings } = this.props;
    return (
      <ProLayoutComponents
        logo={
          <img src={logo} onClick={this.onHome}/>
        }
        fixedHeader={true}
        onCollapse={this.handleMenuCollapse}
        menuItemRender={(menuItemProps, defaultDom) => {
          return <Link to={menuItemProps.path}>{defaultDom}</Link>;
        }}
        // breadcrumbRender={(routers = []) => {
        //   return [
        //     {
        //       path: '/',
        //       breadcrumbName: formatMessage({
        //         id: 'menu.home',
        //         defaultMessage: 'Home',
        //       }),
        //     },
        //     ...routers,
        //   ];
        // }}
        footerRender={this.footerRender}
        menuDataRender={this.menuDataRender}
        formatMessage={formatMessage}
        rightContentRender={rightProps => <RightContent {...rightProps} />}
        {...this.props}
        {...settings}
      >
        {children}
      </ProLayoutComponents>
    );
  }
}

export default connect(({ global, settings, auth }) => ({
  collapsed: global.collapsed,
  settings,
  userAll: auth.userAll
}))(BasicLayout);
