import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { CARD_TITLE_BG } from "@/common/constant";
import { Card, Row, Col, Descriptions, Icon, Modal, Form, Input, Select, Checkbox, InputNumber, Radio, message, Empty, Spin } from 'antd';
import {
  G2,
  Chart,
  Geom,
  Axis,
  Tooltip,
  Coord,
  Label,
  Legend,
  View,
  Guide,
  Shape,
  Facet,
  Util
} from "bizcharts";
import DataSet from "@antv/data-set";
import styles from '../../index.less';
import { connect } from 'dva';
import { project } from '@/services/project';
import { getUserName, isAdmin } from '@/utils/utils';

const { Search } = Input;
const FormItem = Form.Item;
const { Option } = Select;
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
const formItemLayout1 = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 11 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 13 },
  },
};
const ds = new DataSet();

class ProjectInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      type: "",
      showModal: false,
      submitLoading: false,
      selectedEnvIds: [],
      granularity: 'WEEK'
    };
  }
  componentDidMount() {
    const { keys, proInfo, proEnvSoruce, envList, deployCounts } = this.props;
    if (proEnvSoruce.length === 0) {
      this.onFetchSouce();
    } else {
      this.onGetEnvIds(proEnvSoruce);
    }
    if (!envList.length) {
      this.onFetchItem();
    }
    if (!deployCounts.length) {
      this.onFetchDeloyCounts();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { granularity } = this.state;
    if (prevState.granularity !== granularity) {
      this.onFetchDeloyCounts();
    }
  }
  componentWillReceiveProps(nextProps) {
    const { proEnvSoruce } = nextProps;
    if (this.props.proEnvSoruce !== proEnvSoruce) {
      this.onGetEnvIds(proEnvSoruce);
    }
  }
 
  //项目资源
  onFetchSouce = async () => {
    const { projectId, proInfo, dispatch } = this.props;
    dispatch({
      type: 'project/proEnvSoruce',
      payload: { projectId }
    })
  }
  //环境
  onFetchItem = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'env/envList',
      payload: {}
    })
  }
  onFetchDeloyCounts = () => {
    const { projectId, dispatch } = this.props;
    const { granularity } = this.state;
    dispatch({
      type: 'project/deployCounts',
      payload: {
        projectId,
        granularity
      }
    })
  }
  onGetEnvIds = (proEnvSoruce) => {
    let selectedEnvIds = [];
    proEnvSoruce && proEnvSoruce.length && proEnvSoruce.map((item) => {
      selectedEnvIds.push(item.envId)
    })
    this.setState({
      selectedEnvIds
    })
  }
  onShowModal = (type) => {
    this.setState({
      type,
      showModal: true
    })
  }
  onCancel = () => {
    this.setState({
      showModal: false,
      type: ""
    })
  }
  onSubmit = (e) => {
    const { searchObj, selectedEnvIds } = this.state;
    const { projectId } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          submitLoading: true
        })
      }
      let params = values;
      if (values.envList) {
        let env = [];
        values.envList.map((item) => {
          if (selectedEnvIds.indexOf(item) < 0) {
            env.push({
              envId: item
            })
          }
        })
        params = {
          ...values,
          envList: env
        }
      }
      this.onFetchUpdatePro({ ...params, projectId });
    });
  };
  onFetchUpdatePro = async (params) => {
    let res = await project.updateProject(params);
    this.setState({
      submitLoading: false
    })
    if (res && res.code == '1') {
      this.setState({
        showModal: false
      })
      message.success('修改成功');
      this.onFetchInfo();
      this.onFetchSouce();
    }
  }
    //项目信息
    onFetchInfo = () => {
      const { dispatch, projectId } = this.props;
      dispatch({
        type: 'project/proInfo',
        payload: { projectId }
      })
    }
  onGranularityChange = (e) => {
    this.setState({
      granularity: e.target.value
    })
  }
  //------------------------页面渲染-------------------------------------
  renderBasicInfo() {
    // const { info } = this.state;
    const { proInfo, userAll } = this.props;
    let userInfo = getUserName((userAll || {}).rows, proInfo.masterUserId);
    return (
      <Card title="项目信息" extra={
        <Fragment>
          {
            (isAdmin() || proInfo.adminUserRole == 'USER_PROJECT_MASTER') ?
              <Icon type="edit" style={{ fontSize: 20, color: '#1890ff' }} onClick={() => { this.onShowModal('basic') }} /> :
              null
          }

        </Fragment>

      }>
        <div style={{ height: 158 }}>
          <div className={styles.flexCenter}>
            <p className={styles.basicInfoItem}>项目名：</p>
            <p className={styles.flex1}>{proInfo.projectName}</p>
          </div>
          <div className={styles.flexCenter}>
            <p className={styles.basicInfoItem}>项目负责人：</p>
            <p className={styles.flex1}>{userInfo.realName}</p>
          </div>
          <div className={styles.flexCenter}>
            <p className={styles.basicInfoItem}>项目描述：</p>
            <p className={`${styles.flex1} ${styles.proDesc}`} title={proInfo.description}>{proInfo.description}</p>
          </div>
        </div>
      </Card>
    )
  }
  renderEnvSource() {
    const { proEnvSoruce, proInfo } = this.props;
    return (
      <Card title="项目资源" extra={
        <Fragment>
          {
            (isAdmin() || proInfo.adminUserRole == 'USER_PROJECT_MASTER') ?
              <Icon type="edit" style={{ fontSize: 20, color: '#1890ff' }} onClick={() => { this.onShowModal('envSource') }} />
              : null
          }
        </Fragment>

      }>
        <div style={{ overflowX: 'auto' }}>
          <table className={styles.tableTag}>
            <tbody className={`${styles.flexCenter} ${styles.tableBox}`}>
              <tr>
                <th className={`${styles.gridtitleBg} ${styles.gridItem}`}>可用环境</th>
                <th className={`${styles.gridtitleBg} ${styles.gridItem}`}>已分配端口范围</th>
              </tr>
              {
                proEnvSoruce && proEnvSoruce.map((item) => (
                  <tr key={item.envId}>
                    <td className={styles.gridItem}>{item.envName}</td>
                    <td className={styles.gridItem}>{`${item.portStart} ~ ${item.portEnd}`}</td>
                  </tr>
                ))
              }
            </tbody>
          </table>
        </div>
      </Card>
    )
  }
  renderPubNum() {
    const { deployCounts, deployCountsLoading } = this.props;
    const dv = ds.createView().source([...deployCounts]);
    const { granularity } = this.state;
    return (
      <Card title="各服务发布次数" extra={
        <Radio.Group buttonStyle="solid" value={granularity} onChange={this.onGranularityChange}>
          <Radio.Button value="WEEK">本周</Radio.Button>
          <Radio.Button value="MONTH">本月</Radio.Button>
          <Radio.Button value="YEAR">本年</Radio.Button>
        </Radio.Group>
      } className={styles.marginT}>
        <Spin spinning={deployCountsLoading}>
          {
            deployCounts.length ?
              <Chart data={dv} forceFit height={deployCounts.length * 50} padding={[0, 0, 0, '15%']}>
                <Coord transpose />
                <Axis
                  name="appName"
                  label={{
                    offset: 12,
                  }}
                />
                <Axis name="counts" visible={false} />
                <Tooltip />
                <Geom type="interval" position="appName*counts" />
              </Chart> :
              <Empty />
          }
        </Spin>
      </Card>
    )
  }
  renderModal() {
    const { showModal, type, submitLoading, selectedEnvIds } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { userAll, proInfo, envList } = this.props;
    return (
      <Fragment>
        <Modal
          title={type === "basic" ? "修改项目信息" : "修改项目资源"}
          onCancel={this.onCancel}
          width={700}
          confirmLoading={submitLoading}
          onOk={this.onSubmit}
          visible={showModal}
        >
          <Form {...formItemLayout} onSubmit={this.onSubmit}>
            {
              type === "basic" &&
              <Fragment>
                <FormItem label="项目名">
                  {getFieldDecorator('projectName', {
                    initialValue: proInfo.projectName,
                    rules: [
                      { required: true, message: "请输入20位以内中文数字字母中横线的组合", pattern: /^[A-Za-z0-9-\u4E00-\u9FA5]{1,20}$/ }
                    ]
                  })(<Input placeholder="请输入项目名" />)}
                </FormItem>
                <FormItem label="负责人">
                  {getFieldDecorator('masterUserId', {
                    initialValue: proInfo.masterUserId,
                    rules: [
                      { required: true, message: "请选择负责人" }
                    ]
                  })(
                    <Select placeholder="请选择负责人" showSearch optionFilterProp="children">
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
                    initialValue: proInfo.description,
                    rules: [
                      {
                        validator(rule, value, callback){
                          if(value.length > 500) {
                            callback('最多500个字符')
                          }else {
                            callback()
                          }
                        }
                      }
                    ]
                  })(<TextArea placeholder="请输入项目描述" rows={4} />)}
                </FormItem>
              </Fragment>
            }
            {
              type === "envSource" &&
              <Fragment>
                <FormItem label="环境资源">
                  {getFieldDecorator('envList', {
                    initialValue: selectedEnvIds,
                    rules: [
                      { required: true, message: "至少选择一个环境资源" }
                    ]
                  })(
                    <Checkbox.Group style={{ width: '100%', lineHeight: 'unset' }}>
                      <Row type="flex" align="middle">
                        {
                          envList.map((item, i) => (
                            <Col span={11} key={item.id}>
                              <Checkbox value={item.id} disabled={selectedEnvIds.indexOf(item.id) > -1 ? true : false}>{item.name}</Checkbox>
                            </Col>
                          ))
                        }
                      </Row>
                    </Checkbox.Group>
                  )}
                </FormItem>
              </Fragment>
            }

          </Form>
        </Modal >
      </Fragment >
    )
  }
  render() {
    const { showModal } = this.state;
    return (
      <Fragment>
        <Row gutter={48}>
          <Col md={12} sm={24}>
            {this.renderBasicInfo()}
          </Col>
          <Col md={12} sm={24}>
            {this.renderEnvSource()}
          </Col>
        </Row>
        {this.renderPubNum()}
        {showModal && this.renderModal()}
      </Fragment>
    );
  }
}
export default Form.create()(connect(({ auth, project, env, loading }) => ({
  userAll: auth.userAll,
  proInfo: project.proInfo,
  proEnvSoruce: project.proEnvSoruce,
  envList: env.envList,
  deployCounts: project.deployCounts,
  deployCountsLoading: loading.effects['project/deployCounts']
}))(ProjectInfo));
