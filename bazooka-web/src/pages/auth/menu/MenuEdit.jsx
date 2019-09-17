import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Input, Radio, Button, Select, message } from 'antd';
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
class MenuEdit extends React.Component {
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
    this.onFetchItems();
  }
  // --------------------方法 -----------------------------------------------------
  onFetchDetail = () => {
    const { query } = this.state;
    auth.permissiongetPage({ permissionId: query.id })
      .then((res) => {
        if (res && res.code == '1') {
          this.setState({
            detail: res.data && res.data.rows ? res.data.rows[0] : {}
          })
        }
      })
  }
  onFetchItems = () => {
    const { dispatch, list } = this.props;
    if (Object.getOwnPropertyNames(list).length === 0) {
      dispatch({
        type: 'auth/permissionList',
        payload: {
          pageSize: 100
        }
      })
    }
  }
  onSubmit = (e) => {
    const { dispatch } = this.props;
    const { query } = this.state;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          submitLoading: true
        });
        if (query.id) {
          //编辑
          auth.permissionEdit({ ...values, permissionId: query.id })
            .then((res) => {
              this.setState({
                submitLoading: false
              })
              if (res && res.code == '1') {
                message.success('编辑成功');
                dispatch({
                  type: 'auth/clearData',
                  payload: {
                    permissionList: {}
                  }
                })
                this.onBack();
              }
            })
        } else {
          //添加
          auth.permissionAdd({ ...values })
            .then((res) => {
              this.setState({
                submitLoading: false
              })
              if (res && res.code == '1') {
                message.success('添加成功');
                dispatch({
                  type: 'auth/clearData',
                  payload: {
                    permissionList: {}
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
    const { getFieldDecorator } = this.props.form;
    const { list } = this.props;
    const { detail } = this.state;
    return (
      <Fragment>
        <Form className={styles.commomForm} {...formItemLayout} onSubmit={this.onSubmit}>
          <FormItem label="权限名称">
            {getFieldDecorator('permissionName', {
              initialValue: detail.permissionName,
              rules: [
                { required: true, message: "请输入权限名称" }
              ]
            })(<Input placeholder="请输入权限名称" />)}
          </FormItem>
          <FormItem label="类型">
            {getFieldDecorator('type', {
              initialValue: detail.type ? detail.type : 0,
            })(
              <Select placeholder="请选择类型" showSearch>
                <Option value={0}>菜单</Option>
              </Select>,
            )}
          </FormItem>
          <FormItem label="接口地址">
            {getFieldDecorator('url', {
              initialValue: detail.url,
              rules: [
                { required: true, message: "请输入接口地址" }
              ]
            })(<Input placeholder="请输入" />)}
          </FormItem>
          <FormItem label="父节点">
            {getFieldDecorator('parentId', {
              initialValue: detail.parentId,
              rules: [
                { required: true, message: "请选择父节点" }
              ]
            })(
              <Select placeholder="请选择父节点" showSearch>
                <Option value={0}>root</Option>
                {
                  list.rows && list.rows.map((item) => (
                    <Option value={item.permissionId} key={item.permissionId}>{item.permissionName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
          <FormItem label="排序优先级">
            {getFieldDecorator('rank', {
              initialValue: detail.rank,
              rules: [
                { pattern: /^[0-9]\d*$/, message: "请输入正整数" }
              ]
            })(<Input placeholder="请输入排序优先级" />)}
          </FormItem>
          <FormItem label="备注">
            {getFieldDecorator('remark', {
              initialValue: detail.remark,
            })(<TextArea rows={4} placeholder="请输入备注" />)}
          </FormItem>
        </Form>
      </Fragment>
    );
  }
  render() {
    const { query, submitLoading } = this.state;
    return (
      <PageHeaderWrapper title={query.id ? "编辑权限" : "添加权限"}>
        <Card>
          {this.renderForm()}
          <div className={styles.subBox}>
            <Button style={{ marginRight: 20 }} onClick={this.onBack}>关闭</Button>
            <Button type="primary" onClick={this.onSubmit} loading={submitLoading}>提交</Button>
          </div>
        </Card>
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ auth }) => ({
  list: auth.permissionList,
}))(MenuEdit));
