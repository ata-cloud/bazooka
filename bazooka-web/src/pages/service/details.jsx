import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { connect } from 'dva';
import { Card, Row, Col, Select, Icon, Button, Radio, Menu, Dropdown, Tabs, PageHeader, Spin, Modal, message, Form, InputNumber } from 'antd';
import styles from '@/pages/index.less';
import { ServiceBasic, ServiceBuild, ServiceSetting, ServiceWorkInfo, MirrorM } from './detail/'
import { service } from '@/services/service';
import router from 'umi/router';
const { Option } = Select;
const FormItem = Form.Item;

class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: "1",
      showMirror: false,
      tabList: [
        {
          key: '1',
          tab: '概览'
        },
        {
          key: '2',
          tab: '构建发布'
        },
        {
          key: '3',
          tab: '运行情况'
        },
        {
          key: '4',
          tab: '服务配置'
        }
        // {
        //   key: '4',
        //   tab: <Icon type="tool" theme="filled" style={{ fontSize: 20 }} />
        // }
      ],
      projectId: undefined,
      appId: undefined,
      currentEnv: '',
      loading: false,
      deployType: '',
      showStartModal: false,
      currentEnvO: {}
    };
  }
  // -------------------------------------------------生命周期--------------------------------------------------
  componentDidMount() {
    const { location } = this.props;
    let query = location.query || {};
    this.setState({
      projectId: query.projectId,
      appId: parseInt(query.appId),
      key: query.key || '1'
    }, () => {
      this.onFetchEnvWithPro();
    })
  }
  componentDidUpdate(prevProps, prevState) {
    const { currentEnv } = this.state;
    const { appRunStatus, appDeployStatus } = this.props;
    if (currentEnv !== prevState.currentEnv) {
      clearInterval(this.timmer);
      this.onClearData({
        deployTypes: {// 0:禁用 1：可用  2：loading
          "START": 1,
          "RESTART": 1,
          "SCALE": 1,
          "STOP": 1,
          "DEPLOY": 1,
          "ROLLBACK": 1,
        },
        appOperate: {},
        // appDeployStatus: {},
      })
      this.onFetchAppRunStatus();
      this.onFetchAppDeployStatus();
      this.timmer = setInterval(() => {
        this.onFetchAppDeployStatus();
      }, 4000)
    }
  }
  componentWillReceiveProps(nextProps) {
    const { deployType, key } = this.state;
    const { envWithPro, appDeployStatus, appOperate } = nextProps;
    if (envWithPro && this.props.envWithPro !== envWithPro) {
      this.setState({
        currentEnv: envWithPro && envWithPro[0] ? envWithPro[0].envId : '',
      })
      let currentEnvO = envWithPro && envWithPro[0] ? envWithPro[0] : {}
      this.onSetCurrentEnvO(currentEnvO)
    }
    // if (this.props.appDeployStatus !== appDeployStatus && (appDeployStatus.deployType == "DELETE_IMAGE" || appDeployStatus.deployType == "PUSH_IMAGE")) {
    //   clearInterval(this.timmer);
    // }
    if (this.props.appDeployStatus !== appDeployStatus && appDeployStatus.id && !appDeployStatus.deploying) {
      clearInterval(this.timmer);
      setTimeout(() => {
        this.onFetchAppRunStatus();
      }, 0)
      this.setState({
        deployType: undefined
      })
      this.onClearData({
        appOperate: {}
      })
    }
    if (appOperate.event && this.props.appOperate !== appOperate) {
      if (key !== '1') {
        this.onClearData({
          appHistory: {}
        })
      }
      if (key !== '2') {
        this.onClearData({
          appHistoryMarathon: {}
        })
      }
      if (key !== '3') {
        this.onClearData({
          appRunCurrentImage: {},
          appRunContainers: {}
        })
      }
      if (appOperate.event === "START") {
        this.onStartModalCancel();
      }
      this.onFetchAppDeployStatus();
      this.timmer = setInterval(() => {
        this.onFetchAppDeployStatus();
      }, 4000)
    }
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    clearInterval(this.timmer)
    dispatch({
      type: 'service/clearData',
      payload: {
        appRunStatus: {},
        appHistory: {},
        appDeployConfigListAll: [],
        appDeployConfigListAllNoEnv: [],
        appHistoryMarathon: {},
        appRunEndpoint: [],
        appRunContainers: [],
        appBaseInfo: {},
        appRunCurrentImage: {},
        appDeployServicePort: {},
        appGetBranch: [],
        appDeployStatus: {},
        appDeployFlow: {},
        envWithPro: [],
        dockerListAll: [],
        appDeployFlowInfo: {},
        currentEnvO: {}
      }
    })
  }
  onClearData = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload
    })
  }
  // -------------------------------------------------事件--------------------------------------------------
  //设置当前选中的环境
  onSetCurrentEnvO = (payload) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/setCurrentEnvO',
      payload
    })
  }
  onFetchEnvWithPro = async () => {
    const { projectId } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'service/envWithPro',
      payload: {
        projectId
      }
    })
  }
  onFetchAppDeployStatus = () => {
    const { currentEnv, appId } = this.state;
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appDeployStatus',
      payload: {
        appId: appId,
        envId: currentEnv
      }
    })
  }
  onFetchAppRunStatus = async () => {
    const { dispatch } = this.props;
    const { appId, currentEnv } = this.state;
    dispatch({
      type: 'service/appRunStatus',
      payload: {
        appId: appId,
        envId: currentEnv
      }
    })
  }
  onEnvChange = (key, label) => {
    let currentEnvO = label.props.label || {}
    this.setState({
      currentEnv: key,
    })
    this.onSetCurrentEnvO(currentEnvO)
  }
  onOparaChange = async (e) => {
    let deployType = e.target.value;
   
    if (deployType == "START") {
      this.setState({
        showStartModal: true
      })
      return
    } else {
      this.onSetDeployTypes(deployType);
      this.onFetchAppOperate(deployType);
      this.setState({
        deployType
      })
    }
    

  }
  onFetchAppOperate = async (deployType, detail = {}) => {
    const { appId, currentEnv } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'service/appOperate',
      payload: {
        event: deployType,
        appId: appId,
        envId: currentEnv,
        detail: JSON.stringify({ appId: appId, envId: currentEnv, ...detail })
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
  onTabChange = (key) => {
    const { projectId, appId } = this.state;
    this.setState({
      key
    })
    router.replace({
      pathname: '/service/detail',
      query: {
        projectId,
        appId,
        key
      }
    })
  }
  onShowMirrow = () => {
    this.setState({
      showMirror: true
    })
  }
  onClose = () => {
    this.setState({
      showMirror: false
    })
  }
  onOk = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.onSetDeployTypes('START');
        this.setState({
          deployType: 'START'
        })
        this.onFetchAppOperate('START', values)
      }
    });
  }
  onStartModalCancel = () => {
    this.setState({
      showStartModal: false
    })
  }
  // -------------------------------------------------渲染--------------------------------------------------
  renderTitle() {
    const { key, currentEnv, deployType } = this.state;
    const { deployTypes, envWithPro } = this.props;
    return (
      <Fragment>
        <Row type="flex" justify="end" align="middle">
          <Col className={styles.flexCenter} >
            <Select onChange={this.onEnvChange} value={currentEnv} style={{ minWidth: 150 }}>
              {
                envWithPro && envWithPro.map((item) => (
                  <Option value={item.envId} key={item.envId} label={item}>{item.envName}</Option>
                ))
              }
            </Select>
          </Col>
          <Col className={styles.marginL}>
            <Radio.Group buttonStyle="solid" onChange={this.onOparaChange} value={deployType}>
              <Radio.Button value="START" disabled={deployTypes.START == 0 ? true : false}>
                {
                  deployTypes.START == 2 && <Icon type="loading" className={styles.marginR} />
                }
                <span>启动</span>
              </Radio.Button>
              <Radio.Button value="STOP" disabled={deployTypes.STOP == 0 ? true : false}>
                {
                  deployTypes.STOP == 2 && <Icon type="loading" className={styles.marginR} />
                }
                <span>停止</span>
              </Radio.Button>
              <Radio.Button value="RESTART" disabled={deployTypes.RESTART == 0 ? true : false}>
                {
                  deployTypes.RESTART == 2 && <Icon type="loading" className={styles.marginR} />
                }
                <span>重启</span>
              </Radio.Button>
            </Radio.Group>
          </Col>
          <Col className={styles.marginL}>
            <Dropdown overlay={
              <Menu>
                <Menu.Item onClick={this.onShowMirrow}>
                  <span>镜像管理</span>
                </Menu.Item>
              </Menu>
            } placement="bottomCenter">
              <Button><Icon type="ellipsis" /></Button>
            </Dropdown>
          </Col>
        </Row>
      </Fragment>
    )
  }
  renderStartModal() {
    const { showStartModal } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { currentEnvO, appRunStatus }= this.props;
    return (
      <Modal
        title="启动服务"
        visible={showStartModal}
        onCancel={this.onStartModalCancel}
        onOk={this.onOk}
      >
        <Fragment>
          {
            currentEnvO.clusterType !== '2' ?
              <Fragment>
                <p className={styles.disabledColor}>服务当前为已关闭状态，重启服务时请设置容器实例个数</p>
                <Form onSubmit={this.onOk} autoComplete="off">
                  <FormItem>
                    {getFieldDecorator('instance', {
                      initialValue: 1,
                      rules: [{ required: true, message: '请输入大于0的正整数', pattern: /^[1-9][0-9]*$/ }]
                    })(
                      <InputNumber style={{ width: '90%' }} />
                    )}
                  </FormItem>
                </Form>
              </Fragment> :
              <p>服务当前为已关闭状态，启动服务后容器实例个数将恢复为<span className={styles.textError}> {appRunStatus.instances}</span></p>
          }
        </Fragment>

      </Modal>
    )
  }
  render() {
    const { key, showMirror, tabList, currentEnv, projectId, appId, showStartModal } = this.state;
    const { appRunStatus, envWithPro } = this.props;
    return (
      <div className={`${styles.serviceDetailBox} ${key == "4" ? styles.serviceDetailBox2 : ""}`}>
        {
          appRunStatus.appId &&
          <PageHeaderWrapper title={appRunStatus.appName} content={this.renderTitle()} tabList={tabList} tabActiveKey={key} onTabChange={this.onTabChange}>
            <Spin spinning={currentEnv ? false : true} className={`${styles.spin}`} />
            {
              currentEnv &&
              <Fragment>
                {
                  key === '1' &&
                  <ServiceBasic data={{ envId: currentEnv, projectId, appId }} />
                }
                {
                  key === '2' &&
                  <ServiceBuild data={{ envId: currentEnv, projectId, appId }} />
                }
                {
                  key === '3' &&
                  <ServiceWorkInfo data={{ envId: currentEnv, projectId, appId }} />
                }
                {
                  key === '4' &&
                  <ServiceSetting data={{ envId: currentEnv, projectId, appId }} />
                }
              </Fragment>
            }
            {
              showMirror && <MirrorM visible={showMirror} onClose={this.onClose} data={{ envId: currentEnv, projectId, appId }} />
            }
            {
              showStartModal && this.renderStartModal()
            }
          </PageHeaderWrapper>
        }
      </div>

    );
  }
}

export default Form.create()(connect(({ service }) => ({
  appRunStatus: service.appRunStatus,
  deployTypes: service.deployTypes,
  envWithPro: service.envWithPro,
  appDeployStatus: service.appDeployStatus,
  appOperate: service.appOperate,
  currentEnvO: service.currentEnvO
}))(Detail));