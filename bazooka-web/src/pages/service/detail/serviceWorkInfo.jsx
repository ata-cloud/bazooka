import React, { Fragment } from 'react';
import { Card, Row, Col, Descriptions, Radio, Table, Icon, Form, Select, Button, message, Modal, Spin, Input } from 'antd';
import { CARD_TITLE_BG, SCALE_CPU, SCALE_MEN, SCALE_CASE, CONTAINTER_STATUS_O, HEALTH_STATUS_O } from "@/common/constant";
import styles from '@/pages/index.less';
import stylesService from "../index.less";
import { connect } from 'dva';
import moment from 'moment';
import { getCurrentTime, MformG, NoGitUrl } from '@/utils/utils';
import { service } from '@/services/service';
import ImagePushModal from './imagePushModal'
const FormItem = Form.Item;
const { Option } = Select;
class ServiceWorkInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showPushModal: false,
      pushLoading: false,
      currentItem: {},
      currentContainers: [],
      running: ['TASK_STAGING', 'TASK_STARTING', 'TASK_RUNNING', 'TASK_UNREACHABLE', 'TASK_KILLING', 'TASK_LOST'],
      colosed: ['TASK_FINISHED', 'TASK_KILLED', 'TASK_FAILED'],
      showLogModal: false,
      currentLogType: '',
      currentLogItem: {},
      stdout: { log: [] },
      strerr: { log: [] },
      logDetail: [],
      isBottom: true,
      logTopLoading: false,
      containerType: '1',
      showLogAllScreen: false
    };
  }
  componentDidMount() {
    const { appRunEndpoint, appRunContainers, appRunCurrentImage } = this.props;
    const { running } = this.state;
    if (!appRunEndpoint.length) {
      this.onFetchAppRunEndpoint();
    }
    this.onFetchAppRunContainers()
    this.timmerContainer = setInterval(() => {
      this.onFetchAppRunContainers()
    }, 5000)
    if (Object.getOwnPropertyNames(appRunCurrentImage).length === 0) {
      this.onFetchAppRunCurrentImage();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { data, appRunStatus, appRunContainers } = this.props;
    const { currentLogType, stdout, strerr, isBottom, containerType } = this.state;
    if (data.envId !== prevProps.data.envId) {
      this.onFetchAppRunEndpoint();
      this.onFetchAppRunCurrentImage();
      this.onFetchAppRunContainers();
      this.onClearData({
        appDeployConfigList: {},
        dockerListAll: [],
        appHistory: {},
        appDeployConfigListAll: [],
        appHistoryMarathon: {},
        appDeployServicePort: {},
        appGetBranch: [],
        // appDeployStatus: {},
        appDeployFlow: {},
      })
    }
    if (prevProps.appRunContainers !== appRunContainers) {
      this.onContainersChange(containerType)
      // this.setState({
      //   currentContainers: appRunContainers.filter(item => running.indexOf(item.status) > -1)
      // })
    }
    if (prevState.stdout.log && stdout.log && stdout.log.length !== prevState.stdout.log.length) {
      if (isBottom && this._container && this._container.scrollTo) {
        this._container.scrollTo(0, this._container.scrollHeight)
      }
    }
    // if (prevState.strerr.log && strerr.log && strerr.log.length !== prevState.strerr.log.length) {
    //   if (isBottom && this._container && this._container.scrollTo) {
    //     this._container.scrollTo(0, this._container.scrollHeight)
    //   }
    // }
    if (prevState.currentLogType !== currentLogType) {
      if (currentLogType === 'stdout') {
        if (this._container && this._container.scrollTo) {
          this._container.scrollTo(0, this._container.scrollHeight)
        }
        clearInterval(this.timmerstrerr)
        this.onFetchLogDetailBottom()
        this.timmerstdout = setInterval(() => {
          this.onFetchLogDetailBottom()
        }, 3000)
        return
      }
      if (currentLogType === 'strerr') {
        clearInterval(this.timmerstdout)
        // this.onFetchLogDetailBottom()
        this.timmerstrerr = setInterval(() => {
          this.onFetchLogDetailBottom()
        }, 3000)
        return
      }
    }
  }
  componentWillReceiveProps(nextProps) {
    const { appDeployStatus } = nextProps;
    const { running, containerType } = this.state;
    if (this.props.appDeployStatus !== appDeployStatus && appDeployStatus.id && !appDeployStatus.deploying) {
      this.onFetchAppRunContainers();
      if (this.props.appDeployStatus.deployType) {
        if (appDeployStatus.deployType == 'SCALE') {
          message.success('扩缩容成功');
          return
        }
        if (appDeployStatus.deployType == 'PUSH_IMAGE') {
          message.success('推送成功');
          return
        }
      }
    }
  }
  componentWillUnmount() {
    clearInterval(this.timmerstdout)
    clearInterval(this.timmerstrerr)
    clearInterval(this.timmerContainer)
  }
  onClearData = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload
    })
  }
  onFetchAppRunCurrentImage = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appRunCurrentImage',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onFetchAppRunEndpoint = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appRunEndpoint',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onFetchAppRunContainers = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appRunContainers',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  //修改扩缩容
  onScaleSubmit = (e) => {
    const { appRunStatus } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (appRunStatus.cpu == values.cpu && appRunStatus.memory == values.memory && appRunStatus.instances == values.instance) {
          message.info('参数的值没有变化哦~')
          return
        }
        this.onFetchScaleSet(values)
      }
    });
  };
  onFetchScaleSet = async (values) => {
    this.onSetDeployTypes('SCALE');
    this.onFetchAppOperate('SCALE', { ...values, disk: 0 });
    // this.onClearData({
    //   appHistory: {},
    //   appHistoryMarathon: {},
    // })
  }
  onFetchAppOperate = async (deployType, detail = {}) => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appOperate',
      payload: {
        event: deployType,
        appId: data.appId,
        envId: data.envId,
        detail: JSON.stringify({ appId: data.appId, envId: data.envId, ...detail })
      }
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
  onToGit = (gitId) => {
    const { appRunCurrentImage } = this.props;
    let baseUrl = `${NoGitUrl(appRunCurrentImage.gitUrl)}/commit/`;
    window.open(baseUrl + gitId)
  }
  onSubmit = (values) => {
    const { appRunCurrentImage } = this.props;
    this.setState({
      pushLoading: true
    })
    this.onFetchAppOperatePushImage('PUSH_IMAGE', { ...values, dockerImageTag: appRunCurrentImage.imageTag, imageId: appRunCurrentImage.id })
  };
  //镜像推送
  onFetchAppOperatePushImage = async (event, detail = {}) => {
    const { data } = this.props;
    let parmas = { appId: data.appId, envId: data.envId, detail: JSON.stringify({ ...detail, appId: data.appId, envId: data.envId }), event };
    let res = await service.appOperate(parmas);
    if (res && res.code == '1') {
      this.timmer = setInterval(() => {
        this.onFetchStatus(res.data.eventId);
        this.onGetLogDetail(res.data.eventId);
      }, 3000)
    } else {
      this.setState({
        pushLoading: false
      })
    }
  }
  //获取镜像推送进度
  onFetchStatus = async (eventId) => {
    let res = await service.appOperateStatus({ eventId });
    if (res && res.code == '1') {
      if (res.data.status == 'SUCCESS') {
        clearInterval(this.timmer);
        this.setState({
          pushLoading: false
        })
        message.success('推送成功');
        this.onGetLogDetail(eventId)
        return
      }
      if (res.data.status == 'FAILURE') {
        clearInterval(this.timmer);
        this.setState({
          pushLoading: false
        })
        message.error('推送失败');
        this.onGetLogDetail(eventId)
        return
      }
    } else {
      clearInterval(this.timmer);
    }
  }
  //查看镜像推送详情
  onGetLogDetail = async (eventId) => {
    let res = await service.appOperateLog({ eventId });
    if (res && res.code == '1') {
      let data = res.data || [];
      this.setState({
        logDetail: data
      })
    }
  }
  onShowPushModal = () => {
    this.setState({
      showPushModal: true
    })
  }
  onCancel = () => {
    clearInterval(this.timmer)
    this.setState({
      showPushModal: false,
      logDetail: [],
    })
  }
  onContainersChange = (type) => {
    const { appRunContainers } = this.props;
    const { running, colosed } = this.state;
    let containersList = [];
    switch (type) {
      case '1': {
        containersList = appRunContainers.filter(item => running.indexOf(item.status) > -1)
      } break;
      case '2': {
        containersList = appRunContainers.filter(item => colosed.indexOf(item.status) > -1)
      } break;
      default: {
        containersList = appRunContainers
      }
    }
    this.setState({
      currentContainers: containersList,
      containerType: type
    })
  }
  onShowLogDetail = (item) => {
    this.setState({
      showLogModal: true,
      currentLogItem: item,
      currentLogType: 'stdout'
    })
  }
  onLogModalCancel = () => {
    clearInterval(this.timmerstdout)
    clearInterval(this.timmerstrerr)
    this.setState({
      showLogModal: false,
      stdout: { log: [] },
      strerr: { log: [] },
      currentLogType: '',
      isBottom: true,
      showLogAllScreen: false
    })
  }
  onLogTypeChange = (e) => {
    this.setState({
      currentLogType: e.target.value
    })
  }
  onFetchLogDetailBottom = async () => {
    const { currentLogType, currentLogItem, stdout, strerr } = this.state;
    let res = await service.getClusterDockerInstanceLog({
      logType: currentLogType === 'stdout' ? '0' : '1',
      length: 10000,
      clusterId: currentLogItem.clusterId,
      slaveId: currentLogItem.slaveId,
      taskId: currentLogItem.taskId,
      containerId: currentLogItem.containerId,
      frameworkId: currentLogItem.frameworkId,
      offset: this.state[currentLogType].downOffset ? this.state[currentLogType].downOffset : undefined,
      upOffset: this.state[currentLogType].upOffset ? this.state[currentLogType].upOffset : this.state[currentLogType].upOffset == 0 ? 0 : undefined,
      downOffset: this.state[currentLogType].downOffset ? this.state[currentLogType].downOffset + 10000 : undefined,
    });
    if (res && res.code == '1') {
      let result = res.data || {};
      if (result.log) {
        this.setState({
          [currentLogType]: {
            upOffset: result.upOffset,
            downOffset: result.downOffset,
            log: [
              ...this.state[currentLogType].log,
              result.log
            ]
          }
        })
      }
    } else {
      clearInterval(this[`timmer${currentLogType}`])
      // this.setState({
      //   [currentLogType]: {
      //     upOffset: undefined,
      //     downOffset: undefined,
      //     log: []
      //   }
      // })
    }
  }
  onFetchLogDetailTop = async () => {
    const { currentLogType, currentLogItem, stdout, strerr } = this.state;
    if (this.state[currentLogType].upOffset == 0) {
      return
    }
    this.setState({
      logTopLoading: true
    })
    let res = await service.getClusterDockerInstanceLog({
      logType: currentLogType === 'stdout' ? '0' : '1',
      length: this.state[currentLogType].upOffset - 10000 < 10000 ? this.state[currentLogType].upOffset - 10000 : 10000,
      clusterId: currentLogItem.clusterId,
      slaveId: currentLogItem.slaveId,
      taskId: currentLogItem.taskId,
      containerId: currentLogItem.containerId,
      frameworkId: currentLogItem.frameworkId,
      offset: this.state[currentLogType].upOffset ? this.state[currentLogType].upOffset == 0 ? 0 : this.state[currentLogType].upOffset - 10000 < 10000 ? 0 : this.state[currentLogType].upOffset - 10000 : undefined,
      upOffset: this.state[currentLogType].upOffset ? this.state[currentLogType].upOffset == 0 ? 0 : this.state[currentLogType].upOffset - 10000 < 10000 ? 0 : this.state[currentLogType].upOffset - 10000 : undefined,
      downOffset: this.state[currentLogType].downOffset ? this.state[currentLogType].downOffset : undefined,
    });
    this.setState({
      logTopLoading: false
    })
    this._container.scrollTo(0, 1)
    if (res && res.code == '1') {
      let result = res.data || {};
      if (result.log) {
        this.setState({
          [currentLogType]: {
            upOffset: result.upOffset,
            downOffset: result.downOffset,
            log: [
              result.log,
              ...this.state[currentLogType].log,
            ]
          }
        })
      }
    }
  }
  onScroll = () => {
    const { isBottom } = this.state;
    let scrollTop = this._container.scrollTop;
    if (scrollTop == 0) {
      if (isBottom) {
        this.setState({
          isBottom: false
        })
      }
      this.onFetchLogDetailTop();
    }
    if (scrollTop + this._container.clientHeight == this._container.scrollHeight) {
      if (!isBottom) {
        this.setState({
          isBottom: true
        })
      }
    } else {
      if (isBottom) {
        this.setState({
          isBottom: false
        })
      }
    }
  }
  onHref = (href) => {
    if (href.indexOf('http') > -1) {
      window.open(href)
      return
    }
    window.open(`http://${href}`)
  }
  onSetScreen = (type, value) => {
    this.setState({
      [type]: value
    })
  }

  renderPushModal() {
    const { showPushModal, pushLoading, currentItem, logDetail } = this.state;
    const { appRunCurrentImage } = this.props;
    return (
      <ImagePushModal visible={showPushModal} confirmLoading={pushLoading} currentItem={appRunCurrentImage} onCancel={this.onCancel} onSubmit={this.onSubmit} logDetail={logDetail} />
    )
  }
  renderTop() {
    const { appRunCurrentImage, appRunEndpoint, currentEnvO } = this.props;
    return (
      <Row gutter={48}>
        <Col md={12} sm={24}>
          <Card title="当前服务版本">
            <div className={stylesService.basicItemHOnly}>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>镜像：</p>
                <div className={`${styles.flex1} ${styles.flexCenter}`} style={{ width: '80%' }}>
                  {
                    appRunCurrentImage.registry &&
                    <p className={`${styles.textOverflow} ${styles.marginR}`} title={`${appRunCurrentImage.registry}/${appRunCurrentImage.imageName}:${appRunCurrentImage.imageTag}`}>
                      {appRunCurrentImage.registry}/{appRunCurrentImage.imageName}:{appRunCurrentImage.imageTag}
                    </p>
                  }
                  {
                    appRunCurrentImage.registry &&
                    <p>
                      <Icon type="cloud-download" style={{ color: '#1890ff' }} onClick={this.onShowPushModal} />
                    </p>
                  }
                </div>
              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>构建时间：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`}>
                  {appRunCurrentImage.imageCreateTime ? moment(appRunCurrentImage.imageCreateTime).format('YYYY-MM-DD HH:mm:ss') : ''}
                </p>

              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>代码分支：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`}>
                  {appRunCurrentImage.gitBranch}
                </p>
              </div>
              <div className={styles.flexCenter}>
                <p className={styles.noWarp}>关联git提交记录：</p>
                <p className={`${styles.textOverflow} ${styles.flex1}`}>
                  {
                    appRunCurrentImage.gitCommitId &&
                    <a onClick={() => this.onToGit(appRunCurrentImage.gitCommitId)}>
                      <span>{appRunCurrentImage.gitCommitId.slice(0, 8)}</span>
                      <span>/大约{getCurrentTime(appRunCurrentImage.gitCommitTime)}</span>
                    </a>
                  }
                </p>
              </div>
            </div>
          </Card>
        </Col>
        <Col md={12} sm={24}>
          <Card title="服务访问地址">
            <div style={{ overflowX: 'auto' }}>
              <table className={styles.tableTag}>
                <tbody className={`${styles.flexCenter} ${styles.tableBox}`}>
                  <tr>
                    <th className={`${styles.gridtitleBg} ${styles.gridItem}`}>端口</th>
                    <th className={`${styles.gridtitleBg} ${styles.gridItem}`}>LB访问</th>
                  </tr>
                  {
                    appRunEndpoint && appRunEndpoint.map((item) => (
                      <tr key={item.containerPort}>
                        <td className={styles.gridItem}>{item.containerPort}</td>
                        <td className={styles.gridItem}>
                          {
                            item && item.mlbHosts && currentEnvO.clusterType !== '2' ? item.mlbHosts.map((vo) => (
                              <div key={vo} onClick={() => { this.onHref(vo) }}><a>{vo};</a></div>
                            )) : '—'
                          }
                        </td>
                      </tr>
                    ))
                  }
                </tbody>
              </table>
            </div>
          </Card>
        </Col>
      </Row>
    )
  }
  renderList() {
    const { appRunContainers, currentEnvO } = this.props;
    const { currentContainers } = this.state;
    const columns = [
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        render: (text, record) => (
          <span>{getCurrentTime(text)}</span>
        )
      },
      {
        title: '状态',
        dataIndex: 'status',
        render: (text, record) => (
          <Fragment>
            {
              text && CONTAINTER_STATUS_O[text] &&
              <span className={CONTAINTER_STATUS_O[text].color ? styles[CONTAINTER_STATUS_O[text].color] : ''}>{CONTAINTER_STATUS_O[text].text}</span>
            }
          </Fragment>
        )
      },
      {
        title: '健康检查',
        dataIndex: 'healthStatus',
        className: currentEnvO.clusterType === '2' ? styles.hidden : '',
        render: (text, record) => (
          <Fragment>
            {
              text &&
              <div className={styles[HEALTH_STATUS_O[text].bgColor]} title={HEALTH_STATUS_O[text].text}></div>
            }
          </Fragment>
        )
      },
      {
        title: '宿主机',
        dataIndex: 'host',
      },
      {
        title: '映射端口',
        dataIndex: 'ports',
        render: (text, record) => (
          <Fragment>
            {
              text && text.map((item) => (
                <div key={item}>
                  <a onClick={() => this.onHref(`${record.host}:${item}`)}>{`${record.host}:${item}`}</a>
                </div>
              ))
            }
          </Fragment>
        )
      },
      {
        title: 'CPU',
        dataIndex: 'cpu',
        key: 'cpu',
      },
      {
        title: 'MEM',
        dataIndex: 'memory',
        key: 'memory',
        render: (text, record) => (
          <span>{MformG(text) >= 1 ? MformG(text) + 'GiB' : text + 'MiB'}</span>
        )
      },
      // {
      //   title: '登录',
      //   dataIndex: 'login',
      // },
      {
        title: '日志',
        dataIndex: 'log',
        key: 'log',
        className: currentEnvO.clusterType === '2' ? styles.hidden : '',
        render: (text, record) => (
          <a onClick={() => { this.onShowLogDetail(record) }}>查看日志</a>
        )
      },
    ];
    return (
      <div className={styles.marginT}>
        <Card title="容器列表" extra={
          <Radio.Group buttonStyle="solid" defaultValue="1" onChange={(e) => this.onContainersChange(e.target.value)}>
            <Radio.Button value="0">全部</Radio.Button>
            <Radio.Button value="1">运行中</Radio.Button>
            <Radio.Button value="2">已关闭</Radio.Button>
          </Radio.Group>
        }>
          <Table
            columns={columns}
            dataSource={currentContainers || []}
            onChange={this.onTableChange}
            // loading={containetListLoading}
            pagination={false}
            rowKey="taskId"
          />
        </Card>
      </div>
    )
  }
  renderScale() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { deployTypes, appRunStatus } = this.props;
    return (
      <Card title="扩缩容" extra={
        <div>
          <Icon type="question-circle" />
          <span> 正在运行的容器会被重启；扩缩容修改的资源配置，将会在下一次正式发布时被覆盖</span>
        </div>
      } className={styles.marginT}>
        <Row>
          <Form className={styles.antAdvancedSearchForm} onSubmit={this.onScaleSubmit}>
            <Col md={5} sm={12}>
              <FormItem label="CPU">
                {getFieldDecorator('cpu', {
                  initialValue: appRunStatus.cpu ? appRunStatus.cpu : 0,
                })(
                  <Select showSearch style={{ width: 100 }}>
                    {
                      SCALE_CPU.map((item) => (
                        <Option value={item} key={item}>{item + ' Core'}</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col md={5} sm={12}>
              <FormItem label="MEM">
                {getFieldDecorator('memory', {
                  initialValue: appRunStatus.memory ? appRunStatus.memory : 0,
                })(
                  <Select showSearch style={{ width: 100 }}>
                    {
                      SCALE_MEN.map((item) => (
                        <Option value={item} key={item}>{
                          item % 1024 == 0 ? item / 1024 + ' GiB' : item + ' MiB'
                        }</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col md={5} sm={12}>
              <FormItem label="实例个数">
                {getFieldDecorator('instance', {
                  initialValue: appRunStatus.instances ? appRunStatus.instances : 0,
                })(
                  <Select showSearch style={{ width: 100 }}>
                    {
                      SCALE_CASE.map((item) => (
                        <Option key={item} value={item}>{item}</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col md={6} sm={12}>
              <Button type="primary" htmlType="submit" disabled={deployTypes.SCALE === 0 ? true : false} loading={deployTypes.SCALE === 2 ? true : false}>提交修改</Button>
            </Col>
          </Form>
        </Row>
      </Card>
    )
  }
  renderLogModal() {
    const { showLogModal, currentLogType, logTopLoading, showLogAllScreen } = this.state;
    return (
      <Modal
        visible={showLogModal}
        footer={null}
        title={
          <Row type="flex" align="middle">
            <Col className={styles.marginR}>容器日志</Col>
            <Col>
              <Radio.Group buttonStyle="solid" onChange={this.onLogTypeChange} value={currentLogType} size="small">
                <Radio.Button value={'stdout'}>stdout</Radio.Button>
                <Radio.Button value={'strerr'}>stderr</Radio.Button>
              </Radio.Group>
            </Col>
            <Col className={styles.iconAllScreen}>
              {
                showLogAllScreen ?
                  <Icon type="shrink" className={styles.iconAll} onClick={() => this.onSetScreen('showLogAllScreen', false)} /> :
                  <Icon type="arrows-alt" className={styles.iconAll} onClick={() => this.onSetScreen('showLogAllScreen', true)} />
              }
            </Col>
          </Row>
        }
        width={showLogAllScreen ? '100vw' : 1000}
        onCancel={this.onLogModalCancel}
        className={showLogAllScreen ? styles.modalAll : null}
        style={showLogAllScreen ? { top: 0, paddingBottom: 0 } : null}
      >

        <div id="log" className={`${styles.modalScrollHas} ${showLogAllScreen ? styles.height100 : null}`} ref={c => this._container = c} onScrollCapture={this.onScroll}>
          {
            logTopLoading && <Spin className={styles.spin} />
          }
          {
            this.state[currentLogType] ?
              <Fragment>
                {
                  this.state[currentLogType].log && !this.state[currentLogType].log.length &&
                  <div className={`${styles.textC} ${styles.marginT}`}>
                    <Spin />
                  </div>

                }
                <div style={{ whiteSpace: 'pre' }}>
                  {
                    this.state[currentLogType].log && this.state[currentLogType].log.map((item, i) => (
                      <span key={i} id={i}>{item}</span>
                    ))
                  }
                </div>

              </Fragment>
              : ''
          }
        </div>
      </Modal>
    )
  }
  render() {
    const { showPushModal, showLogModal } = this.state;
    const { currentEnvO } = this.props;
    return (
      <div>
        {this.renderTop()}
        {this.renderList()}
        {currentEnvO.clusterType !== '2' && this.renderScale()}
        {showPushModal && this.renderPushModal()}
        {showLogModal && this.renderLogModal()}
      </div>
    );
  }
}
export default Form.create()(connect(({ service, loading }) => ({
  appRunStatus: service.appRunStatus,
  appRunCurrentImage: service.appRunCurrentImage,
  appRunEndpoint: service.appRunEndpoint,
  appRunContainers: service.appRunContainers,
  deployTypes: service.deployTypes,
  appOperate: service.appOperate,
  appDeployStatus: service.appDeployStatus,
  currentEnvO: service.currentEnvO
  // containetListLoading: loading.effects["service/appRunContainers"]
}))(ServiceWorkInfo));
