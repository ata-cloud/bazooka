import React, { Fragment, Children } from 'react';
import { Card, Button, Form, Select, Input, Row, Col } from 'antd';
import { connect } from 'dva';
import styles from '@/pages/index.less';
const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
class CommomItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      projectId: undefined
    };
  }
  componentDidMount() {
    const { projectListAdmin } = this.props;
    if (!projectListAdmin.length) {
      this.onFetchListAdmin();
    }
  }
  onFetchListAdmin = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/projectListAdmin',
      payload: {}
    })
  }
  renderForm() {
    const { userAll, projectListAdmin, formItem, children, projectId } = this.props;
    const { getFieldDecorator } = formItem;
    return (
      <Fragment>
        <Col md={12} sm={24}>
          <FormItem label="所属项目" extra="此服务所属的项目，项目管理计算资源和用户权限">
            {getFieldDecorator('projectId', {
              initialValue: projectId ? parseInt(projectId) : undefined,
              rules: [
                { required: true, message: "请选择所属项目" }
              ]
            })(
              <Select placeholder="请选择所属项目" showSearch optionFilterProp="children" disabled={projectId ? true : false}>
                {
                  projectListAdmin && projectListAdmin.map((item) => (
                    <Option value={item.id} key={item.id}>{item.projectName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
        </Col>
        <Col md={12} sm={24}>
          <FormItem label="服务名" extra="可以使用中文、字母（大小写）、数字、中横线，在当前项目内不可重复">
            {getFieldDecorator('appName', {
              rules: [
                { required: true, message: "服务名为20位以内中文数字字母中横线的组合", pattern: /^[a-zA-z0-9-\u4E00-\u9FA5]{1,20}$/ }
              ]
            })(<Input placeholder="请输入服务名" />)}
          </FormItem>
        </Col>
        <Col md={12} sm={24}>
          <FormItem label="服务负责人" extra="服务负责人能够修改服务信息和发布配置等">
            {getFieldDecorator('leaderId', {
              rules: [
                { required: true, message: "请选择服务负责人" }
              ]
            })(
              <Select placeholder="请选择服务负责人" showSearch optionFilterProp="children">
                {
                  userAll && userAll.rows && userAll.rows.map((item) => (
                    <Option value={item.userId} key={item.userId}>{item.realName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
        </Col>
        <Col md={12} sm={24}>
          <FormItem label="服务CODE" extra={
            <Fragment>
              <div>作为服务的唯一标识，创建后不能修改，将用于代码库、镜像、容器管理</div>
              <span>可以使用字母（小写）、数字、中横线</span>
            </Fragment>
          }>
            {getFieldDecorator('appCode', {
              rules: [
                { required: true, message: "CODE为20位以内数字小写字母中横线的组合", pattern: /^[a-z0-9-]{1,20}$/ }
              ]
            })(<Input placeholder="请输入服务CODE，创建后不能修改" />)}
          </FormItem>
        </Col>
        {children}
        <Col md={12} sm={24}>
          <FormItem label="服务描述" help="服务的简单描述，不超过500字">
            {getFieldDecorator('description', {
              rules: [
                {
                  validator: async (rule, value, callback)=>{
                    if(value && value.length > 500) {
                      callback('最多500个字符')
                    }else {
                      callback()
                    }
                  }
                }
              ]
            })(<TextArea placeholder="请输入描述" rows={4} />)}
          </FormItem>
        </Col>
      </Fragment>
    )
  }
  render() {
    const { onCancel, onSave, loading } = this.props;
    return (
      <Fragment>
        {this.renderForm()}
        <Col span={24} className={styles.marginT}>
          <Button onClick={onCancel} className={styles.marginR30}>上一步</Button>
          <Button type="primary" onClick={onSave} loading={loading}>保存</Button>
        </Col>
      </Fragment>
    );
  }
}
export default Form.create()(connect(({ auth, project }) => ({
  userAll: auth.userAll,
  projectListAdmin: project.projectListAdmin,
}))(CommomItem));
