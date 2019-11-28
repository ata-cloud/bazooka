import React, { Fragment } from 'react';
import { Card, Select, Row, Col, Icon, Steps, Button, Table, Empty, Modal, Divider, message } from 'antd';
import { SERVICE_LOG_RESULT_O, DEPLOY_WITH_BUILD } from "@/common/constant";
import { connect } from 'dva';
import ReactJson from 'react-json-view'
import styles from '@/pages/index.less';
import moment from 'moment';
import { service } from '@/services/service';
import { getCurrentTime, NoGitUrl } from '@/utils/utils';

const { Option } = Select;
const { Step } = Steps;
class ServiceBuild extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      buildObj: {},
      currentBranch: {},
      showHistoryModal: false,
      historyDetail: "",
      showBulidLogModal: false,
      currentLog: {},
      currentHistory: {},
      showLogAllScreen: false
    };
  }
  componentDidMount() {
    const { appDeployConfigListAll, appHistoryMarathon, deployTypes, appDeployFlowInfo } = this.props;
    if (!appDeployConfigListAll.length) {
      this.onFetchAppDeployConfigListAll();
    }
    if (Object.getOwnPropertyNames(appHistoryMarathon).length === 0) {
      this.onFetchAppHistoryMarathon();
    }
    if (appDeployFlowInfo.deployConfigId) {
      this.setState({
        buildObj: {
          config: appDeployFlowInfo.deployConfigId,
          deployMode: appDeployFlowInfo.deployMode,
          branch: appDeployFlowInfo.branch,
          dockerImageTag: appDeployFlowInfo.dockerImageTag
        }
      })
    } else {
      this.onFetchAppFlowInfo();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { buildObj } = this.state;
    const { data, appRunStatus } = this.props;
    if (prevState.buildObj && prevState.buildObj.config !== buildObj.config) {
      if (buildObj.deployMode.indexOf("BUILD") > -1) {
        this.onFetchBranch();
        return
      }
      if (buildObj.deployMode == "DOCKER_IMAGE") {
        this.onFetchDockerListAll();
        return
      }
    }
    if (data.envId !== prevProps.data.envId) {
      this.setState({
        buildObj: {},
        currentBranch: {},
      })
      this.onFetchAppDeployConfigListAll();
      this.onFetchAppHistoryMarathon();
      this.onClearData({
        appRunCurrentImage: {},
        appRunEndpoint: [],
        appRunContainers: [],
        appDeployConfigList: {},
        appDeployConfigListAll: [],
        dockerListAll: [],
        appDeployServicePort: {},
        appGetBranch: [],
        appDeployFlow: {},
        appDeployFlowInfo: {}
      })
    }

  }
  componentWillReceiveProps(nextProps) {
    const { appDeployFlow, appDeployStatus, appDeployFlowInfo, appOperate } = nextProps;
    const { currentHistory } = this.state;
    if (appOperate.event && appOperate.event.indexOf("DEPLOY") > -1 && this.props.appOperate !== appOperate) {
      this.onFetchAppDeployFlow(appOperate.eventId)
    }
    if (this.props.appDeployStatus !== appDeployStatus && !appDeployStatus.deploying) {
      this.onFetchAppHistoryMarathon();
      if (appDeployStatus.deployType && appDeployStatus.deployType.indexOf("DEPLOY") > -1) {
        let eventId = appOperate.eventId || appDeployFlowInfo.eventId;
        if (eventId) {
          this.onFetchAppDeployFlow(eventId)
        }
      }
      if (appDeployStatus.deployType == 'ROLLBACK') {
        this.setState({
          currentHistory: {}
        })
      }
    }
    if (this.props.appDeployStatus !== appDeployStatus && appDeployStatus.deployType && appDeployStatus.deployType.indexOf('DEPLOY') > -1 && appDeployStatus.deploying) {
      let eventId = appOperate.eventId && appDeployFlowInfo.eventId;
      if (eventId) {
        this.onFetchAppDeployFlow(eventId);
      } else {
        this.onFetchAppFlowInfo();
      }
    }

    if (this.props.appDeployFlowInfo !== appDeployFlowInfo && appDeployFlowInfo.deployConfigId) {
      this.setState({
        buildObj: {
          config: appDeployFlowInfo.deployConfigId,
          deployMode: appDeployFlowInfo.deployMode,
          branch: appDeployFlowInfo.branch,
          dockerImageTag: appDeployFlowInfo.dockerImageTag
        }
      })
      this.onFetchAppDeployFlow(appDeployFlowInfo.eventId);
    }
  }
  componentWillUnmount() {
    // this.onClearData({
    //   appDeployFlow: {}
    // })
  }
  onClearData = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload
    })
  }
  onFetchAppFlowInfo = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appDeployFlowInfo',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onFetchAppDeployConfigListAll = () => {

    const { dispatch, data } = this.props;
    dispatch({
      type: 'service/appDeployConfigListAll',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
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
  onFetchDockerListAll = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/dockerListAll',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onFetchAppHistoryMarathon = (params = {}) => {
    const { dispatch, data } = this.props;
    dispatch({
      type: 'service/appHistoryMarathon',
      payload: {
        appId: parseInt(data.appId),
        envId: data.envId,
        pageNum: params.pageNum || 1,
        pageSize: 5
      }
    })
  }
  onFetchBranch = () => {
    const { data, dispatch } = this.props;
    const { buildObj } = this.state;
    dispatch({
      type: 'service/appGetBranch',
      payload: {
        appId: data.appId,
        configId: buildObj.config
      }
    })

  }
  onBuildSetChange = (e, option) => {
    const { buildObj } = this.state;
    this.setState({
      buildObj: {
        config: e,
        deployMode: option.props.label,
      }
    })
  }
  onBuildBranchChange = (e, option) => {
    const { buildObj } = this.state;
    this.setState({
      buildObj: {
        ...buildObj,
        branch: e,
      },
      currentBranch: option.props.label
    })
  }
  onBuildChange = (e, type, ) => {
    const { buildObj } = this.state;
    this.setState({
      buildObj: {
        ...buildObj,
        [type]: e,
      }
    })
  }
  onDetail = async (record) => {
    let res = await service.appHistoryMarathonLog({ eventId: record.eventId });
    if (res && res.code == '1') {
      this.setState({
        showHistoryModal: true,
        historyDetail: res.data || ''
      })
    }
  }
  onHistoryModalCancel = () => {
    this.setState({
      showHistoryModal: false
    })
  }
  onBuildLogModalCancel = () => {
    this.setState({
      showBulidLogModal: false,
      showLogAllScreen: false
    })
  }
  onShowLogModal = (item) => {
    this.setState({
      showBulidLogModal: true,
      currentLog: item
    })
  }
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    this.onFetchAppHistoryMarathon({ pageNum: current })
  }
  onToGit = (gitId) => {
    const { appRunStatus } = this.props;
    let baseUrl = `${NoGitUrl(appRunStatus.gitRepository)}/commit/`;
    window.open(baseUrl + gitId)
  }
  //开始发布
  onBeginDeploy = async () => {
    const { buildObj } = this.state;
    const { data, currentEnvO } = this.props;
    let deployType = DEPLOY_WITH_BUILD[buildObj.deployMode] ? DEPLOY_WITH_BUILD[buildObj.deployMode].deployType : "DEPLOY";
    this.onSetDeployTypes(deployType);
    let params = {
      branch: buildObj.branch,
      deployConfigId: buildObj.config,
      dockerImageTag: buildObj.dockerImageTag
    }
    this.setState({
      currentBranch: {}
    })
    this.onAppOperate(deployType, params)
  }
  onAppOperate = (event, detail = {}) => {
    const { data, dispatch } = this.props;
    let parmas = { appId: data.appId, envId: data.envId, detail: JSON.stringify({ ...detail, appId: data.appId, envId: data.envId }), event };
    dispatch({
      type: 'service/appOperate',
      payload: parmas
    })
  }
  onSetDeployTypes = (deployType) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/getAppDeployStatus',
      payload: {
        data: {
          deploying: true,
          deployType
        }
      }
    })
  }
  //获取发布流程
  onFetchAppDeployFlow = async (eventId) => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appDeployFlow',
      payload: {
        eventId
      }
    })
  }
  //回滚
  onRollBack = async (record) => {
    const { data } = this.props;
    this.onSetDeployTypes('ROLLBACK');
    this.setState({
      currentHistory: record
    })
    this.onAppOperate('ROLLBACK', { templateEventId: record.eventId })
  }
  onSetScreen = (type, value) => {
    this.setState({
      [type]: value
    })
  }
  // -------------------------------渲染---------------------------------
  renderBulid() {
    const { appDeployConfigListAll, appGetBranch, deployTypes, appGetBranchLoading, appDeployFlow, dockerListAll, appDeployFlowInfo } = this.props;
    const { buildObj, currentBranch } = this.state;
    let commitInfo = currentBranch.commit || {};
    return (
      <Card title="构建发布">
        <Row type="flex" align="middle">
          <Col className={`${styles.width100} ${styles.textR}`}>
            <label>发布配置: </label>
          </Col>
          <Col className={styles.marginL10}>
            <Select onChange={this.onBuildSetChange} style={{ width: 200 }} value={buildObj.config} placeholder="请选择发布配置" disabled={appDeployFlow.status == "PROCESS" ? true : false}>
              {
                appDeployConfigListAll && appDeployConfigListAll.map((item, i) => (
                  <Option value={item.id} key={item.id} label={item.deployMode}>{item.configName}</Option>
                ))
              }
            </Select>
          </Col>
        </Row>
        {
          buildObj.deployMode && buildObj.deployMode == 'DOCKER_IMAGE' &&
          <Row type="flex" align="middle" className={styles.marginT}>
            <Col className={`${styles.width100} ${styles.textR}`}>
              <label>选择镜像: </label>
            </Col>
            <Col className={styles.marginL10}>
              <Select placeholder="请选择镜像" value={buildObj.dockerImageTag} onChange={(e) => this.onBuildChange(e, 'dockerImageTag')} style={{ width: 200 }} showSearch optionFilterProp="children" disabled={appDeployFlow.status == "PROCESS" ? true : false}>
                {
                  dockerListAll && dockerListAll.map((item) => (
                    <Option value={item.imageTag} key={item.imageTag}>{item.imageTag}</Option>
                  ))
                }
              </Select>
            </Col>
          </Row>
        }
        {
          buildObj.deployMode && buildObj.deployMode.indexOf('BUILD') > -1 &&
          <Row type="flex" align="middle" className={styles.marginT}>
            <Col className={`${styles.width100} ${styles.textR}`}>
              <label>选择代码分支: </label>
            </Col>
            <Col className={styles.marginL10}>
              <Select disabled={appDeployFlow.status == "PROCESS" ? true : false} onChange={this.onBuildBranchChange} value={buildObj.branch} style={{ width: 200 }} placeholder="请选择代码分支" loading={appGetBranchLoading} showSearch optionFilterProp="children">
                {
                  appGetBranch && appGetBranch.map((item, i) => (
                    <Option value={item.name} key={i} label={item}>{item.name}</Option>
                  ))
                }
              </Select>
            </Col>
            {
              commitInfo.id &&
              <Col className={`${styles.marginL} ${styles.marginTopMobile}`}>
                {/* <Icon type="gitlab" /> */}
                <span>此分支最后一次提交信息：{commitInfo.message}</span>
                <span className={styles.marginL}>提交人： {commitInfo.authorName}</span>
                <span className={styles.marginL}>提交时间：{commitInfo.committedDate ? moment(commitInfo.committedDate).format('YYYY-MM-DD HH:mm') : ''}</span>
              </Col>
            }
          </Row>
        }
        <div style={{ margin: '30px 0', marginLeft: 110 }}>
          <Button type="primary" disabled={deployTypes.DEPLOY === 0 || !buildObj.config || (!buildObj.branch && !buildObj.dockerImageTag) ? true : false} loading={deployTypes.DEPLOY === 2 ? true : false} onClick={this.onBeginDeploy}>开始</Button>
        </div>
        {
          appDeployFlow && appDeployFlow.flows ?
            <div className={styles.marginT} style={{ padding: '0 110px' }}>
              <Steps current={appDeployFlow.flows.length} size="small">
                {
                  appDeployFlow.flows.map((item, i) => (
                    <Step title={item.displayName} key={item.deployFlowId} status={item.status === "FAILURE" ? 'error' : ''} description={
                      <div>
                        <a onClick={() => this.onShowLogModal(item)}>查看日志</a>
                        {
                          appDeployFlow.flows.length > 5 ?
                            <p style={{ fontSize: 12 }}>{item.finishDatetime ? moment(item.finishDatetime).format('YYYY-MM-DD HH:mm:ss') : ''}</p> :
                            <p>{item.finishDatetime ? moment(item.finishDatetime).format('YYYY-MM-DD HH:mm:ss') : ''}</p>
                        }

                      </div>
                    } icon={item.status == "PROCESS" ? <Icon type="loading" /> : ''} style={{ maxWidth: 300 }} />
                  ))
                }
              </Steps>
            </div> : null
        }

      </Card>
    )
  }
  renderHistory() {
    const { currentHistory } = this.state;
    const { appHistoryMarathon, appHistoryMarathonLoading, deployTypes } = this.props;
    const columns = [
      {
        title: '类型',
        dataIndex: 'event',
      },
      {
        title: '版本',
        dataIndex: 'version',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
        )
      },
      {
        title: '镜像Tag',
        dataIndex: 'imageTag',
      },
      {
        title: '关联git提交记录',
        dataIndex: 'gitCommitId',
        render: (text, record) => (
          <Fragment>
            {
              text &&
              <a onClick={() => this.onToGit(text)}>
                <span>{text.slice(0, 8)}</span>
                <span>/大约{getCurrentTime(record.gitCommitTime)}</span>
              </a>
            }
          </Fragment>
        )
      },
      {
        title: '结果',
        dataIndex: 'statusCode',
        render: (text, record) => (
          <Fragment>
            {
              text &&
              <span className={styles[SERVICE_LOG_RESULT_O[text].color]}>{record.status}</span>
            }
          </Fragment>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <div>
            <a
              onClick={() => {
                this.onDetail(record);
              }}
            >
              <span>查看详情</span>
            </a>
            {
              record.statusCode === "SUCCESS" &&
              <Fragment>
                <Divider type="vertical" />
                {
                  record.imageIsDelete ?
                    <span className={styles.disabledColor}>镜像已删除</span> :
                    record.isTheLast ?
                      <span className={styles.disabledColor}>当前版本</span> :
                      <Fragment>
                        {
                          currentHistory.eventId == record.eventId ?
                            <Fragment>
                              <Icon type="loading" className={styles.marginR} />
                              <span className={styles.disabledColor}>回滚到此版本</span>
                            </Fragment> :
                            <Fragment>
                              {
                                deployTypes.ROLLBACK === 0 || deployTypes.ROLLBACK === 2 ?
                                  <span className={styles.disabledColor}>回滚到此版本</span> :
                                  <a onClick={() => this.onRollBack(record)}>回滚到此版本</a>
                              }
                            </Fragment>
                        }
                      </Fragment>

                }
              </Fragment>
            }
          </div>
        ),
      },
    ];
    return (
      <Card title="服务运行版本记录" className={styles.marginT}>
        <Table
          // rowSelection={rowSelection}
          columns={columns}
          dataSource={appHistoryMarathon.rows || []}
          onChange={this.onTableChange}
          loading={appHistoryMarathonLoading}
          pagination={{
            // showQuickJumper: true,
            // pageSizeOptions: ['10', '20', '30', '50'],
            total: appHistoryMarathon.totalCount || 0,
            showTotal: (total, range) =>
              `共${appHistoryMarathon.totalCount || 0}条，当前${appHistoryMarathon.pageNum ? appHistoryMarathon.pageNum : 1}/${
              appHistoryMarathon.totalPage ? appHistoryMarathon.totalPage : 1
              }页`,
            // showSizeChanger: true,
            current: appHistoryMarathon.pageNum ? appHistoryMarathon.pageNum : 1,
            pageSize: appHistoryMarathon.pageSize ? appHistoryMarathon.pageSize : 10,
          }}
          rowKey={record => {
            return record.eventId;
          }}
        />
      </Card>
    )
  }
  renderHistoryModal() {
    const { showHistoryModal, historyDetail } = this.state;
    return (
      <Modal
        title="详情"
        footer={null}
        width={800}
        visible={showHistoryModal}
        onCancel={this.onHistoryModalCancel}
      >
        <div className={styles.modalScroll}>
          {
            historyDetail ?
              <ReactJson src={JSON.parse(historyDetail)} name={false} /> :
              <Empty />
          }
        </div>
      </Modal>
    )
  }
  renderBuildLogModal() {
    const { showBulidLogModal, currentLog, showLogAllScreen } = this.state;
    return (
      <Modal
        title={
          <Row type="flex" align="middle">
            <Col className={styles.marginR}>操作详情</Col>
            <Col className={styles.iconAllScreen}>
              {
                showLogAllScreen ?
                  <Icon type="shrink" className={styles.iconAll} onClick={() => this.onSetScreen('showLogAllScreen', false)} /> :
                  <Icon type="arrows-alt" className={styles.iconAll} onClick={() => this.onSetScreen('showLogAllScreen', true)} />
              }
            </Col>
          </Row>
        }
        footer={null}
        width={showLogAllScreen ? '100vw' : 800}
        visible={showBulidLogModal}
        onCancel={this.onBuildLogModalCancel}
        className={showLogAllScreen ? styles.modalAll : null}
        style={showLogAllScreen ? { top: 0, paddingBottom: 0 } : null}
      >
        <div className={`${styles.modalScroll} ${showLogAllScreen ? styles.height100 : null}`}>
          <pre className={styles.preWarp}>
            {currentLog.log}
          </pre>
        </div>
      </Modal>
    )
  }
  render() {
    const { showHistoryModal, showBulidLogModal } = this.state;
    return (
      <div>
        {this.renderBulid()}
        {this.renderHistory()}
        {showHistoryModal && this.renderHistoryModal()}
        {showBulidLogModal && this.renderBuildLogModal()}
      </div>
    );
  }
}

export default connect(({ service, loading }) => ({
  currentEnvO: service.currentEnvO,
  appDeployConfigListAll: service.appDeployConfigListAll,
  appHistoryMarathon: service.appHistoryMarathon,
  appGetBranch: service.appGetBranch,
  appGetBranchLoading: loading.effects['service/appGetBranch'],
  appRunStatus: service.appRunStatus,
  deployTypes: service.deployTypes,
  appDeployFlow: service.appDeployFlow,
  dockerListAll: service.dockerListAll,
  appDeployStatus: service.appDeployStatus,
  appDeployFlowInfo: service.appDeployFlowInfo,
  appOperate: service.appOperate,
  appHistoryMarathonLoading: loading.effects['service/appHistoryMarathon']
}))(ServiceBuild);

