import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Input, Radio, Button, Select, message  } from 'antd';
import { connect } from 'dva';
import styles from '../../index.less';
import { auth } from '@/services/auth';
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
class RoleEdit extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      query: {},
      detail: {},
      submitLoading: false,
    };
  }
  // --------------------生命周期 --------------------------------------------------
  componentDidMount() {
    const { location } = this.props;
    let query = location.query || {};
    if (query.id) {
      this.setState({
        query
      }, () => {
        this.onFetchDetail();
      })
    }
  }
  // --------------------方法 -----------------------------------------------------
  onFetchDetail = () => {
    const { query } = this.state;
    auth.rolegetPage({ roleId: query.id })
      .then((res) => {
        if (res && res.code == '1') {
          this.setState({
            detail: res.data && res.data.rows ? res.data.rows[0] : {}
          })
        }
      })
  }
  onClose = () => {
    this.props.history.goBack();
  }
  onSubmit = (e) => {
    const { query } = this.state;
    const { dispatch } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          submitLoading: true
        });
        if (query.id) {
          //编辑
          auth.roleEdit({ ...values, roleId: query.id })
            .then((res) => {
              this.setState({
                submitLoading: false
              })
              if (res && res.code == '1') {
                message.success('编辑成功');
                dispatch({
                  type: 'auth/clearData',
                  payload: {
                    roleList: {}
                  }
                })
                this.onBack();
              }
            })
        } else {
          //添加
          auth.roleAdd({ ...values })
            .then((res) => {
              this.setState({
                submitLoading: false
              })
              if (res && res.code == '1') {
                message.success('添加成功');
                dispatch({
                  type: 'auth/clearData',
                  payload: {
                    roleList: {}
                  }
                })
                this.onBack();
              }
            })
        }
      }
    });
  };
  onBack = () => {
    this.props.history.goBack();
  }
  // --------------------渲染 --------------------------------------------------
  renderForm() {
    const { detail } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <Fragment>
        <Form className={styles.commomForm} {...formItemLayout} onSubmit={this.onSubmit}>
          <FormItem label="角色名称">
            {getFieldDecorator('roleName', {
              initialValue: detail.roleName,
              rules: [
                { required: true, message: "请输入角色名称" }
              ]
            })(<Input placeholder="请输入角色名称" />)}
          </FormItem>
          <FormItem label="描述">
            {getFieldDecorator('remark', {
              initialValue: detail.remark,
            })(<TextArea rows={4} placeholder="请输入描述" />)}
          </FormItem>
        </Form>
      </Fragment>
    );
  }
  render() {
    const { query, submitLoading } = this.state;
    return (
      <PageHeaderWrapper title={query.id ? "编辑角色" : "添加角色"}>
        <Card>
          {this.renderForm()}
          <div className={styles.subBox}>
            <Button style={{ marginRight: 20 }} onClick={this.onClose}>关闭</Button>
            <Button type="primary" onClick={this.onSubmit} loading={submitLoading}>提交</Button>
          </div>
        </Card>
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ }) => ({}))(RoleEdit));
