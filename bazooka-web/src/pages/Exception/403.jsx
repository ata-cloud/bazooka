import React from 'react';
// import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Empty, Button } from 'antd';
import router from 'umi/router';
class Exception403 extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  onToHome=()=>{
    router.replace('/service')
  }
  render() {
    return (
      <Empty
        image={Empty.PRESENTED_IMAGE_SIMPLE}
        description={
          <span>
            没有权限
          </span>
        }
      >
        <Button type="primary" onClick={this.onToHome}>回首页</Button>
      </Empty>
    );
  }
}
export default Exception403;
