import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Button, Icon, Typography, Progress, Form, Spin, Empty } from 'antd';
import { connect } from 'dva';
import Link from 'umi/link';
import styles from '../index.less';
import { CLUSTER_STATUS } from '@/common/constant';
import { MformG, isAdmin, Percent } from '@/utils/utils';
import router from 'umi/router';
const { Title, Text } = Typography;

class Cluster extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      resourceType: [
        {
          name: 'CPU',
          value: '',
          unit: 'S'
        },
        {
          name: '磁盘',
          value: ''
        },
        {
          name: '内存',
          value: ''
        },

      ]
    };
  }
  componentDidMount() {
    if(!isAdmin()) {
      router.replace('/exception403')
    }
    this.onFetchList()
  }
  onFetchList = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'cluster/clusterList',
      payload
    })
  }
  onRaload = (item) => {
    this.onFetchList()
  }
  onRouteTo = (href, data) => {
    this.props.history.push({
      pathname: href,
      query: data || {}
    })
  }
  //------------------------页面渲染-------------------------------------
  renderTitle() {
    return (
      <Row type="flex" justify="space-between" align="middle">
        <Col>通过Mesos集群、Kubernetes集群或者独立节点的方式，管理物理机、虚拟机、云主机等各类计算资源</Col>
        {/* <Col className={styles.flexCenter}>
          <Button type="primary" onClick={this.onAdd}>+ 导入集群</Button>
          <Button type="primary" className={styles.marginL} onClick={this.onAdd}>+ 新建集群</Button>
        </Col> */}
      </Row>
    )
  }
  renderResource(item) {
    return (
      <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
        <div>{item.name}</div>
        <div className={`${styles.flex1} ${styles.processLR}`}>
          <Progress percent={50} showInfo={false} />
        </div>
        <div>50%（135 / 144 Shares）</div>
      </div>
    )
  }
  //集群列表
  renderList() {
    const { resourceType } = this.state;
    const { list, loading } = this.props;
    return (
      <Row gutter={24}>
        {
          list && list.rows && !list.rows.length && !loading &&
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
        }
        {
          list && list.rows && list.rows.length && list.rows.map((item, i) => (

            <Col md={12} key={i} className={styles.marginB} sm={24}>
              <Card title={
                <strong>{item.name}<Text type="secondary">（本地）</Text></strong>
              } hoverable extra={
                <Icon type="reload" onClick={() => this.onRaload(item)} />
              }>
                <div onClick={() => { this.onRouteTo('/cluster/detail', { clusterId: item.clusterId }) }}>
                  <div className={styles.flex}>
                    <div className={styles.clusterItem}>
                      <Text strong>状态</Text>
                      {
                        CLUSTER_STATUS[item.status] &&
                        <Fragment>
                          <div className={styles.midHeight}>
                            <Icon type={CLUSTER_STATUS[item.status].icon} style={{ fontSize: 30, color: CLUSTER_STATUS[item.status].colorValue }} />
                          </div>
                          <Text type="secondary">{CLUSTER_STATUS[item.status].text}</Text>
                        </Fragment>
                      }
                    </div>
                    <div className={styles.clusterItem}>
                      <Text strong>环境</Text>
                      <div className={styles.midHeight}>
                        <strong>{item.envQuantity}</strong>
                      </div>
                    </div>
                    <div className={styles.clusterItem}>
                      <Text strong>主机</Text>
                      <div className={styles.midHeight}>
                        <span className={styles.textSuccess}>{item.normalNodeQuantity}</span>
                        <span>/</span>
                        <strong>{item.nodeQuantity}</strong>
                      </div>
                      <Text type="secondary">正常/所有</Text>
                    </div>
                    <div className={styles.clusterItem}>
                      <Text strong>容器</Text>
                      <div className={styles.midHeight}>
                        <span className={styles.textSuccess}>{item.runningServiceQuantity || 0}</span>
                        <span>/</span>
                        <strong>{item.serviceQuantity || 0}</strong></div>
                      <Text type="secondary">运行中/所有容器</Text>
                    </div>
                  </div>

                  <div className={styles.marginT}>
                    <p>环境资源（已分隔给各环境的资源 / 集群总资源）</p>
                    <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                      <div>CPU</div>
                      <div className={`${styles.flex1} ${styles.processLR}`}>
                        <Progress percent={Percent(item.envCpu, item.cpu)} showInfo={false} strokeColor="#1890ff"/>
                      </div>
                      <div className={styles.rightBlock}>{`${Percent(item.envCpu, item.cpu)}%（${item.envCpu} / ${item.cpu} Core）`}</div>
                    </div>
                    <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                      <div>内存</div>
                      <div className={`${styles.flex1} ${styles.processLR}`}>
                        <Progress percent={Percent(item.envMemory, item.memory)} showInfo={false} strokeColor="#1890ff"/>
                      </div>
                      <div className={styles.rightBlock}>{`${Percent(item.envMemory, item.memory)}%（${MformG(item.envMemory)} / ${MformG(item.memory)} GiB）`}</div>
                    </div>
                    <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                      <div>磁盘</div>
                      <div className={`${styles.flex1} ${styles.processLR}`}>
                        <Progress percent={Percent(item.envDisk, item.disk)} showInfo={false} strokeColor="#1890ff" />
                      </div>
                      <div className={styles.rightBlock}>{`${Percent(item.envDisk, item.disk)}%（${MformG(item.envDisk)} / ${MformG(item.disk)} GiB）`}</div>
                    </div>
                    {/* {
                        resourceType.map((item, i) => (
                          <Fragment key={i}>
                            {this.renderResource(item)}
                          </Fragment>

                        ))
                      } */}

                  </div>
                </div>

              </Card>
            </Col>
          ))
        }
      </Row>
    )
  }
  render() {
    const { loading } = this.props;
    return (
      <PageHeaderWrapper content={this.renderTitle()}>
        <Spin spinning={loading}>
          {this.renderList()}
        </Spin>
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ cluster, loading }) => ({
  list: cluster.clusterList,
  loading: loading.effects["cluster/clusterList"]
}))(Cluster));

