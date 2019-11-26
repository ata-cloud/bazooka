import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Tabs } from 'antd';
import { isAdmin } from '@/utils/utils';
import router from 'umi/router';
import User from './user/index';
import Role from './role/index';

const { TabPane } = Tabs;
class Auth extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',
      tabList: [
        {
          key: '1',
          tab: '用户'
        },
        {
          key: '2',
          tab: '角色'
        }
      ],
    };
  }
  componentDidMount() { 
    if(!isAdmin()) {
      router.replace('/exception403')
    }
  }
  onTabChange = (key) => {
    this.setState({
      key
    })
  }
  render() {
    const { key, tabList } = this.state;
    return (
      <PageHeaderWrapper content="系统用户管理，角色分配等" tabList={tabList} onTabChange={this.onTabChange} tabActiveKey={key}>

        {
          key === '1' && <User />
        }
        {
          key === '2' && <Role />
        }
        {/* <Tabs activeKey={ key } onChange={this.onTabChange}>
            <TabPane tab="用户" key="1">
              <User />
            </TabPane>
            <TabPane tab="角色" key="2">
              <Role />
            </TabPane>
          </Tabs> */}
      </PageHeaderWrapper>
    );
  }
}
export default Auth;
