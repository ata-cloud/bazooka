import React, { Fragment } from 'react';
import { Card, Form, Modal, Input, Radio, Select, Checkbox, message } from 'antd';
import { connect } from 'dva';
import GITMODAL from '../addService/gitCredentialModal';
import { system } from '@/services/system';
import styles from '@/pages/index.less';

const FormItem = Form.Item;
const { Option } = Select;
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

class ImagePushModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showGitModal: false
    };
  }
  componentDidMount() {
    this.onFetchCredentialsList()
  }

  onFetchCredentialsList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/credentialsList',
      payload: {
        domain: 'DOCKER_REGISTRY'
      }
    })
  }
  onOk = (e) => {
    const { onSubmit } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = {};
        if (!values.isExternalDockerRegistry) {
          params = { ...values }
        } else {
          params = { ...values, needAuth: values.credentialId == '-1' ? false : true, credentialId: values.credentialId == '-1' ? undefined : values.credentialId }
        }
        onSubmit(params)
      }
    });
  };
  onShowGitModal = () => {
    this.setState({
      showGitModal: true
    })
  }
  onGitCancel = () => {
    this.setState({
      showGitModal: false
    })
  }
  onAddGitCredentials = async (params) => {
    const { setFieldsValue } = this.props.form;
    let res = await system.credentialsAdd({ ...params, credentialType: 'USERNAME_WITH_PASSWORD' });
    if (res && res.code == '1') {
      message.success('添加成功');
      this.setState({
        showGitModal: false
      })
      this.onFetchCredentialsList();
      setFieldsValue({
        credentialId: res.data.id
      })
    }
  }
  renderForm() {
    const { visible, confirmLoading, currentItem, onCancel, envWithPro, appRunStatus, credentialsList } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { showGitModal } = this.state;
    return (
      <Modal
        visible={visible}
        title="镜像推送"
        onCancel={onCancel}
        onOk={this.onOk}
        okText="开始推送"
        confirmLoading={confirmLoading}
        width={700}
      >
        <Form {...formItemLayout} onSubmit={this.onOk} autoComplete="off">
          <FormItem label="镜像名">
            <Input disabled value={`${currentItem.registry}/${currentItem.imageName}`} />
          </FormItem>
          <FormItem label="Tag">
            <Input disabled value={`${currentItem.imageTag}`} />
          </FormItem>
          <FormItem label=" " colon={false}>
            {getFieldDecorator('isExternalDockerRegistry', {
              initialValue: false,
            })(
              <Radio.Group>
                <Radio value={false}>ata ops内部镜像库</Radio>
                <Radio value={true}>外部镜像库</Radio>
              </Radio.Group>
            )}
          </FormItem>
          {
            !getFieldValue('isExternalDockerRegistry') &&
            <FormItem label="目标镜像库">
              {getFieldDecorator('targetEnvId', {
                rules: [
                  { required: true, message: "请选择目标镜像库" }
                ]
              })(
                <Select placeholder="请选择目标镜像库" showSearch allowClear optionFilterProp="children">
                  {
                    envWithPro && envWithPro.map((item) => (
                      <Option value={item.envId} key={item.envId} disabled={item.envId == appRunStatus.envId ? true : false}>{item.envName}</Option>
                    ))
                  }
                </Select>
              )}
            </FormItem>
          }
          {
            getFieldValue('isExternalDockerRegistry') &&
            <Fragment>
              <FormItem label="目标镜像库">
                {getFieldDecorator('targetDockerRegistry', {
                  rules: [
                    { required: true, message: "外部镜像库地址不包括协议,如http://a.com只需输入a.com", pattern: /^(?!http)\S*$/ }
                  ]
                })(<Input placeholder="请输入外部镜像库地址" />)}
              </FormItem>
              <FormItem label="镜像库凭据" help={
                <span>没有我需要的凭据？我可以<a onClick={this.onShowGitModal}>新建镜像库凭据</a></span>
              }>
                {getFieldDecorator('credentialId', {
                  initialValue: '-1',
                  // rules: [
                  //   { required: true, message: "请选择镜像库凭据" }
                  // ]
                })(
                  <Select placeholder="请选择镜像库凭据">
                    <Option value="-1">不需要凭据</Option>
                    {
                      credentialsList && credentialsList.map((item) => (
                        <Option value={item.id} key={item.id}>{item.credentialName}</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
              {/* <FormItem label="需要登录">
                {getFieldDecorator('needAuth', {
                  initialValue: false,
                  // rules: [
                  //   { required: true, message: "请输入外部镜像库地址" }
                  // ]
                })(<Checkbox></Checkbox>)}
              </FormItem>
              {
                getFieldValue('needAuth') &&
                <Card>
                  <FormItem label="用户名">
                    {getFieldDecorator('username', {
                      rules: [
                        { required: true, message: "请输入用户名" }
                      ]
                    })(<Input placeholder="请输入用户名" />)}
                  </FormItem>
                  <FormItem label="密码">
                    {getFieldDecorator('password', {
                      rules: [
                        { required: true, message: "请输入密码" }
                      ]
                    })(<Input.Password placeholder="请输入密码" />)}
                  </FormItem>
                </Card>
              } */}
            </Fragment>
          }
        </Form>
        {this.renderLog()}
        {showGitModal && <GITMODAL visible={showGitModal} onCancel={this.onGitCancel} credentialType="DOCKER_REGISTRY" onOk={this.onAddGitCredentials} />}
      </Modal>
    );
  }
  renderLog() {
    const { logDetail } = this.props;
    return (
      <Fragment>
        {
          logDetail && logDetail.length ?
            <div className={`${styles.boxBlock} ${styles.boxBlockScroll}`}>
              {
                logDetail.map((item) => (
                  <div key={item.type}>
                    <p>>> {item.type}</p>
                    <pre>{item.content}</pre>
                  </div>
                ))
              }
            </div> : null
        }

      </Fragment>
    )
  }
  render() {
    return (
      <div>
        {this.renderForm()}
      </div>
    )
  }
}

export default Form.create()(connect(({ service, system }) => ({
  envWithPro: service.envWithPro,
  appRunStatus: service.appRunStatus,
  credentialsList: system.credentialsList
}))(ImagePushModal));