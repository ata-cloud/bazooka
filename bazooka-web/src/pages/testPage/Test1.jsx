import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
class TestOne extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() {}
  render() {
    return (
      <PageHeaderWrapper>
        <Card>6666我就测试测试</Card>
      </PageHeaderWrapper>
    );
  }
}
export default TestOne;
