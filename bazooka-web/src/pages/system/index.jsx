import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { connect } from 'dva';
import { Card, Table, Divider, message, Row, Col, Badge } from 'antd';
import { isAdmin, toHref } from '@/utils/utils';
import router from 'umi/router';
import { CLUSTER_STATUS, GIT_DOMAIN_O } from '@/common/constant';
import GITMODAL from '../service/addService/gitCredentialModal';
import { system } from '@/services/system';
import styles from '@/pages/index.less';
class System extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showGitModal: false,
      currentItem: {}
    };
  }
  componentDidMount() {
    if (!isAdmin()) {
      router.replace('/exception403');
      return
    }
    this.onFetchCredentialsList();
    this.onFetchClusterComponents();
  }
  onFetchCredentialsList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/credentialsList',
    })
  }
  onFetchClusterComponents = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/clusterComponents',
      payload: {
        clusterName: '系统集群'
      }
    })
  }
  onEdit = (record) => {
    this.setState({
      showGitModal: true,
      currentItem: record
    })
  }
  onGitCancel = () => {
    this.setState({
      showGitModal: false,
      currentItem: {}
    })
  }
  //修改凭据
  onAddGitCredentials = async (params) => {
    // const { currentItem } = this.state;
    // let currentParams = { credentialKey: params.credentialKey, credentialValue: params.credentialValue };
    // if (params.credentialValue == '******') {
    //   currentParams = { credentialKey: params.credentialKey }
    // }

    // let res = await system.credentialsUpdate({ id: currentItem.id, ...currentParams });
    // if (res && res.code == '1') {
    //   message.success('修改成功');
      this.onGitCancel();
      this.onFetchCredentialsList()
    // }
  }
  onToHref = (item) => {
    let href = toHref(item.url);
    if(item.name == "MLB") {
      href = toHref(item.url)+":9090/haproxy?stats"
    }
    window.open(href)
  }
  renderCredentialsList() {
    const { credentialsList, loading } = this.props;
    const columns = [
      {
        title: '名称',
        dataIndex: 'credentialName',
      },
      {
        title: '类型',
        dataIndex: 'domain',
        render: (text, record) => (
          <span>{GIT_DOMAIN_O[text]}</span>
        )
      },
      {
        title: '使用域',
        dataIndex: 'scope',
        render: (text) => (
          <span>{text == "GLOBAL" ? '全局' : ''}</span>
        )
      },
      {
        title: '凭据类别',
        dataIndex: 'credentialType',
        render: (text, record) => (
          <span>{text === "USERNAME_WITH_PASSWORD" ? '用户名/密码' : ''}</span>
        )
      },
      {
        title: '值',
        dataIndex: 'credentialValue',
        render: (text, record) => (
          <span>{record.credentialKey}/{text}</span>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <span>
            <a
              onClick={() => {
                this.onEdit(record);
              }}
            >
              修改
              </a>
            {/* <Divider type="vertical" /> */}
            {/* <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.userId)}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm> */}
          </span>
        ),
      },
    ]
    return (
      <Card title="凭据" className={styles.marginT}>
        <Table
          columns={columns}
          dataSource={credentialsList || []}
          // onChange={this.onTableChange}
          loading={loading}
          pagination={false}
          rowKey={record => {
            return record.id;
          }}
        />
      </Card>
    )
  }
  renderSystemInfo() {
    return (
      <Card title="系统基础信息">
        <Row type="flex" align="middle">
          <Col md={7} sm={24}>
            <div className={styles.flexCenter}>
              <p className={`${styles.textBlack} ${styles.stystemInfoTitle}`}>项目：</p>
              <p>上海彩亿信息技术有限公司</p>
            </div>

          </Col>
          <Col md={7} sm={24}>
            <div className={styles.flexCenter}>
              <p className={`${styles.textBlack} ${styles.stystemInfoTitle}`}>版本：</p>
              <p>0.2.1</p>
            </div>
          </Col>
          <Col md={7} sm={0}></Col>
          <Col md={7} sm={24}>
            <div className={styles.flexCenter}>
              <p className={`${styles.textBlack} ${styles.stystemInfoTitle}`}>安装方案：</p>
              <p>本地集群安装</p>
            </div>
          </Col>
          <Col md={7} sm={24}>
            <div className={styles.flexCenter}>
              <p className={`${styles.textBlack} ${styles.stystemInfoTitle}`}>发布时间：</p>
              <p>2019-06-10</p>
            </div>
          </Col>
          <Col md={7} sm={0}></Col>
        </Row>
      </Card>
    )
  }
  renderComponent() {
    const { clusterComponents } = this.props;
    let clusterComponentsList = clusterComponents.clusterComponents || [];
    return (
      <Card title="ATCLOUD系统服务组件" className={styles.marginT}>
        <Row type="flex" align="middle">
          {
            clusterComponentsList.map((item, i) => (
              <Col md={7} key={i} sm={12}>
                <div className={`${styles.flexCenter}`}>
                  {
                    item.status && CLUSTER_STATUS[item.status] &&
                    <p className={styles[CLUSTER_STATUS[item.status].style]} style={{ marginLeft: 0, minWidth: 10 }}></p>
                  }
                  {
                    (item.name == '镜像库' || item.name == "AtaCloud")? 
                    <p>{item.name}</p>:
                    <p><a onClick={() => { this.onToHref(item) }}>{item.name}</a></p>
                  }
                  <p> （</p>
                  <p className={styles.textOverflow}>版本：{item.version}</p>
                  <p>）</p>
                </div>
              </Col>
            ))
          }
        </Row>
      </Card>
    )
  }
  render() {
    const { showGitModal, currentItem } = this.state;
    return (
      <PageHeaderWrapper content="系统基础信息、组件状态和凭据管理">
        {this.renderSystemInfo()}
        {this.renderComponent()}
        {this.renderCredentialsList()}
        {showGitModal && <GITMODAL visible={showGitModal} onCancel={this.onGitCancel} onOk={this.onAddGitCredentials} currentItem={currentItem} />}
      </PageHeaderWrapper>
    );
  }
}
export default connect(({ system, loading }) => ({
  credentialsList: system.credentialsList,
  clusterComponents: system.clusterComponents,
  loading: loading.effects["system/credentialsList"]
}))(System);
