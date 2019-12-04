import React, { Fragment } from 'react';
import { Card, Form, Row, Col, Select, Input, message } from 'antd';
import { connect } from 'dva';
import Commom from './commomItem';
import { IMAGE } from '@/assets/index';
import GITMODAL from './gitCredentialModal';
import { system } from '@/services/system';
const FormItem = Form.Item;
const { Option } = Select;
class GitItem extends React.Component {
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
        domain: 'GIT_SERVER'
      }
    })
  }
  //新建服务
  onSubmit = (e) => {
    const { onSave } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = { ...values };
        if (values.gitCredentialId == '-1') {
          params = { ...values, gitCredentialId: undefined }
        }
        onSave({ ...params, appKind: 'GIT_REPOSITORY' })
      }
    });
  };
  showGitModal = () => {
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
    // let res = await system.credentialsAdd({ ...params, credentialType: 'USERNAME_WITH_PASSWORD' });
    // if (res && res.code == '1') {
    //   message.success('添加成功');
      this.setState({
        showGitModal: false
      })
      this.onFetchCredentialsList();
      setFieldsValue({
        gitCredentialId: params.id
      })
    // }
  }
  renderTitle() {
    return (
      <Fragment>
        <div>
          <img src={IMAGE.GIT} />
          <h3>Git代码仓库</h3>
          <p>使用外部的代码仓库（非OPS托管Gitlab），请提供此服务对应的完整代码仓库地址和相应的凭据</p>
        </div>
      </Fragment>
    )
  }
  renderForm() {
    const { onCancel, form, credentialsList, projectId, loading } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form onSubmit={this.onSubmit}>
        <Row type="flex" align="top" gutter={120}>
          <Commom onCancel={onCancel} onSave={this.onSubmit} formItem={form} projectId={projectId} loading={loading}>
            <Col md={12} sm={24}>
              <FormItem label="代码仓库地址" extra={
                <Fragment>
                  <div>服务对应的完整代码仓库地址，例如</div>
                  <span>http://registry.ata.cloud.com/ata/ata-ops.git</span>
                </Fragment>
              }>
                {getFieldDecorator('gitUrl', {
                  rules: [
                    { required: true, message: "请输入" }
                  ]
                })(<Input placeholder="请输入代码仓库地址" />)}
              </FormItem>
            </Col>
            <Col md={12} sm={24}>
              <FormItem label="代码仓库凭据" extra={
                <Fragment>
                  <div>用于获取代码仓库的branch、tag列表，以及拉取代码</div>
                  <span>没有我需要的凭据？我可以<a onClick={this.showGitModal}>新建代码仓库凭据</a></span>
                </Fragment>
              }>
                {getFieldDecorator('gitCredentialId', {
                  initialValue: '-1',
                })(
                  <Select placeholder="请选择代码仓库凭据" showSearch optionFilterProp="children">
                    <Option value="-1">不需要凭据</Option>
                    {
                      credentialsList && credentialsList.map((item) => (
                        <Option value={item.id} key={item.id}>{item.credentialName}</Option>
                      ))
                    }
                  </Select>,
                )}
              </FormItem>
            </Col>
          </Commom>
        </Row>
      </Form>
    )
  }
  render() {
    const { onCancel, projectId } = this.props;
    const { showGitModal } = this.state;
    return (
      <Card>
        {this.renderTitle()}
        {this.renderForm()}
        {showGitModal && <GITMODAL visible={showGitModal} onCancel={this.onGitCancel} credentialType="GIT_SERVER" onOk={this.onAddGitCredentials} projectId={projectId}/>}
      </Card>
    );
  }
}
export default Form.create()(connect(({ system }) => ({
  credentialsList: system.credentialsList
}))(GitItem));
