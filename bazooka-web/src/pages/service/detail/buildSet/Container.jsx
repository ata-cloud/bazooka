import React, { Fragment } from 'react';
import { Card, Form, Input, Select, Row, Col, Button, Icon, Checkbox, message } from 'antd';
import { connect } from 'dva';
import styles from '@/pages/index.less';
import { SCALE_CPU, SCALE_MEN, SCALE_CASE } from '@/common/constant';
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
class Container extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ports: [],
      keys: [0]
    };
  }
  componentDidMount() {
    this.onGetPorts();
    // const { data } = this.props;

    // this.setState({
    //   ports: data.keys ? [...data.keys] : []
    // })
  }
  onGetPorts = () => {
    const { data } = this.props;
    let ports = [];
    data.portMappings && data.portMappings.map((item, i) => {
      if (item.servicePort) {
        ports.push(i)
      }
    })
    this.setState({
      ports
    })
  }
  removeRange = (k, type) => {
    const { getFieldValue, setFieldsValue } = this.props.formParent;
    const keys = getFieldValue(type);
    if (keys.length === 1) {
      return;
    }
    setFieldsValue({
      [type]: keys.filter(key => key !== k),
    });
  }

  addRange = (type) => {
    const { getFieldValue, setFieldsValue } = this.props.formParent;
    const keys = getFieldValue(type);
    const nextKeys = keys.concat(keys[keys.length - 1] + 1);
    setFieldsValue({
      [type]: nextKeys,
    });
  }
  onPortChange = (value, k, containerPort) => {
    if (!containerPort) {
      message.warning('请先输入服务端口')
      return
    }
    const { ports } = this.state;
    let currentPorts = ports;
    if (value) {
      let i = currentPorts.indexOf(k)
      if (i > -1) {
        currentPorts.splice(i, 1)
      } else {
        currentPorts.push(k);
        this.onFetchServicePort({ containerPort })
      }
    } else {
      let i = currentPorts.indexOf(k)
      if (i > -1) {
        currentPorts.splice(i, 1)
      }
    }
    this.setState({
      ports: currentPorts
    })
  }
  //LB端口获取
  onFetchServicePort = (params = {}) => {
    const { info, dispatch, appDeployServicePort } = this.props;
    if (appDeployServicePort[params.containerPort]) {
      return
    }
    dispatch({
      type: 'service/appDeployServicePort',
      payload: {
        appId: info.appId,
        envId: info.envId,
        containerPort: params.containerPort
      }
    })

  }
  renderPort() {
    const { ports } = this.state;
    const { data, appDeployServicePort } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    getFieldDecorator('keys', { initialValue: data.keys || [0] });
    let keys = getFieldValue('keys');
    let portMappings = data.portMappings || [];
    return (
      <Fragment>
        {
          keys.map((k, index) => (
            <Row type="flex" key={k} gutter={16}>
              <Col span={5}>
                <FormItem>
                  {getFieldDecorator(`portMappings.${k}.containerPort`, {
                    initialValue: portMappings[k] ? portMappings[k].containerPort : undefined,
                    rules: [
                    { required: true, message: '格式错误', pattern: /^[0-9]*$/ }
                    ]
                  })(<Input placeholder="请输入" />)}
                </FormItem>
              </Col>
              <Col span={9}>
                <Checkbox onChange={(e) => { this.onPortChange(e.target.checked, k, getFieldValue(`portMappings.${k}.containerPort`)) }} checked={ports.indexOf(k) > -1 ? true : false}>
                  {
                    ports.indexOf(k) > -1 ? "LB分配端口" : "提供集群外访问"
                  }
                </Checkbox>
              </Col>
              {
                ports.indexOf(k) > -1 &&
                <Col span={5}>
                  <FormItem>
                    {getFieldDecorator(`portMappings.${k}.servicePort`, {
                      initialValue: portMappings[k] && portMappings[k].servicePort ? portMappings[k].servicePort : appDeployServicePort[getFieldValue(`portMappings.${k}.containerPort`)] ? appDeployServicePort[getFieldValue(`portMappings.${k}.containerPort`)] : undefined,
                      rules: [
                        { required: true, message: '格式错误', pattern: /^[0-9]*$/ }
                      ]
                    })(<Input placeholder="请输入" />)}
                  </FormItem>
                </Col>
              }
              <Col>
                {keys.length > 1 ? (
                  <Icon type="close" onClick={() => this.removeRange(k, 'keys')} style={{ color: '#1890ff' }} />
                ) : null}
              </Col>
            </Row>
          ))
        }
        <p onClick={() => this.addRange('keys')}>
          <a>+ 新增服务端口</a>
        </p>
      </Fragment>
    )
  }
  render() {
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    const { data } = this.props;
    return (
      <Fragment>
        <div className={`${styles.marginT} ${styles.marginB}`}>
          <h3 className={styles.textC}>容器运行</h3>
          <p className={styles.textC}>设置容器运行时的参数，如计算资源、启动命令、服务端口等</p>
        </div>
        <Row gutter={48}>
          <Col span={12}>
            <FormItem label="服务启动命令" extra="启动服务的命令，例如：exec java xxxxx.jar" colon={false}>
              {getFieldDecorator('startCommand', {
                initialValue: data.startCommand,
                // rules: [
                //   { required: true, message: "请输入服务启动命令" }
                // ]
              })(<TextArea placeholder="请输入服务启动命令" rows={4} />)}
            </FormItem>
            <FormItem label="容器计算资源" required={true} colon={false}>
              <FormItem label="CPU"  {...formItemLayout}>
                {getFieldDecorator('cpus', {
                  initialValue: data.cpus || 0.3,
                  rules: [
                    { required: true, message: "请选择CPU" }
                  ]
                })(
                  <Select>
                    {
                      SCALE_CPU.map((item) => (
                        <Option value={item} key={item}>{item + ' Core'}</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
              <FormItem label="内存"  {...formItemLayout}>
                {getFieldDecorator('memory', {
                  initialValue: data.memory || 1024,
                  rules: [
                    { required: true, message: "请选择内存" }
                  ]
                })(
                  <Select>
                    {
                      SCALE_MEN.map((item) => (
                        <Option value={item} key={item}>{
                          item % 1024 == 0 ? item / 1024 + ' GiB' : item + ' MiB'
                        }</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
              <FormItem label="容器个数"  {...formItemLayout}>
                {getFieldDecorator('instance', {
                  initialValue: data.instance || 1,
                  rules: [
                    { required: true, message: "请选择容器个数" }
                  ]
                })(
                  <Select>
                    {
                      SCALE_CASE.map((item) => (
                        <Option value={item} key={item}>{item}</Option>
                      ))
                    }
                  </Select>
                )}
              </FormItem>
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem label="服务端口" required={true} help={
              <Fragment>
                <div>设置服务运行时端口，例如tomcat端口：8080</div>
                <div>如果此端口需要被集群外访问，可以勾选“提供集群外访问”，将根据项目和环境自动为此端口在Marathon-LB上分配端口，您也可以自行指定</div>
                <div>如果服务需要多个端口，请使用“新增服务端口</div>
              </Fragment>
            }>
              {
                this.renderPort()
              }
            </FormItem>
          </Col>
        </Row>
      </Fragment>
    );
  }
}
export default connect(({ service }) => ({
  appDeployServicePort: service.appDeployServicePort,
}))(Container);