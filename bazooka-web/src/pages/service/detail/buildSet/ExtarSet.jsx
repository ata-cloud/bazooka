import React, { Fragment } from 'react';
import { Card, Form, Input, Row, Col, Checkbox, Icon, Select, Divider, InputNumber } from 'antd';
import styles from '@/pages/index.less';
import { connect } from 'dva';
const FormItem = Form.Item;
const { Option } = Select;
const formItemLayout = {
  labelCol: { span: 12 },
  wrapperCol: { span: 12 },
};
class ExtraSet extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      envChecked: false,
      mountVolumeChecked: false,
      healthChecked: true
    };
  }
  componentDidMount() {
    const { data } = this.props;
    if (data.keys1 && data.keys1.length) {
      this.setState({
        envChecked: true
      })
    }
    if (data.keys2 && data.keys2.length) {
      this.setState({
        mountVolumeChecked: true
      })
    }
    if (data.id && (!data.keys3 || !data.keys3.length)) {
      this.setState({
        healthChecked: false
      })
    }
  }
  removeRange = (k, type, name) => {
    const { getFieldValue, setFieldsValue } = this.props.formParent;
    const keys = getFieldValue(type);
    if (keys.length == 1) {
      this.setState({
        [name]: false
      })
    }
    setFieldsValue({
      [type]: keys.filter(key => key !== k),
    });
  }

  addRange = (type) => {
    const { getFieldValue, setFieldsValue } = this.props.formParent;
    const keys = getFieldValue(type);
    const nextKeys = keys.length ? keys.concat(keys[keys.length - 1] + 1) : [0];
    setFieldsValue({
      [type]: nextKeys,
    });
  }
  onChange = (value, type) => {
    this.setState({
      [type]: value
    })
  }
  //环境变量
  renderEnv = () => {
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    const { envChecked } = this.state;
    const { data } = this.props;
    getFieldDecorator('keys1', { initialValue: data.keys1 || [0] });
    const keys1 = getFieldValue('keys1');
    let environmentVariable = data.environmentVariable || [];
    return (
      <Card>
        <Checkbox onChange={(e) => this.onChange(e.target.checked, 'envChecked')} checked={envChecked}>环境变量</Checkbox>
        <p>添加容器运行时的环境变量</p>
        {
          envChecked &&
          <Fragment>
            {
              keys1.map((k, index) => (
                <Row key={k} type="flex" gutter={48}>
                  <Col span={8}>
                    <FormItem>
                      {getFieldDecorator(`environmentVariable.${[k]}.key`, {
                        initialValue: environmentVariable[k] ? environmentVariable[k].key : undefined,
                        rules: [
                          { required: true, pattern: /^[a-zA-Z_]+$/, message: '请输入大小写字母下划线的组合' }
                        ]
                      })(<Input placeholder="环境变量名称" />)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem>
                      {getFieldDecorator(`environmentVariable.${[k]}.value`, {
                        initialValue: environmentVariable[k] ? environmentVariable[k].value : undefined,
                        rules: [
                          { required: true, message: '请输入环境变量值' }
                        ]
                      })(<Input placeholder="环境变量值" />)}
                    </FormItem>
                  </Col>
                  <Col>
                    <FormItem>
                      <Icon type="close" onClick={() => this.removeRange(k, 'keys1', 'envChecked')} style={{ color: '#1890ff' }} />
                    </FormItem>
                  </Col>
                </Row>
              ))
            }
            {
              (keys1.length || envChecked) ?
                <p onClick={() => this.addRange('keys1')}>
                  <a>+ 新增环境变量</a>
                </p>
                : null
            }
          </Fragment>
        }
      </Card>
    )
  }
  //持久化挂载卷
  renderMountVolume() {
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    const { mountVolumeChecked } = this.state;
    const { data } = this.props;
    getFieldDecorator('keys2', { initialValue: [0] });
    const keys2 = getFieldValue('keys2');
    let volumes = data.volumes || [];
    return (
      <Card className={styles.marginT}>
        <Checkbox onChange={(e) => this.onChange(e.target.checked, 'mountVolumeChecked')} checked={mountVolumeChecked}>持久化挂载卷</Checkbox>
        <p>容器重启或者漂移时，本地文件将会丢失，可以使用挂载卷以持久化保存本地文件</p>
        {
          mountVolumeChecked &&
          <Fragment>
            {
              keys2.map((k, index) => (
                <Row type="flex" gutter={48} key={k}>
                  <Col span={8}>
                    <FormItem>
                      {getFieldDecorator(`volumes.${[k]}.hostPath`, {
                        initialValue: volumes[k] ? volumes[k].hostPath : undefined,
                        rules: [
                          { required: true, message: '请输入主机路径' }
                        ]
                      })(<Input placeholder="主机路径" />)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem>
                      {getFieldDecorator(`volumes.${[k]}.containerPath`, {
                        initialValue: volumes[k] ? volumes[k].containerPath : undefined,
                        rules: [
                          { required: true, message: '请输入容器路径' }
                        ]
                      })(<Input placeholder="容器路径" />)}
                    </FormItem>
                  </Col>
                  <Col span={4}>
                    <FormItem>
                      {getFieldDecorator(`volumes.${[k]}.mode`, {
                        initialValue: volumes[k] ? volumes[k].mode : undefined,
                        rules: [
                          { required: true, message: '请选择' }
                        ]
                      })(
                        <Select>
                          <Option value="ReadOnly">只读</Option>
                          <Option value="ReadAndWrite">读写</Option>
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  <Col>
                    <FormItem>
                      <Icon type="close" onClick={() => this.removeRange(k, 'keys2', 'mountVolumeChecked')} style={{ color: '#1890ff' }} />
                    </FormItem>
                  </Col>
                </Row>
              ))
            }
            {
              (keys2.length || mountVolumeChecked) ?

                <p onClick={() => this.addRange('keys2')}>
                  <a>+ 新增持久化挂载卷</a>
                </p>
                : null
            }
          </Fragment>
        }
      </Card>
    )
  }
  //健康检查
  renderHealth() {
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    const { healthChecked } = this.state;
    const { data } = this.props;
    getFieldDecorator('keys3', { initialValue: data.keys3 || [0] });
    const keys3 = getFieldValue('keys3');
    let healthChecks = data.healthChecks || [];
    return (
      <Card className={styles.marginT}>
        <Checkbox onChange={(e) => this.onChange(e.target.checked, 'healthChecked')} checked={healthChecked}>健康检查</Checkbox>
        <p>无法通过所有健康检查的容器将会被重启</p>
        {
          healthChecked &&
          <Fragment>
            {
              keys3.map((k, index) => (
                <Card key={k} className={styles.marginB}>
                  <Row type="flex" gutter={16}>
                    <Fragment>
                      <Col span={6}>
                        <FormItem label="服务端口" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.portIndex`, {
                            initialValue: healthChecks[k] ? healthChecks[k].portIndex : 0,
                            rules: [
                              { required: true, message: '请选择' }
                            ]
                          })(
                            <Select>
                              {
                                data.portMappings && data.portMappings.map((item, i) => (
                                  <Option value={i} key={i}>{item.containerPort}</Option>
                                ))
                              }
                            </Select>
                          )}
                        </FormItem>
                        <FormItem label="探针协议" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.protocol`, {
                            initialValue: healthChecks[k] ? healthChecks[k].protocol : 'TCP',
                            rules: [
                              { required: true, message: '请选择' }
                            ]
                          })(
                            <Select>
                              <Option value="TCP">tcp</Option>
                              <Option value="HTTP">http</Option>
                            </Select>
                          )}
                        </FormItem>
                      </Col>
                      <Col span={1}>
                        <Divider type="vertical" dashed style={{ height: '100%' }} />
                      </Col>
                      <Col span={8}>
                        <FormItem label="初始延迟" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.gracePeriodSeconds`, {
                            initialValue: healthChecks[k] ? healthChecks[k].intervalSeconds : 60,
                            rules: [
                              { required: true, pattern: /^[1-9][0-9]*$/, message: '请输入正整数' }
                            ]
                          })(<Input placeholder="" addonAfter="秒" />)}
                        </FormItem>
                        <FormItem label="检查时间间隔" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.intervalSeconds`, {
                            initialValue: healthChecks[k] ? healthChecks[k].gracePeriodSeconds : 5,
                            rules: [
                              { required: true, pattern: /^[1-9][0-9]*$/, message: '请输入正整数' }
                            ]
                          })(<Input placeholder="" addonAfter="秒" />)}
                        </FormItem>
                        {
                          getFieldValue(`healthChecks.${[k]}.protocol`) == "HTTP" &&
                          <FormItem label="路径" {...formItemLayout}>
                            {getFieldDecorator(`healthChecks.${[k]}.path`, {
                              initialValue: healthChecks[k] ? healthChecks[k].path : '/',
                              rules: [
                                { required: true, pattern: /([/0-9a-zA-Z.]+)?/, message: '请输入url路径' }
                              ]
                            })(<Input placeholder="" />)}
                          </FormItem>
                        }
                      </Col>
                      <Col span={8}>
                        <FormItem label="超时时间" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.timeoutSeconds`, {
                            initialValue: healthChecks[k] ? healthChecks[k].timeoutSeconds : 30,
                            rules: [
                              { required: true, pattern: /^[1-9][0-9]*$/, message: '请输入正整数' }
                            ]
                          })(<Input placeholder="" addonAfter="秒" />)}
                        </FormItem>
                        <FormItem label="最大失败次数" {...formItemLayout}>
                          {getFieldDecorator(`healthChecks.${[k]}.maxConsecutiveFailures`, {
                            initialValue: healthChecks[k] ? healthChecks[k].maxConsecutiveFailures : 3,
                            rules: [
                              { required: true, pattern: /^[1-9][0-9]*$/, message: '请输入正整数' }
                            ]
                          })(<Input placeholder="" addonAfter="次" />)}
                        </FormItem>

                      </Col>
                      <Col className={`${styles.textR}`} span={1}>
                        {/* <a><Icon type="edit" className={styles.iconColor} /></a> */}
                        <a><Icon type="close" className={`${styles.iconColor} ${styles.marginL10}`} onClick={() => this.removeRange(k, 'keys3', 'healthChecked')} /></a>
                      </Col>
                    </Fragment>
                  </Row>
                </Card>
              ))
            }
            {
              (keys3.length || healthChecked) ?
                <p onClick={() => this.addRange('keys3')}>
                  <a>+ 新增健康检查</a>
                </p>
                : null
            }
          </Fragment>
        }

      </Card>
    )
  }
  render() {
    const { currentEnvO } = this.props;
    return (
      <Fragment>
        <div className={`${styles.marginT} ${styles.marginB}`}>
          <h3 className={styles.textC}>额外设置</h3>
          <p className={styles.textC}>可选设置项，可以设置容器运行时的环境变量、持久化挂载卷和健康检查等</p>
        </div>
        <div className={styles.setScroll}>
          {
            this.renderEnv()
          }
          {this.renderMountVolume()}
          { currentEnvO.clusterType !== '2' && this.renderHealth()}
        </div>

      </Fragment>
    );
  }
}
export default connect(({ service }) => ({
  currentEnvO: service.currentEnvO
}))(ExtraSet);

