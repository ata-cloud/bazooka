import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Button, Dropdown, Menu, Icon, Spin, Empty,message } from 'antd';
import { connect } from 'dva';
import { ServiceItem } from '@/pages/components/';
import router from "umi/router";
import styles from '@/pages/index.less';
import { service } from '@/services/service';
class ProjectList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [{}, {}, {}, {}],
      projectId: undefined
    };
  }
  componentDidMount() {
    const { appList } = this.props;
    if (!appList.length) {
      this.onFetchList();
    }
  }
  componentWillReceiveProps(nextProps) {
    const { keyword } = nextProps;
    if(this.props.keyword !== nextProps.keyword) {
      this.onFetchList({keyword: keyword})
    }
  }
  onFetchList = (params) => {
    const { projectId, appList, dispatch } = this.props;
    dispatch({
      type: 'service/appList',
      payload: { projectId, ...params }
    })
  }
  onRouteTo = (href, data) => {
    router.push({
      pathname: href,
      query: {
        projectId: data.projectId,
        appId: data.id
      }
    })
  }
  onTop = async (item) => {
    if (item.orderId > 0) {
      //取消置顶
      let res = await service.topDelete({ appId: item.id });
      if (res && res.code === '1') {
        message.success('取消成功');
        this.onFetchList();
      }
    } else {
      //置顶
      let res = await service.topAdd({ appId: item.id });
      if (res && res.code === '1') {
        message.success('置顶成功');
        this.onFetchList();
      }
    }
  }
  //------------------------页面渲染-------------------------------------

  renderList() {
    const { appList } = this.props;
    return (
      <Fragment>
        <Row gutter={24}>
          {
            appList && appList.length ? appList.map((item, i) => (
              <Col span={8} key={i}>
                <ServiceItem item={{ ...item, index: i }} toTop={() => this.onTop(item)} onItemClick={() => this.onRouteTo('/service/detail', item)} />
              </Col>
            )) :
              <div className={styles.marginT}>
                <Empty />
              </div>
          }
        </Row>
      </Fragment>
    )
  }
  render() {
    const { loading } = this.props;
    return (
      <Fragment>
        <Spin spinning={loading}>
          {this.renderList()}
        </Spin>
      </Fragment>
    );
  }
}
export default connect(({ service, loading }) => ({
  appList: service.appList,
  loading: loading.effects["service/appList"]
}))(ProjectList);