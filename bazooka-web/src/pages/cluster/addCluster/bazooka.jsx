import React, { Fragment } from 'react';
import { Card, Form, Row, Col, Input, Icon, Select, InputNumber, message } from 'antd';
import { connect } from 'dva';
import CommomItem from './commomItem';
import styles from '@/pages/index.less';
import { IMAGE } from '@/assets/';
import stylesAddCluster from './index.less';
import GITMODAL from '@/pages/service/addService/gitCredentialModal';
import { cluster } from '@/services/cluster'

const FormItem = Form.Item;
const { Option } = Select;

class Bazooka extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showGitModal: false,
      saveLoading: false
    };
  }
  componentDidMount() {
    this.onFetchCredentialsList()
  }
  onFetchCredentialsList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/credentialsListTypes',
      payload: {
        domain: 'NODE_LOGIN'
      }
    })
  }
  onSubmit = (e) => {
    const { onCancel } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll(async (err, values) => {
      if (!err) {
        this.setState({
          saveLoading: true
        })
        let params = {
          ...values, type: '2',
          nodeList: values.nodeList.filter(item => item.nodeIp),
          keys: undefined, credentialId: values.credentialId == '-1' ? undefined : values.credentialId,
        };
        let res = await cluster.createSingleNodeCluster(params)
        if (res && res.code == '1') {
          message.success('添加成功')
          onCancel()
        }
        this.setState({
          saveLoading: false
        })
      }
    });
  }
  addKey = (type) => {
    const { getFieldValue, setFieldsValue } = this.props.form;
    const keys = getFieldValue(type);
    const nextKeys = keys.concat(keys[keys.length - 1] + 1);
    setFieldsValue({
      [type]: nextKeys,
    });
  }
  removeKey = (k, type) => {
    const { getFieldValue, setFieldsValue } = this.props.form;
    const keys = getFieldValue(type);
    if (keys.length === 1) {
      return;
    }
    setFieldsValue({
      [type]: keys.filter(key => key !== k),
    });
  }
  onAddNodeLogin = () => {
    this.setState({
      showGitModal: true
    })
  }
  onGitCancel = () => {
    this.setState({
      showGitModal: false
    })
  }
  onAddGitCredentials = (params) => {
    // const { setFieldsValue } = this.props.form;
    this.onGitCancel();
    this.onFetchCredentialsList();
    // setFieldsValue({
    //   credentialId: params.id
    // })
  }

  renderTitle() {
    return (
      <div className={styles.flexCenter}>
        <img src={IMAGE.BAZOOKA} />
        <div className={styles.marginL10}>
          <div className={stylesAddCluster.clusterTitle}>Bazooka单节点集群</div>
          <span>单节点集群只是将一系列分配给此业务的节点记录为一个集群，实际上节点间互不访问</span>
        </div>
      </div>
    )
  }
  renderForm() {
    const { onCancel, form } = this.props;
    const { getFieldDecorator } = form;
    const { saveLoading } = this.state;
    return (
      <Form onSubmit={this.onSubmit} colon={false} className={styles.marginT}>
        <Row type="flex" align="top" gutter={48}>
          <CommomItem onCancel={onCancel} onSave={this.onSubmit} formItem={form} saveLoading={saveLoading}>
            <Col span={24}>
              <FormItem required={true} label={
                <Fragment>
                  <span>节点列表</span>
                  <span className={styles.marginL10}><Icon type="plus" style={{ color: '#1890ff' }} onClick={() => this.addKey('keys')} /></span>
                </Fragment>
              } help={
                <Fragment>
                  <div>请填写集群内所有节点ip，并保证此节点能被Bazooka服务访问</div>
                  <div>每个节点请选择登录凭据，如果没有可用的凭据，您可以<a onClick={this.onAddNodeLogin}>新建节点登录凭据</a></div>
                </Fragment>
              }>
                {this.renderNodeList()}
              </FormItem>
            </Col>
          </CommomItem>
        </Row>
      </Form>
    )
  }
  renderNodeList() {
    const { credentialsListNODE_LOGIN } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    getFieldDecorator('keys', { initialValue: [0] });
    let keys = getFieldValue('keys');
    return (
      <Fragment>
        {
          keys.map((k, index) => (
            <Row type="flex" gutter={24} key={k}>
              <Col span={2}>
                <FormItem label="序号" colon={false}>
                  <span>{index + 1}</span>
                </FormItem>
              </Col>
              <Col span={4}>
                <FormItem label="节点ip" colon={false}>
                  {getFieldDecorator(`nodeList.${k}.nodeIp`, {
                    rules: [
                      { required: true, message: '请输入节点ip' }
                    ]
                  })(<Input placeholder="请输入节点ip" />)}
                </FormItem>
              </Col>
              <Col span={4}>
                <FormItem label="SSH端口" colon={false}>
                  {getFieldDecorator(`nodeList.${k}.sshPort`, {
                    initialValue: 22,
                    rules: [
                      { required: true, message: '请输入ssh端口' }
                    ]
                  })(<Input placeholder="请输入ssh端口" />)}
                </FormItem>
              </Col>
              <Col span={4}>
                <FormItem label="CPU" colon={false}>
                  {getFieldDecorator(`nodeList.${k}.cpu`, {
                    rules: [
                      { required: true, message: '请输入CPU核数' }
                    ]
                  })(<InputNumber placeholder="请输入CPU核数" style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col span={4}>
                <FormItem label="内存" colon={false}>
                  {getFieldDecorator(`nodeList.${k}.memory`, {
                    rules: [
                      { required: true, message: '请输入内存大小（GiB）' }
                    ]

                  })(<InputNumber placeholder="请输入内存大小（GiB）" style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col span={5}>
                <FormItem label="节点登录凭据" colon={false}>
                  {getFieldDecorator(`nodeList.${k}.credentialId`, {
                    rules: [
                      { required: true, message: '请选择节点登录凭据' }
                    ]
                  })(
                    <Select placeholder="请选择节点登录凭据">
                      {
                        credentialsListNODE_LOGIN && credentialsListNODE_LOGIN.map((item) => (
                          <Option value={item.id} key={item.id}>{item.credentialName}</Option>
                        ))
                      }
                    </Select>
                  )}
                </FormItem>
              </Col>
              {
                keys.length > 1 &&
                <FormItem className={styles.marginL10} label=" " colon={false}>
                  <Icon type="minus" style={{ color: '#1890ff' }} onClick={() => { this.removeKey(k, 'keys') }} />
                </FormItem>
              }
            </Row>
          ))
        }
      </Fragment>
    )
  }
  render() {
    const { showGitModal } = this.state;
    return (
      <Card>
        {this.renderTitle()}
        {this.renderForm()}
        {showGitModal && <GITMODAL visible={showGitModal} onCancel={this.onGitCancel} credentialType="NODE_LOGIN" onOk={this.onAddGitCredentials} />}
      </Card>
    );
  }
}
export default Form.create()(connect(({ system }) => ({
  credentialsListNODE_LOGIN: system.credentialsListNODE_LOGIN
}))(Bazooka));
