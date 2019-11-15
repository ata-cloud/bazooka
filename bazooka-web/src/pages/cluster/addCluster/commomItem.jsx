import React, { Fragment, Children } from 'react';
import { Card, Button, Form, Select, Input, Row, Col } from 'antd';
import { connect } from 'dva';
import styles from '@/pages/index.less';
import GITMODAL from '@/pages/service/addService/gitCredentialModal';
const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
class CommomItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      projectId: undefined,
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
  onShowGitModal=()=>{
    this.setState({
      showGitModal: true
    })
  }
  onGitCancel = () => {
    this.setState({
      showGitModal: false
    })
  }
  onAddGitCredentials=(params)=>{
    const { setFieldsValue } = this.props.formItem;
    this.onGitCancel();
    this.onFetchCredentialsList();
    setFieldsValue({
      credentialId: params.id
    })
  }
  renderForm() {
    const { credentialsList, formItem, children } = this.props;
    const { getFieldDecorator } = formItem;
    return (
      <Fragment>
        <Col md={8} sm={24}>
          <FormItem label="集群名称" extra="集群名称不可重复">
            {getFieldDecorator('name', {
              rules: [
                { required: true, message: "服务名为20位以内中文数字字母中横线的组合", pattern: /^[a-zA-z0-9-\u4E00-\u9FA5]{1,20}$/ }
              ]
            })(<Input placeholder="请输入集群名称" />)}
          </FormItem>
        </Col>
        <Col md={8} sm={24}>
          <FormItem label="镜像库" extra={
            <div>
              <p>此集群内容器部署时将使用此镜像库，请保证此镜像库能够被Bazooka服务和集群内slave节点访问</p>
              <p>推荐在<a>Mesos集群内部自建镜像库</a></p>
            </div>
          }>
            {getFieldDecorator('imageUrl', {
              rules: [
                { required: true, message: "请输入镜像库地址" }
              ]
            })(<Input placeholder="请输入镜像库地址" />)}
          </FormItem>
        </Col>
        <Col md={8} sm={24}>
          <FormItem label="镜像仓库凭据" extra={
            <Fragment>
              <div>用于获取代码仓库的branch、tag列表，以及拉取代码</div>
              <span>没有我需要的凭据？我可以<a onClick={this.onShowGitModal}>新建镜像仓库凭据</a></span>
            </Fragment>
          }>
            {getFieldDecorator('credentialId', {
              initialValue: '-1',
            })(
              <Select placeholder="请选择镜像仓库凭据" showSearch optionFilterProp="children">
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
        {children}
      </Fragment>
    )
  }
  render() {
    const { onCancel, onSave } = this.props;
    const { showGitModal } = this.state;
    return (
      <Fragment>
        {this.renderForm()}
        <Col span={24} className={styles.marginT}>
          <Button onClick={onCancel} className={styles.marginR30}>上一步</Button>
          <Button type="primary" onClick={onSave}>保存</Button>
        </Col>
        {showGitModal && <GITMODAL visible={showGitModal} onCancel={this.onGitCancel} credentialType="DOCKER_REGISTRY" onOk={this.onAddGitCredentials}/>}
      </Fragment>
    );
  }
}
export default Form.create()(connect(({ system }) => ({
  credentialsList: system.credentialsList
}))(CommomItem));
