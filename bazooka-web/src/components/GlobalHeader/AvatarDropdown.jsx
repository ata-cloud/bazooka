import React, { Fragment } from 'react';
import { Avatar, Menu, Spin, Icon, message } from 'antd';
import { FormattedMessage } from 'umi-plugin-react/locale';
import { connect } from 'dva';
import router from 'umi/router';
import HeaderDropdown from '../HeaderDropdown';
import EditPassword from './EditPassword';
import { auth } from '@/services/auth';
import styles from './index.less';

class AvatarDropdown extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showEditPassword: false
    };
  }

  onMenuClick = event => {
    const { key } = event;
    if (key === 'logout') {
      const { dispatch } = this.props;
      if (dispatch) {
        dispatch({
          type: 'login/logout',
        });
      }
      return;
    }
    if (key === 'editPassword') {
      this.setState({
        showEditPassword: true
      })
      return;
    }
    router.push(`/account/${key}`);
  };
  handleOk = async (values) => {
    const { dispatch } = this.props;
    let res = await auth.modifyPassword(values);
    if (res && res.code == '1') {
      message.success('修改成功');
      dispatch({
        type: 'login/logout',
      });
    }
  }
  handleCancel = () => {
    this.setState({
      showEditPassword: false
    })
  }

  render() {
    const { menu } = this.props;
    const { showEditPassword } = this.state;
    const currentUser = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {};
    // if (!menu) {
    //   return (
    //     <span className={`${styles.action} ${styles.account}`}>
    //       <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar" />
    //       <span className={styles.name}>{currentUser.name}</span>
    //     </span>
    //   );
    // }

    const menuHeaderDropdown = (
      <Menu className={styles.menu} selectedKeys={[]} onClick={this.onMenuClick}>
        {/* <Menu.Item key="center">
          <Icon type="user" />
          <FormattedMessage id="menu.account.center" defaultMessage="account center" />
        </Menu.Item>
        <Menu.Item key="settings">
          <Icon type="setting" />
          <FormattedMessage id="menu.account.settings" defaultMessage="account settings" />
        </Menu.Item> */}
        <Menu.Item key="editPassword">
          <Icon type="lock" />
          <span>修改密码</span>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="logout">
          <Icon type="logout" />
          <FormattedMessage id="menu.account.logout" defaultMessage="logout" />
        </Menu.Item>
      </Menu>
    );
    return (
      <Fragment>
        <HeaderDropdown overlay={menuHeaderDropdown}>
          <span className={`${styles.action} ${styles.account}`}>
            {/* <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar" /> */}
            <span className={styles.name}>{currentUser.realName ? currentUser.realName : '欢迎'}</span>
            <Icon type="down"/>
          </span>
        </HeaderDropdown>
        {/* {
          currentUser && currentUser.name ? (
            <HeaderDropdown overlay={menuHeaderDropdown}>
              <span className={`${styles.action} ${styles.account}`}>
                <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar" />
                <span className={styles.name}>{currentUser.name}</span>
              </span>
            </HeaderDropdown>
          ) : (
              <Spin
                size="small"
                style={{
                  marginLeft: 8,
                  marginRight: 8,
                }}
              />
            )
        } */}
        {showEditPassword && <EditPassword visible={showEditPassword} handleOk={this.handleOk} handleCancel={this.handleCancel} />}
      </Fragment>
    )
  }
}

export default connect(({ user }) => ({
  currentUser: user.currentUser,
}))(AvatarDropdown);
