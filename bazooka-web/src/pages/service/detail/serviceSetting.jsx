import React, { Fragment } from 'react';
import { Card, Icon, Table, Popconfirm, Divider, Dropdown, Button, Menu, Modal, Form, Input, Spin, message, Select } from 'antd';
import styles from '@/pages/index.less';
import { CARD_TITLE_BG, APP_KINDS_O, CLUSTER_TYPE_O_ALL } from "@/common/constant";
import { connect } from 'dva';
import BuildSet from './buildSet/index';
import { getUserName, isAdmin } from '@/utils/utils';
import { service } from '@/services/service';
import moment from 'moment';
import router from 'umi/router';
const { SubMenu } = Menu;
const { confirm } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
const { TextArea } = Input;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 5 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};
class ServiceSetting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showDeleteModal: false,
      showBuildSet: false,
      deployMode: '',
      currentItem: {},
      deleteLoading: false,
      showServiceEdit: false,
      editLoading: false
    };
  }
  componentDidMount() {
    const { appBaseInfo, appDeployConfigListAll, appDeployConfigListAllNoEnv } = this.props;
    if (Object.getOwnPropertyNames(appBaseInfo).length === 0) {
      this.onFetchAppGet();
    }
    if (appDeployConfigListAll && !appDeployConfigListAll.length) {
      this.onFetchAppDeployConfigListAll();
    }
    if (appDeployConfigListAllNoEnv && !appDeployConfigListAllNoEnv.length) {
      this.onFetchAppDeployConfigListAll('noEnv');
    }

  }
  componentDidUpdate(prevProps, prevState) {
    const { data } = this.props;
    if (data.envId !== prevProps.data.envId) {
      this.onFetchAppDeployConfigListAll();
      this.onClearData({
        appRunEndpoint: [],
        appDeployConfigList: {},
        dockerListAll: [],
        appHistory: {},
        appHistoryMarathon: {},
        appDeployServicePort: {},
        appGetBranch: [],
        // appDeployStatus: {},
        appDeployFlow: {},
        appRunCurrentImage: {}
      })
    }
  }
  onFetchAppGet = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appBaseInfo',
      payload: {
        appId: data.appId
      }
    })
  }
  onFetchAppDeployConfigListAll = (type) => {
    const { dispatch, data } = this.props;
    let envId = type == 'noEnv' ? {} : { envId: data.envId }
    dispatch({
      type: 'service/appDeployConfigListAll',
      payload: {
        appId: data.appId,
        ...envId
      }
    })
  }
  //保存构建配置成功
  onSavaSuccess = () => {
    this.onFetchAppDeployConfigListAll();
    this.onFetchAppDeployConfigListAll('noEnv');
  }
  //删除服务
  onDeleteApp = () => {
    this.setState({
      showDeleteModal: true
    })
  }
  //确认删除服务
  onDeleteAppOk = (e) => {
    const { appRunStatus } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (appRunStatus.appName !== values.name) {
          message.warning('请输入正确的服务名')
          return
        }
        this.onDeleteAppFetch(values)
      }
    });
  }
  //删除服务接口
  onDeleteAppFetch = async (params) => {
    const { data } = this.props;
    this.setState({
      deleteLoading: true
    });
    let res = await service.appDelete({ appId: data.appId });
    this.setState({
      deleteLoading: false
    })
    if (res && res.code == '1') {
      message.success('删除成功');
      router.goBack();
    }
  }
  onCancel = () => {
    this.setState({
      showDeleteModal: false
    })
  }
  onBuldSetCancle = () => {
    this.setState({
      showBuildSet: false,
      deployMode: ''
    })
  }
  onShowBuildSet = (deployMode, currentItem) => {
    console.log('deployMode-->', deployMode)
    this.setState({
      showBuildSet: true,
      deployMode,
      currentItem: currentItem || {}
    })
  }
  onShowEditModal = () => {
    this.setState({
      showServiceEdit: true
    })
  }
  onCancelEdit = () => {
    this.setState({
      showServiceEdit: false
    })
  }
  onServiceEdit = (e) => {
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.onServiceEditFetch(values)
      }
    });
  }
  onServiceEditFetch = async (params) => {
    const { data } = this.props;
    this.setState({
      editLoading: true
    })
    let res = await service.appUpdate({ ...params, appId: data.appId });
    this.setState({
      editLoading: false
    })
    if (res && res.code == '1') {
      message.success('修改成功');
      this.onFetchAppGet();
      this.setState({
        showServiceEdit: false
      })
    }
  }
  // onTableChange = () => {

  // }
  onDelete = async (configId) => {
    const { data } = this.props;
    let res = await service.appDeployConfigDelete({
      appId: data.appId,
      configId
    });
    if (res && res.code == '1') {
      message.success('删除成功');
      this.onFetchAppDeployConfigListAll();
      this.onFetchAppDeployConfigListAll('noEnv');
    }
  }
  onClearData = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload
    })
  }
  // --------------------渲染 --------------------------------------------------
  renderDeletePro() {
    const { showDeleteModal, deleteLoading } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { appRunStatus } = this.props;
    return (
      <Modal
        visible={showDeleteModal}
        title="删除服务"
        okText="确认删除"
        okType="danger"
        onOk={this.onDeleteAppOk}
        onCancel={this.onCancel}
        confirmLoading={deleteLoading}
      >
        <p>请输入服务名“<span className={styles.textError}>{appRunStatus.appName}</span>”确认删除服务</p>
        <p>删除服务前请先关停服务相关的所有容器，并停止发布、重启、扩缩容等操作。服务删除后，代码仓库和镜像会被保留。 删除操作不可逆，请谨慎操作。</p>
        <Form className={styles.antAdvancedSearchForm} onSubmit={this.onDeleteAppOk}>
          <FormItem>
            {getFieldDecorator('name', {
              rules: [
                { required: true, message: "请输入服务名" }
              ]
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    )
  }
  renderServiceInfoEditModal() {
    const { showServiceEdit, editLoading } = this.state;
    const { appBaseInfo, userAll } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        title="修改服务信息"
        visible={showServiceEdit}
        onOk={this.onServiceEdit}
        onCancel={this.onCancelEdit}
        confirmLoading={editLoading}
      >
        <Form {...formItemLayout}>
          <FormItem label="服务类型">
            <Input disabled value={APP_KINDS_O[appBaseInfo.appKind].text} />
          </FormItem>
          <FormItem label="负责人">
            {getFieldDecorator('leaderId', {
              initialValue: appBaseInfo.leaderId,
              rules: [
                { required: true, message: "请选择项目负责人" }
              ]
            })(
              <Select placeholder="请选择项目负责人" showSearch optionFilterProp="children">
                {
                  userAll && userAll.rows && userAll.rows.map((item) => (
                    <Option value={item.userId} key={item.userId}>{item.realName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
          <FormItem label="描述">
            {getFieldDecorator('description', {
              initialValue: appBaseInfo.description,
              rules: [
                {
                  validator: async (rule, value, callback) => {
                    if (value.length > 500) {
                      callback('最多500个字符')
                    } else {
                      callback()
                    }
                  }
                }
              ]
            })(<TextArea placeholder="请输入描述" rows={4} />)}
          </FormItem>
        </Form>
      </Modal >
    )
  }
  renderServiceInfo() {
    const { appBaseInfo, userAll } = this.props;
    let userInfo = getUserName((userAll || {}).rows, appBaseInfo.leaderId);
    return (
      <Card title="服务信息" extra={
        <Fragment>
          {
            (isAdmin() || appBaseInfo.userType == 'USER_APP_MASTER' || appBaseInfo.userType == 'USER_PROJECT_MASTER') &&
            <Icon type="edit" style={{ color: "#1890ff", fontSize: 20 }} onClick={this.onShowEditModal} />
          }
        </Fragment>

      } hoverable>
        <div className={styles.flexCenter}>
          <p className={styles.basicInfoItem}>服务类型：</p>
          {
            appBaseInfo.appKind && APP_KINDS_O[appBaseInfo.appKind] ?
              <p className={styles.flex1}>
                <img src={APP_KINDS_O[appBaseInfo.appKind].icon} className={styles.appKindIcon} />
                <span>{APP_KINDS_O[appBaseInfo.appKind].text}</span>
              </p> :
              <p className={styles.flex1}></p>
          }
        </div>
        <div className={styles.flexCenter}>
          <p className={styles.basicInfoItem}>服务负责人：</p>
          <p className={styles.flex1}>{userInfo.realName}</p>
        </div>
        <div className={styles.flexCenter}>
          <p className={styles.basicInfoItem}>服务描述：</p>
          <p className={styles.flex1} style={{ wordBreak: 'break-all' }}>{appBaseInfo.description}</p>
        </div>
      </Card>
    )
  }
  renderMenu() {
    const { appDeployConfigListAllNoEnv } = this.props;
    return (
      <Menu>
        {
          appDeployConfigListAllNoEnv && appDeployConfigListAllNoEnv.map((item) => (
            <Menu.Item key={item.id} onClick={() => this.onShowBuildSet(item.deployMode, { type: 'copy', id: item.id })}>【{item.envName}】 {item.configName}</Menu.Item>
          ))
        }
      </Menu>
    )
  }
  renderSet() {
    const { deployMode } = this.state;
    const { appDeployConfigListAll, appBaseInfo, currentEnvO } = this.props;
    let isMaster = (isAdmin() || appBaseInfo.userType == 'USER_APP_MASTER' || appBaseInfo.userType == 'USER_PROJECT_MASTER');
    let opera = isMaster ? {
      title: '操作',
      dataIndex: 'opera',
      render: (text, record) => (
        <div>
          <Popconfirm
            title="确定删除吗?"
            onConfirm={() => this.onDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <a>删除</a>
          </Popconfirm>
          <Divider type="vertical" />
          <a
            onClick={() => {
              this.onShowBuildSet(record.deployMode, { type: 'edit', id: record.id });
            }}
          >
            修改
            </a>
        </div>
      ),
    } : {};
    const columns = [
      {
        title: '配置名',
        dataIndex: 'configName',
      },
      {
        title: '环境',
        dataIndex: 'envName',
      },
      {
        title: '方式',
        dataIndex: 'deployMode',
        render: (text, record) => (
          <span>
            {
              text.indexOf('BUILD') > -1 === 'BUILD' && <span>构建发布</span>
            }
            {
              text === 'DOCKER_IMAGE' && <span>镜像发布</span>
            }
          </span>
        )
      },
      {
        title: '修改人',
        dataIndex: 'updateAuthor',
      },
      {
        title: '修改时间',
        dataIndex: 'updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
        )
      },
      { ...opera },
    ];
    return (
      <Card className={styles.marginT} title="构建发布配置" extra={
        <Fragment>
          {
            (isAdmin() || appBaseInfo.userType == 'USER_APP_MASTER' || appBaseInfo.userType == 'USER_PROJECT_MASTER') &&
            // <Dropdown overlay={this.renderMenu()}>
            //   <Button type="primary">
            //     <span>新建构建发布配置</span>
            //     <Icon type="down" />
            //   </Button>
            // </Dropdown>
            <div>
              <Button type="primary" onClick={() => { this.onShowBuildSet(CLUSTER_TYPE_O_ALL[currentEnvO.clusterType].buildType || "BUILD") }} className={styles.marginR}>+ 从代码发布开始</Button>
              <Button type="primary" onClick={() => { this.onShowBuildSet('DOCKER_IMAGE') }} className={styles.marginR}>+ 从镜像发布开始</Button>
              <Dropdown overlay={this.renderMenu()}>
                <Button type="primary">
                  <span>导入已有配置</span>
                  <Icon type="down" />
                </Button>
              </Dropdown>
            </div>
          }
        </Fragment>

      } hoverable>
        <Table
          // rowSelection={rowSelection}
          columns={columns}
          dataSource={appDeployConfigListAll || []}
          // onChange={this.onTableChange}
          pagination={false}
          rowKey={record => {
            return record.id;
          }}
        />
      </Card>
    )
  }
  renderDelete() {
    return (
      <Card title="删除服务" className={styles.marginT} hoverable>
        <p>删除服务前请先关停服务相关的所有容器，并停止发布、重启、扩缩容等操作。服务删除后，代码仓库和镜像会被保留。 删除操作不可逆，请谨慎操作。</p>
        <Button type="danger" onClick={this.onDeleteApp}>删除服务</Button>
      </Card>
    )
  }
  render() {
    const { showDeleteModal, showBuildSet, deployMode, currentItem, showServiceEdit } = this.state;
    const { data, appDeployConfigListAllLoading, appBaseInfo } = this.props;
    return (
      <div>
        {this.renderServiceInfo()}
        <Spin spinning={appDeployConfigListAllLoading}>
          {this.renderSet()}
        </Spin>
        {
          (isAdmin() || appBaseInfo.userType == "USER_PROJECT_MASTER") &&
          this.renderDelete()
        }
        {showDeleteModal && this.renderDeletePro()}
        {showServiceEdit && this.renderServiceInfoEditModal()}
        {showBuildSet && deployMode && <BuildSet visible={showBuildSet} onCancel={this.onBuldSetCancle} deployMode={deployMode} info={data} onSavaSuccess={this.onSavaSuccess} currentItem={currentItem} />}
      </div>
    );
  }
}
export default Form.create()(connect(({ service, auth, loading }) => ({
  currentEnvO: service.currentEnvO,
  appBaseInfo: service.appBaseInfo,
  appRunStatus: service.appRunStatus,
  userAll: auth.userAll,
  appDeployConfigListAll: service.appDeployConfigListAll,
  appDeployConfigListAllNoEnv: service.appDeployConfigListAllNoEnv,
  appDeployConfigListAllLoading: loading.effects['service/appDeployConfigListAll']
}))(ServiceSetting));
