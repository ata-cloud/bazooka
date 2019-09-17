import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Input, Button, Icon, Typography, Progress, Popconfirm, Form, message, Spin } from 'antd';
import Link from 'umi/link';
import styles from '../index.less';
import EditModal from './EditModal';
import { ENV_STATUS_OBJECT } from '@/common/constant';
import { env } from '@/services/env';
import { connect } from 'dva';
import { MformG, isAdmin } from '@/utils/utils';
const { Search } = Input;
const { Title, Text } = Typography;
class Env extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      resourceType: [
        {
          name: 'CPU',
          value: '',
          unit: 'Shares',
        },
        {
          name: '磁盘',
          value: '',
          unit: 'GiB',
        },
        {
          name: '内存',
          value: '',
          unit: 'GiB',
        },
      ],
      searchObj: {},
      showEdit: false,
      currentItem: {},
      confirmLoading: false
    };
  }
  componentDidMount() {
    this.onFetchList();
  }
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (prevState.searchObj !== searchObj) {
      this.onFetchList()
    }
  }
  onFetchList = () => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'env/envList',
      payload: searchObj
    })
  }

  onSearch = (e) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        keyword: e
      }
    })
  }
  onAdd = (item) => {
    this.setState({
      currentItem: item || {}
    }, () => {
      this.setState({
        showEdit: true
      })
    })
  }
  onDelete = async (item) => {
    let res = await env.envDeltete({ envId: item.id });
    if (res && res.code == '1') {
      message.success('删除成功');
      this.onFetchList();
    }
  }
  onCancel = () => {
    this.setState({
      showEdit: false,
      currentItem: {},
      confirmLoading: false
    })
  }
  onOk = async (values) => {
    const { currentItem } = this.state;
    this.setState({
      confirmLoading: true
    })
    if (currentItem.id) {
      let res = await env.envUpdate({ envId: currentItem.id, ...values });
      this.setState({
        confirmLoading: false
      })
      if (res && res.code === '1') {
        message.success('修改成功');
        this.onCancel();
        this.onFetchList();
      }
    } else {
      let res = await env.envCreate(values);
      this.setState({
        confirmLoading: false
      })
      if (res && res.code === '1') {
        message.success('添加成功');
        this.onCancel();
        this.onFetchList();
      }
    }
  }
  //------------------------页面渲染-------------------------------------
  renderTitle() {
    return (
      // <Row type="flex" justify="space-between" align="middle">
      //   <Col>环境用于按功能分隔<Link to="/cluster">集群</Link>的计算资源，比如分为“测试环境”、“预发布环境”、“生产环境”等，各个服务将部署在所属项目已关联的环境中</Col>
      //   <Col className={styles.flexCenter}>
      //     <Search placeholder="搜索环境" onSearch={this.onSearch} />
      //     <Button type="primary" className={styles.marginL} onClick={this.onAdd}>+ 新增环境</Button>
      //   </Col>
      // </Row>
      <div>
        <p className={styles.marginB}>
          <span>环境用于按功能分隔</span>
          {
            isAdmin() ?
            <Link to="/cluster">集群</Link>:
            <span>集群</span>
          }
          <span>的计算资源，比如分为“测试环境”、“预发布环境”、“生产环境”等，各个服务将部署在所属项目已关联的环境中</span></p>
        <Row type="flex" justify="space-between" align="middle">
          <Col>
            {
              isAdmin() && <Button type="primary" onClick={this.onAdd}>+ 新增环境</Button>
            }
          </Col>
          <Col>
            <Search placeholder="搜索环境" onSearch={this.onSearch} />
          </Col>
        </Row>
      </div>
    )
  }
  renderResource(item) {
    return (
      <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
        <div className={styles.leftBlock}>{item.name}</div>
        <div className={`${styles.flex1} ${styles.processLR}`}>
          <Progress percent={50} showInfo={false} />
        </div>
        <div className={styles.rightBlock}>50%（135 / 144 {item.unit}）</div>
      </div>
    )
  }
  renderList() {
    const { resourceType } = this.state;
    const { list, loading } = this.props;
    return (
      <Row gutter={24}>
        {
          list && list && !list.length && !loading &&
          <Col span={8}>
            {
              isAdmin() &&
              <Button type="dashed" className={styles.listItem} onClick={this.onAdd}>
                <Icon type="plus" />
                <span>新增环境</span>
              </Button>
            }
          </Col>
        }
        {
          list && list.map((item, i) => (
            <Col md={12} key={i} className={styles.marginB} sm={24}>
              <Card title={
                <strong>{item.name}</strong>
              } extra={
                <Fragment>
                  {
                    isAdmin() &&
                    <Fragment>
                      <Icon type="setting" style={{ fontSize: 20 }} onClick={() => { this.onAdd(item) }} />
                      {
                        item.projectNum === 0 &&
                        <Popconfirm
                          title="确定删除吗?"
                          onConfirm={() => this.onDelete(item)}
                          okText="确定"
                          cancelText="取消"
                        >
                          <Icon type="close" style={{ fontSize: 16, marginLeft: 15 }} />
                        </Popconfirm>
                      }
                    </Fragment>

                  }
                </Fragment>
              }>
                <div className={styles.flex}>
                  <div className={styles.clusterItem}>
                    <p>所属集群</p>
                    {
                      isAdmin() ? 
                      <Link to={{ pathname: '/cluster/detail', query: { clusterId: item.clusterId } }}>{item.clusterName}</Link>:
                      <span>{item.clusterName}</span>
                    }
                   
                  </div>
                  <div className={styles.clusterItem}>
                    <p>使用此环境的项目</p>
                    <strong>{item.projectNum ? item.projectNum : 0}</strong>
                  </div>
                  <div className={styles.clusterItem}>
                    <p>环境状态</p>
                    {
                      ENV_STATUS_OBJECT[item.state] &&
                      <strong className={styles[ENV_STATUS_OBJECT[item.state].color]}>{ENV_STATUS_OBJECT[item.state].text}</strong>
                    }

                  </div>
                </div>
                <div className={styles.marginT}>
                  <p>环境资源（服务使用中 / 环境总资源）</p>
                  <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                    <div className={styles.leftBlock}>CPU</div>
                    <div className={`${styles.flex1} ${styles.processLR}`}>
                      <Progress percent={item.cpus ? parseInt(item.cpusUsed / item.cpus * 100) : 0} showInfo={false} />
                    </div>
                    <div className={styles.rightBlock}>{`${item.cpus ? parseInt(item.cpusUsed / item.cpus * 100) : 0}`}%（{item.cpusUsed} / {item.cpus} Core）</div>
                  </div>
                  <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                    <div className={styles.leftBlock}>内存</div>
                    <div className={`${styles.flex1} ${styles.processLR}`}>
                      <Progress percent={item.memory ? parseInt(item.memoryUsed / item.memory * 100) : 0} showInfo={false} />
                    </div>
                    <div className={styles.rightBlock}>{`${item.memory ? parseInt(item.memoryUsed / item.memory * 100) : 0}`}%（{MformG(item.memoryUsed)} / {MformG(item.memory)} GiB）</div>
                  </div>
                  <div className={`${styles.flexCenter}`} style={{ margin: '10px 20px' }}>
                    <div className={styles.leftBlock}>磁盘</div>
                    <div className={`${styles.flex1} ${styles.processLR}`}>
                      <Progress percent={item.disk ? parseInt(item.diskUsed / item.disk * 100) : 0} showInfo={false} />
                    </div>
                    <div className={styles.rightBlock}>{`${item.disk ? parseInt(item.diskUsed / item.disk * 100) : 0}`}%（{MformG(item.diskUsed)} / {MformG(item.disk)} GiB）</div>
                  </div>
                  {/* {
                    resourceType.map((item, i) => (
                      <Fragment key={i}>
                        {this.renderResource(item)}
                      </Fragment>

                    ))
                  } */}
                </div>
              </Card>
            </Col>
          ))
        }
      </Row>
    )
  }
  render() {
    const { showEdit, currentItem, confirmLoading } = this.state;
    const { loading } = this.props;
    return (
      <PageHeaderWrapper content={this.renderTitle()}>
        <Spin spinning={loading}>
          {this.renderList()}
        </Spin>

        {showEdit && <EditModal visible={showEdit} currentItem={currentItem} onCancel={this.onCancel} onOk={this.onOk} confirmLoading={confirmLoading} />}
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ env, cluster, loading }) => ({
  list: env.envList,
  loading: loading.effects["env/envList"]
}))(Env));
