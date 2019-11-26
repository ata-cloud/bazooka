import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Modal, Form, Input, Radio, Button, Select, message } from 'antd';
import { connect } from 'dva';
import styles from '../../index.less';
import { system } from '@/services/system';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
const domains = [
  {
    value: 'GIT_SERVER',
    text: '代码仓库凭据'
  },
  {
    value: 'DOCKER_REGISTRY',
    text: '镜像库凭据'
  },
  {
    value: 'NODE_LOGIN',
    text: '节点登录凭据'
  },
];
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
class GitModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  static defaultProps = {
    currentItem: {},
    credentialType: ""
  };
  componentDidMount() {

  }
  onSubmit = (e) => {
    const { onOk, currentItem } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll(async (err, values) => {
      if (!err) {
        let params = { ...values, scope: 'GLOBAL' }
        if (currentItem.id) {
          //修改
          let currentParams = { credentialKey: params.credentialKey, credentialValue: params.credentialValue };
          if (params.credentialValue == '******') {
            currentParams = { credentialKey: params.credentialKey }
          }
          let res = await system.credentialsUpdate({ id: currentItem.id, ...currentParams });
          if (res && res.code == '1') {
            message.success('修改成功');
            onOk(res.data)
          }
        } else {
          //新增
          let res = await system.credentialsAdd({ ...params, credentialType: 'USERNAME_WITH_PASSWORD' });
          if (res && res.code == '1') {
            message.success('添加成功');
            onOk(res.data)
          }
        }

        // onOk({ ...values, scope: 'GLOBAL' })
      }
    });
  };
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { credentialType, currentItem } = this.props;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout} autoComplete="off">
        <FormItem label="名称">
          {getFieldDecorator('credentialName', {
            initialValue: currentItem.credentialName,
            rules: [
              { required: true, message: "请输入名称" }
            ]
          })(<Input placeholder="请输入名称" disabled={currentItem.id ? true : false} />)}
        </FormItem>
        <FormItem label="类型">
          {getFieldDecorator('domain', {
            initialValue: currentItem.domain || credentialType,
            // rules: [{
            //   required: true,
            //   message: "请选择角色"
            // }]
          })(
            <Select placeholder="请选择类型" disabled>
              {
                domains.map((item) => (
                  <Option value={item.value} key={item.value}>{item.text}</Option>
                ))
              }
            </Select>,
          )}
        </FormItem>
        <FormItem label="用户名">
          {getFieldDecorator('credentialKey', {
            initialValue: currentItem.credentialKey,
            rules: [
              { required: true, message: "请输入用户名" }
            ]
          })(<Input placeholder="请输入用户名" autoComplete="new-password" />)}
        </FormItem>
        <FormItem label="密码">
          {getFieldDecorator('credentialValue', {
            initialValue: currentItem.credentialValue,
            rules: [
              { required: true, message: "请输入密码" }
            ]
          })(<Input.Password placeholder="请输入密码" autoComplete="new-password" visibilityToggle={currentItem.id ? false : true} />)}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { visible, onCancel, currentItem } = this.props;
    return (
      <Modal
        visible={visible}
        title={currentItem.id ? "修改凭据" : "新增凭据"}
        onCancel={onCancel}
        onOk={this.onSubmit}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}
export default Form.create()(connect(({ system }) => ({

}))(GitModal));