import React, { Fragment } from 'react';
import { Card, Form, Row, Col, Input, Icon,message } from 'antd';
import { connect } from 'dva';
import CommomItem from './commomItem';
import styles from '@/pages/index.less';
import { IMAGE } from '@/assets/';
import stylesAddCluster from './index.less';
import { cluster } from '@/services/cluster'

const FormItem = Form.Item;

class Mesos extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  onSubmit = (e) => {
    const { onCancel } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll(async (err, values) => {
      if (!err) {
        let res = await cluster.createMesosCluster({...values, type: '0', keys: undefined, keys1: undefined, credentialId: values.credentialId=='-1'? undefined: values.credentialId});
        if(res && res.code == '1') {
          message.success('添加成功')
          onCancel()
        }
        // onSave({ ...values })
      }
    });
  }
 
  addKey = (type) => {
    const { getFieldValue, setFieldsValue } = this.props.form;
    const keys = getFieldValue(type);
    const nextKeys = keys.concat(keys[keys.length - 1] + 1);
    setFieldsValue({
      [type]: nextKeys,
    });
  }
  removeKey = (k, type) => {
    const { getFieldValue, setFieldsValue } = this.props.form;
    const keys = getFieldValue(type);
    if (keys.length === 1) {
      return;
    }
    setFieldsValue({
      [type]: keys.filter(key => key !== k),
    });
  }
  renderTitle() {
    return (
      <div className={styles.flexCenter}>
        <img src={IMAGE.MESOS} />
        <div className={styles.marginL10}>
          <div className={stylesAddCluster.clusterTitle}>Mesos集群</div>
          <span>使用Mesos集群的方式来管理资源</span>
        </div>
      </div>
    )
  }
  renderForm() {
    const { onCancel, form, credentialsList } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form onSubmit={this.onSubmit} colon={false} className={styles.marginT}>
        <Row type="flex" align="top" gutter={48}>
          <CommomItem onCancel={onCancel} onSave={this.onSubmit} formItem={form}>
            <Col md={8} sm={24}>
              <FormItem label={
                <Fragment>
                  <span>Master节点ip列表</span>
                  <span className={styles.marginL10}><Icon type="plus" style={{ color: '#1890ff' }} onClick={() => this.addKey('keys')} /></span>
                </Fragment>
              } required={true} help="请填写Mesos集群所有Master节点列表，并保证此节点ip能被Bazooka服务访问">
                {
                  this.renderMesosIp()
                }
              </FormItem>
            </Col>
            <Col md={8} sm={24}>
              <FormItem label={
                <Fragment>
                  <span>Public agent节点ip列表</span>
                  <span className={styles.marginL10}><Icon type="plus" style={{ color: '#1890ff' }} onClick={() => this.addKey('keys1')} /></span>
                </Fragment>
              } required={true} help={
                <span>请填写Mesos集群所有public agent节点列表，请在public agent节点上<a href="https://github.com/ata-cloud/bazooka" target="_black">部署marathon-lb容器</a>并保证此节点ip能被Bazooka服务访问</span>
              }>
                {
                  this.renderPublicIp()
                }
              </FormItem>
            </Col>
          </CommomItem>
        </Row>
      </Form>
    )

  }
  renderMesosIp() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    getFieldDecorator('keys', { initialValue: [0] });
    let keys = getFieldValue('keys');
    return (
      <Fragment>
        {
          keys.map((k) => (
            <div className={styles.flex} key={k}>
              <FormItem className={styles.flex1}>
                {getFieldDecorator(`masterUrls.${k}`, {
                })(<Input placeholder="请输入master节点ip" />)}
              </FormItem>
              <FormItem className={styles.marginL10}>
                {keys.length > 1 && <Icon type="minus" style={{ color: '#1890ff' }} onClick={() => { this.removeKey(k, 'keys') }} />}
              </FormItem>
            </div>
          ))
        }
      </Fragment>
    )
  }
  renderPublicIp() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    getFieldDecorator('keys1', { initialValue: [0] });
    let keys1 = getFieldValue('keys1');
    return (
      <Fragment>
        {
          keys1.map((k) => (
            <div className={styles.flex} key={k}>
              <FormItem className={styles.flex1}>
                {getFieldDecorator(`mlbUrls.${k}`, {
                })(<Input placeholder="请输入public agent节点ip" />)}
              </FormItem>
              <FormItem className={styles.marginL10}>
                {keys1.length > 1 && <Icon type="minus" style={{ color: '#1890ff' }} onClick={() => { this.removeKey(k, 'keys1') }} />}
              </FormItem>
            </div>
          ))
        }
      </Fragment>
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
export default Form.create()(connect(({ system }) => ({
  credentialsList: system.credentialsList
}))(Mesos));
