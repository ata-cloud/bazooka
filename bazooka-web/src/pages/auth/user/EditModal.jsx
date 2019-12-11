import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Modal, Form, Input, Radio, Button, Select, message } from 'antd';
import { connect } from 'dva';
import { auth } from '@/services/auth';
import styles from '../../index.less';
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
class EditModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      detail: {},
      isResetPasword: 0
    };
  }
  componentDidMount() {
    const { userId } = this.props;
    if (userId) {
      this.onFetchDetail();
    }
    this.onFetchItems();
  }

  onFetchDetail = () => {
    const { userId } = this.props;
    auth.usergetPage({ userId })
      .then((res) => {
        if (res && res.code == '1') {
          let data = res.data && res.data.rows ? res.data.rows[0] : {};
          let roleIds = [];
          if (data.userRoles) {
            data.userRoles.map((item) => {
              roleIds.push(item.roleId)
            })
          }
          this.setState({
            detail: { ...data, roleIds }
          })
        }
      })
  }
  onFetchItems = () => {
    const { dispatch, roleList } = this.props;
    if (Object.getOwnPropertyNames(roleList).length === 0 || roleList.pageSize != 100) {
      dispatch({
        type: 'auth/roleList',
        payload: {
          pageSize: 100
        }
      })
    }

  }
  onSubmit = (e) => {
    const { onOk } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // if (values.password !== values.confirmPassword) {
        //   message.error('两次密码不一致');
        //   return
        // }
        onOk(values);
      }
    });
  };
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { roleList, userId } = this.props;
    const { detail, isResetPasword } = this.state;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="用户名">
          {getFieldDecorator('username', {
            initialValue: detail.username,
            rules: [
              { required: true, message: "用户名为14位以内数字字母下划线的组合", pattern: /^\w{1,14}$/ }
            ]
          })(<Input placeholder="请输入用户名" disabled={userId ? true : false} autoComplete="new-password" />)}
        </FormItem>
        <FormItem label="全名">
          {getFieldDecorator('realName', {
            initialValue: detail.realName,
            rules: [
              { required: true, message: "请输入32位以下中文字母标点符号的组合", pattern: /^[a-zA-Z,.?;:，。“”！（）？\u4E00-\u9FA5]{1,32}$/, }
            ]
          })(<Input placeholder="请输入真实姓名" />)}
        </FormItem>
        <FormItem label="邮箱">
          {getFieldDecorator('email', {
            initialValue: detail.email,
            rules: [
              {
                required: true,
                message: '邮箱格式不正确',
                pattern: /^\S+@{1}\S+[.]{1}\S+$/,

              }, {
                validator(rule, value, callback) {
                  try {
                    if (value.length > 100) {
                      callback('最多100个字符')
                    } else {
                      callback()
                    }
                  } catch (err) {
                    callback()
                  }
                }
              }
            ]
          })(<Input placeholder="请输入邮箱" />)}
        </FormItem>
        <FormItem label="选择角色">
          {getFieldDecorator('roleIds', {
            initialValue: detail.roleIds ? detail.roleIds : [],
            rules: [{
              required: true,
              message: "请选择角色"
            }]
          })(
            <Select placeholder="请选择用户角色" showSearch mode="multiple" allowClear>
              {
                roleList && roleList.rows && roleList.rows.map((item) => (
                  <Option value={item.roleId} key={item.roleId}>{item.roleName}</Option>
                ))
              }
            </Select>,
          )}
        </FormItem>
        {
          userId &&
          <FormItem label="是否重置密码">
            {getFieldDecorator('resetPas', {
              initialValue: 0,
              rules: [
                { required: false }
              ]
            })(
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            )}
          </FormItem>
        }
        {
          (!userId || getFieldValue('resetPas') === 1) &&
          <FormItem label="密码">
            {getFieldDecorator('password', {
              initialValue: detail.password,
              rules: [
                { required: true, message: "请输入密码(不含空格)", pattern: /^\S*$/ }
              ]
            })(<Input.Password placeholder="请输入密码" autoComplete="new-password" />)}
          </FormItem>
        }


      </Form>
    )
  }
  render() {
    const { visible, userId, onCancel } = this.props;
    const { submitLoading } = this.props;
    return (
      <Modal
        visible={visible}
        title={userId ? "编辑用户" : "新增用户"}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={submitLoading}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}
export default Form.create()(connect(({ auth }) => ({
  roleList: auth.roleList,
}))(EditModal));

