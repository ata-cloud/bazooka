import React, { Fragment } from 'react';
import { Card, Row, Col, Descriptions, Timeline, Spin, Empty, Icon, Modal, Radio } from 'antd';
import { connect } from 'dva';
import stylesService from "../index.less";
import { CARD_TITLE_BG } from "@/common/constant";
import styles from '@/pages/index.less';
import Mirror from './mirrorM';
import { MformG, getCurrentTime, isAdmin } from '@/utils/utils';
import { APP_STATUS } from '@/common/constant';
import { service } from '@/services/service';
import router from 'umi/router';
import moment from 'moment';
class ServiceBasic extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      logDetail: [],
      currentLog: {},
      showLogDetail: false,
      showAllScreen: false
    };
  }
  componentDidMount() {
    const { appRunStatus, appHistory } = this.props;
    if (Object.getOwnPropertyNames(appRunStatus).length === 0) {
      this.onFetchAppRunStatus();
    }
    if (Object.getOwnPropertyNames(appHistory).length === 0) {
      this.onFethAppHistory();
    }
  }
  componentWillReceiveProps(nextProps) {
    const { appDeployStatus } = nextProps;
    if (this.props.appDeployStatus !== appDeployStatus && appDeployStatus.id && !appDeployStatus.deploying) {
      this.onFethAppHistory();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { data, appRunStatus } = this.props;
    if (data.envId !== prevProps.data.envId) {
      this.onFethAppHistory();
      this.onClearData({
        appRunCurrentImage: {},
        appRunEndpoint: [],
        appRunContainers: [],
        appDeployConfigList: {},
        dockerListAll: [],
        appHistoryMarathon: {},
        appDeployServicePort: {},
        appGetBranch: [],
        appDeployConfigListAll: [],
        // appDeployStatus: {},
        // deployTypes: {// 0:禁用 1：可用  2：loading
        //   "START": 1,
        //   "RESTART": 1,
        //   "SCALE": 1,
        //   "STOP": 1,
        //   "DEPLOY": 1,
        //   "ROLLBACK": 1,
        // },
        appDeployFlow: {},
        appOperate: {}
      })
    }
    // if (prevProps.appRunStatus !== appRunStatus) {
    //   this.onFethAppHistory();
    // }
  }
  onFetchAppRunStatus = async () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appRunStatus',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onFethAppHistory = (params = {}) => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appHistory',
      payload: {
        appId: data.appId,
        envId: data.envId,
        pageNum: params.pageNum || 1,
        pageSize: 5
      }
    })
  }
  onHistoryMore = () => {
    const { appHistory } = this.props;
    if (appHistory.totalPage == 0 || appHistory.totalPage === appHistory.pageNum) {
      return
    }
    this.onFethAppHistory({ pageNum: appHistory.pageNum + 1 })
  }
  onRouteTo = (pathname, query) => {
    router.push({
      pathname,
      query
    })
  }
  onToGit = (url) => {
    window.open(url)
  }
  //查看详情
  onGetLogDetail = async (item) => {
    let res = await service.appOperateLog({ eventId: item.eventId });
    if (res && res.code == '1') {
      let data = res.data || [];
      this.setState({
        showLogDetail: true,
        logDetail: data,
        currentLog: data[0] || {}
      })
    }
  }
  onLogTypeChange = (e) => {
    this.setState({
      currentLog: e.target.value
    })
  }
  onLogModalCancel = () => {
    this.setState({
      showLogDetail: false,
      currentLog: {},
      showAllScreen: false
    })
  }
  onClearData = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload
    })
  }
  onSetScreen = (type, value) => {
    this.setState({
      [type]: value
    })
  }
  renderBasic() {
    const { appRunStatus, data } = this.props;
    return (
      <Row gutter={48}>
        <Col md={8} sm={24}>
          <Card title="服务状态">
            <div className={stylesService.basicItemH}>
              {
                appRunStatus.status &&
                <p className={`${styles[APP_STATUS[appRunStatus.status].color]} ${stylesService.textStatus}`}>
                  {APP_STATUS[appRunStatus.status].text}
                </p>
              }
            </div>

          </Card>
        </Col>
        <Col md={8} sm={24}>
          <Card title="资源使用情况">
            <div className={stylesService.basicItemH}>
              <p className={stylesService.textSource}>（{appRunStatus.cpu ? appRunStatus.cpu : 0} Core | {appRunStatus.memory ? MformG(appRunStatus.memory) : 0} GiB）* {appRunStatus.instances || 0}</p>
              <p>（CPU | 内存）* 实例个数</p>
            </div>
          </Card>
        </Col>
        <Col md={8} sm={24}>
          <Card title="运行环境">
            <div className={stylesService.basicItemHOnly}>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>代码：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`} title={appRunStatus.gitRepository}>
                  <a onClick={() => this.onToGit(appRunStatus.gitRepository)}>{appRunStatus.gitRepository}</a>
                </p>
              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>项目：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`} title={appRunStatus.projectName}>
                  <a onClick={() => { this.onRouteTo('/project/detail', { projectId: appRunStatus.projectId }) }}>{appRunStatus.projectName}</a>
                </p>

              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>环境：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`} title={appRunStatus.envName}>
                  <a onClick={() => { this.onRouteTo('/environment') }}>{appRunStatus.envName}</a>
                </p>
              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>集群：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`} title={appRunStatus.clusterName}>
                  {
                    isAdmin() ?
                      <a onClick={() => { this.onRouteTo('/cluster/detail', { clusterId: appRunStatus.clusterId }) }}>{appRunStatus.clusterName}</a> :
                      <span>{appRunStatus.clusterName}</span>
                  }
                </p>
              </div>
              {/* <Descriptions column={1} className={styles.descriptionsTtem}>
                <Descriptions.Item label="代码">
                  <a onClick={() => this.onToGit(appRunStatus.gitRepository)} className={styles.textOverflow} title={appRunStatus.gitRepository}>{appRunStatus.gitRepository}</a>
                </Descriptions.Item>
                <Descriptions.Item label="项目">
                  <a onClick={() => { this.onRouteTo('/project/detail', { projectId: appRunStatus.projectId }) }}>{appRunStatus.projectName}</a></Descriptions.Item>
                <Descriptions.Item label="环境">
                  <a onClick={() => { this.onRouteTo('/environment') }}>{appRunStatus.envName}</a>
                </Descriptions.Item>
                <Descriptions.Item label="集群">
                  <a onClick={() => { this.onRouteTo('/cluster/detail', { clusterId: appRunStatus.clusterId }) }}>{appRunStatus.clusterName}</a>
                </Descriptions.Item>
              </Descriptions> */}
            </div>

          </Card>
        </Col>
      </Row>
    )
  }
  //操作日志
  renderLog() {
    const { appHistory, historyLoading } = this.props;
    return (
      <Card title="操作日志" className={`${styles.marginT}`}>
        <Spin spinning={historyLoading}>
          {
            appHistory && appHistory.rows && !appHistory.rows.length ?
              <div>
                <Empty />
              </div> :
              <Fragment>
                <Timeline className={stylesService.serviceTimeLine}>
                  {
                    appHistory && appHistory.rows && appHistory.rows.map((item, i) => (
                      <div className={styles.flex} key={i}>
                        <div className={stylesService.timeLineLeft}>
                          <div>{item.operateDatetime ? moment(item.operateDatetime).format('HH:mm:ss') : ''}</div>
                          <div>{getCurrentTime(item.operateDatetime)}</div>
                        </div>
                        <Timeline.Item className={stylesService.timtLineItem} dot={item.status == 'PROCESS' ? <Icon type="loading" /> : null} color={item.status == 'FAILURE' ? 'red' : 'green'}>
                          <div className={stylesService.timeRight}>
                            <Row type="flex" justify="space-between" align="middle" gutter={24}>
                              <Col>
                                <div>{`【${item.operator}】 ${item.event}${item.status === "FAILURE" ? '失败' : item.status === "SUCCESS" ? '成功' : ''} ${item.remark ? item.remark : ''}`}</div>
                              </Col>
                              <Col>
                                <a onClick={() => { this.onGetLogDetail(item) }}>查看详情</a>
                              </Col>
                            </Row>
                          </div>
                        </Timeline.Item>
                      </div>
                    ))
                  }
                </Timeline>
                {
                  appHistory && (appHistory.pageNum !== appHistory.totalPage || !appHistory.totalPage) &&
                  <div className={styles.textC}>
                    <a onClick={this.onHistoryMore}>
                      <span>查看更多</span>
                      <Icon type="down" style={{ fontSize: 12 }} />
                    </a>
                  </div>
                }
              </Fragment>
          }
        </Spin>

      </Card>
    )
  }
  renderLogModal() {
    const { logDetail, currentLog, showLogDetail, showAllScreen } = this.state;
    return (
      <Modal
        title={
          <Row type="flex" align="middle">
            <Col className={styles.marginR}>操作详情</Col>
            <Col>
              <Radio.Group buttonStyle="solid" onChange={this.onLogTypeChange} value={currentLog} size="small">
                {
                  logDetail && logDetail.map((item, i) => (
                    <Radio.Button value={item} key={i}>{item.type}</Radio.Button>
                  ))
                }
              </Radio.Group>
            </Col>
            <Col className={styles.iconAllScreen}>
              {
                showAllScreen ?
                  <Icon type="shrink" className={styles.iconAll} onClick={() => this.onSetScreen('showAllScreen', false)} /> :
                  <Icon type="arrows-alt" className={styles.iconAll} onClick={() => this.onSetScreen('showAllScreen', true)} />
              }
            </Col>
          </Row>
        }
        visible={showLogDetail}
        footer={null}
        width={showAllScreen ? '100vw' : 800}
        onCancel={this.onLogModalCancel}
        className={showAllScreen ? styles.modalAll : null}
        style={showAllScreen ? { top: 0, paddingBottom: 0 } : null}
      >
        <div className={`${styles.modalScroll} ${showAllScreen ? styles.height100 : null}`}>
          {
            currentLog.content ?
              <pre className={styles.preWarp}>
                {currentLog.content}
              </pre> :
              <Empty />
          }
        </div>
      </Modal>
    )
  }
  render() {
    const { showLogDetail } = this.state;
    return (
      <div>
        {
          this.renderBasic()
        }
        {
          this.renderLog()
        }
        {
          showLogDetail && this.renderLogModal()
        }
      </div>
    );
  }
}
export default connect(({ service, loading }) => ({
  appRunStatus: service.appRunStatus,
  appHistory: service.appHistory,
  appDeployStatus: service.appDeployStatus,
  historyLoading: loading.effects["service/appHistory"]
}))(ServiceBasic);
