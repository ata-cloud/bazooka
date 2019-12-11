import React, { Fragment } from 'react';
import { Card, Form, Row, Col, Select, Input } from 'antd';
import Commom from './commomItem';
import { IMAGE } from '@/assets/index';
const FormItem = Form.Item;
const { Option } = Select;
class GitLabItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  //新建服务
  onSubmit = (e) => {
    const { onSave } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSave({...values, appKind: 'OPS_GITLAB'})
      }
    });
  };
  renderTitle() {
    return (
      <Fragment>
        <div>
          <img src={IMAGE.GITLAB} />
          <h3>OPS托管Gitlab</h3>
          <p>OPS安装时附带安装的托管Gitlab，OPS将自动管理此Gitlab，自动创建和管理项目和服务，拉取代码时使用token，不需要额外设置凭据</p>
        </div>
      </Fragment>
    )
  }
  renderForm() {
    const { onCancel, form, projectId, loading } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form onSubmit={this.onSubmit}>
        <Row type="flex" align="top" gutter={120}>
          <Commom onCancel={onCancel} onSave={this.onSubmit} formItem={form} projectId={projectId} loading={loading}>        
          </Commom>
        </Row>
      </Form>
    )
  }
  render() {
    return (
      <Card>
        {this.renderTitle()}
        {this.renderForm()}
      </Card>
    );
  }
}

export default Form.create()(GitLabItem);
