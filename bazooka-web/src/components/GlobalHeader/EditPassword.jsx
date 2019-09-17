import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Modal, Form, Input, message } from 'antd';
import { connect } from 'dva';
import styles from '@/pages/index.less';

const FormItem = Form.Item;
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
class EditPassword extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { handleOk } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if(values.password !== values.confirmPassword) {
          message.error('两次新密码不一致');
          return;
        }
        handleOk(values)
      }
    });
  };
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit}>
        <FormItem label="旧密码">
          {getFieldDecorator('originPassword', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请输入旧密码", pattern: /^\S*$/ }
            ]
          })(<Input.Password placeholder="请输入旧密码" />)}
        </FormItem>
        <FormItem label="新密码">
          {getFieldDecorator('password', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请输入新密码" }
            ]
          })(<Input.Password placeholder="请输入新密码" />)}
        </FormItem>
        <FormItem label="确认密码">
          {getFieldDecorator('confirmPassword', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请确认新密码(不含空格)", pattern: /^\S*$/ }
            ]
          })(<Input.Password placeholder="请确认新密码"/>)}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { visible, handleOk, handleCancel } = this.props;
    return (
      <Modal
        title="修改密码"
        visible={visible}
        onOk={this.onSubmit}
        onCancel={handleCancel}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}
export default Form.create()(connect(({ }) => ({}))(EditPassword));
