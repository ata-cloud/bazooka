import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Button } from 'antd';
import { IMAGE } from '@/assets/';
import { connect } from 'dva';
import styles from '@/pages/index.less';
import stylesAddCluster from './index.less';
import Mesos from './mesos';
import Bazooka from './bazooka';
class AddCluster extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showType: ''
    };
  }
  componentDidMount() { }
  onAdd = (type) => {
    this.setState({
      showType: type
    })
  }
  //上一步
  onCancel = () => {
    this.setState({
      showType: ''
    })
  }
  // onSave = () => {
  //   this.setState
  // }
  renderMesos() {
    return (
      <Card>
        <div className={stylesAddCluster.clusterTitleBox}>
          <img src={IMAGE.MESOS} />
          <span className={stylesAddCluster.clusterTitle}>Mesos集群</span>
        </div>
        <div>
          <Row type="flex">
            <Col span={10}>
            </Col>
            <Col span={14}>
              <p><span className={stylesAddCluster.titleSub}>Mesos集群</span>是多主（master）多从（slave）的结构，核心组件为Mesos和Marathon。</p>
              <p>Mesos slave安装在所有slave节点上，管理节点资源并向Mesos master汇报。<br />
                Mesos master安装在所有master节点上，主从结构，主Mesos处理工作，其余Mesos服务stand by。<br />
                Mesos master收集Mesos slave收集的资源并提供给各个Framework使用。</p>
              <p>Marathon安装在所有master节点上，主从结构，主Marathon处理工作，其余Marathon服务stand by。<br />
                Marathon在Mesos master注册为Framework之后，接受Mesos master分配的计算资源，在各个Mesos slave节点上，由Docker Executor使用具体的计算资源并启动容器。</p>
              <p>Zookeeper安装在所有master节点上，提供一个高可用的数据库，保存Mesos集群的各类数据。</p>
              <p><a>了解更多</a></p>
              <Button type="primary" onClick={() => this.onAdd('Mesos')}>+ 添加Mesos集群</Button>
            </Col>
          </Row>
        </div>
      </Card>
    )
  }
  renderKubernetes() {
    return (
      <Card className={styles.marginT}>
        <div className={stylesAddCluster.clusterTitleBox}>
          <img src={IMAGE.KUBERNETES} />
          <span className={stylesAddCluster.clusterTitle}>Kubernetes集群</span>
        </div>
        <div>
          <Row type="flex">
            <Col span={10}>
            </Col>
            <Col span={12}>
              <p><span className={stylesAddCluster.titleSub}>Kubernetes集群</span>是多主（master）多从（slave）的结构，核心组件包括API Server，Scheduler，Controller Manager，Kubelet和etcd。</p>
              <p>API Server安装在所有master节点上，用于提供所有Kubernetes API，用于Kubernetes各组件之间的通信。<br />
                Scheduler安装在所有master节点上，作用是调度容器到各个节点。<br />
                Controller Manager安装在所有master节点上，是具体的执行者，负责节点故障的通知和响应、控制集群内容器的正确数量，也维护endpoints和命名空间。</p>
              <p>Kubeket安装在所有slave节点上，接收Kubernetes Master的容器任务，并管理容器以pod的方式在各个slave节点上正确运行。</p>
              <p>etcd可以安装在master节点、集群外部或者任何能被访问到的地方，提供一个高可用的数据库，保存Kubernetes集群的各类数据。</p>
              <p><a>了解更多</a></p>
              <Button type="primary" disabled>+ 添加Kubernetes集群</Button>
            </Col>
          </Row>
        </div>
      </Card>
    )
  }
  renderBazooka() {
    return (
      <Card className={styles.marginT}>
        <div className={stylesAddCluster.clusterTitleBox}>
          <img src={IMAGE.BAZOOKA} />
          <span className={stylesAddCluster.clusterTitle}>Bazooka单节点集群</span>
        </div>
        <div>
          <Row type="flex">
            <Col span={10}>
            </Col>
            <Col span={12}>
              <p><span className={stylesAddCluster.titleSub}>Bazooka单节点集群</span>是阿塔云Bazooka自行实现的资源管理插件，所有节点将不安装任何agent，而是通过ssh来部署容器。</p>
              <p>单节点集群只是将一系列分配给此业务的节点记录为一个集群，实际上节点间互不访问</p>
              <p>Kubeket安装在所有slave节点上，接收Kubernetes Master的容器任务，并管理容器以pod的方式在各个slave节点上正确运行。</p>
              <p>请注意，此方案可以管理单台物理机或者虚拟机，但是没有容器调度、负载均衡、健康检查和容器自愈等功能</p>
              <p><a>了解更多</a></p>
              <Button type="primary" onClick={() => this.onAdd('Bazooka')}>+ 添加Bazooka单节点集群</Button>
            </Col>
          </Row>
        </div>
      </Card>
    )
  }
  render() {
    const { showType } = this.state;
    return (
      <PageHeaderWrapper content="可以根据资源的类型不同，添加Mesos集群、Kubernetes集群或者单节点集群" title="新增资源">
        {
          !showType &&
          <Fragment>
            {
              this.renderMesos()
            }
            {
              this.renderKubernetes()
            }
            {
              this.renderBazooka()
            }
          </Fragment>
        }
        {
          showType === 'Mesos' && <Mesos onCancel={this.onCancel} />
        }
        {
          showType === 'Bazooka' && <Bazooka onCancel={this.onCancel}/>
        }

      </PageHeaderWrapper>
    );
  }
}

export default connect(({ service }) => ({

}))(AddCluster);
