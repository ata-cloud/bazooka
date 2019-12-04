import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Modal, Form, Input, Radio, Button, Select, message, Slider, InputNumber } from 'antd';
import { connect } from 'dva';
import { cluster } from '@/services/cluster';
import { ENV_STATUS_ARR, CLUSTER_TYPE_O } from '@/common/constant';
import styles from '../index.less';
import { MformG } from '@/utils/utils';
const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 3 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
};
class EnvEditModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      clusterId: '',
      clusterType: '',
      availableResource: {}
    };
  }
  componentDidMount() {
    const { currentItem, clusterList } = this.props;
    if (Object.getOwnPropertyNames(clusterList).length === 0) {
      this.onFetchClusterList();
    }
    this.setState({
      clusterId: currentItem.clusterId,
      clusterType: currentItem.clusterType ? currentItem.clusterType : ''
    })
  }
  componentDidUpdate(prevProps, pervState) {
    const { clusterId, clusterType } = this.state;
    if (clusterId && clusterId !== pervState.clusterId && clusterType !== '2') {
      this.onFetchAvailableResource();
    }
  }
  onFetchClusterList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'cluster/clusterList',
      payload: {pageSize: 100}
    })
  }
  //获取可分配资源
  onFetchAvailableResource = async () => {
    const { clusterId } = this.state;
    let res = await cluster.getAvailableResource({ clusterId });
    this.setState({
      availableResource: res.data || {}
    })
  }
  onFetchItem = () => {
    const { clusterList, dispatch } = this.props;
    if (Object.getOwnPropertyNames(clusterList).length === 0) {
      this.onFetchClusterList()
    }
  }
  onSlideChange = (value, type) => {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      [type]: Number(value)
    })

  }
  onClusterChange = (value, option) => {
    this.setState({
      clusterId: value,
      clusterType: option.props.label
    })
  }
  onSubmit = (e) => {
    const { onOk } = this.props;
    const { clusterType } = this.state;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = {}
        if (clusterType !== '2') {
          params = {
            ...values,
            disk: values.disk * 1024,
            memory: values.memory * 1024
          }
          if (params.cpus && params.cpus < 0) {
            message.error('CPU不能小于0');
            return
          }
          if (params.memory && params.memory < 0) {
            message.error('内存不能小于0');
            return
          }
          if (params.disk && params.disk < 0) {
            message.error('磁盘不能小于0');
            return
          }
        }else {
          params = {
            ...values,
            cpus: 0,
            disk: 0,
            memory: 0
          }
        }
        onOk(params)
      }
    });
  };
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { clusterId, availableResource, clusterType } = this.state;
    const { clusterList, currentItem } = this.props;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit}>
        <FormItem label="命名">
          {getFieldDecorator('envName', {
            initialValue: currentItem.name,
            rules: [
              { required: true, message: "请输入长度20以内的汉字字母数字中横线的组合", pattern: /^[\u4E00-\u9FA5A-Za-z0-9-]{1,20}$/ }
            ]
          })(<Input placeholder="请输入命名" disabled={currentItem.id ? true : false} />)}
        </FormItem>
        <FormItem label="CODE">
          {getFieldDecorator('envCode', {
            initialValue: currentItem.code,
            rules: [
              { required: true, message: "请输入长度20以内的小写字母数字中横线的组合", pattern: /^[a-z0-9-]{1,20}$/ }
            ]
          })(<Input placeholder="请输入CODE" disabled={currentItem.id ? true : false} />)}
        </FormItem>
        {
          currentItem.id &&
          <Fragment>
            {
              currentItem.state == '2' ?
                <FormItem label="环境状态">
                  {getFieldDecorator('envState', {
                    initialValue: '2',
                  })(
                    <Select placeholder="请选择环境状态" showSearch disabled>
                      <Option value="2">
                        <span className={styles.errorColor}>集群异常</span>
                      </Option>
                    </Select>
                  )}
                </FormItem> :
                <FormItem label="环境状态">
                  {getFieldDecorator('envState', {
                    initialValue: currentItem.state,
                  })(
                    <Select placeholder="请选择环境状态" showSearch>
                      {
                        ENV_STATUS_ARR.map((item) => (
                          <Option value={item.value} key={item.value}>{item.text}</Option>
                        ))
                      }
                    </Select>

                  )}
                </FormItem>
            }
          </Fragment>
        }
        <FormItem label="资源">
          {getFieldDecorator('clusterId', {
            initialValue: currentItem.clusterId,
            rules: [
              {
                required: true,
                message: '请选择资源'
              }
            ]
          })(
            <Select placeholder="请选择资源" showSearch onChange={this.onClusterChange} disabled={currentItem.projectNum > 0 ? true : false}>
              {
                clusterList && clusterList.rows && clusterList.rows.map((item, i) => (
                  <Option value={item.clusterId} key={item.clusterId} label={item.type}>{item.name}（{CLUSTER_TYPE_O[item.type]}）</Option>
                ))
              }
            </Select>
          )}
        </FormItem>
        {
          clusterType !== '2' && getFieldValue('clusterId') && <div className={styles.paddingL}>
            <p>
              <span>环境资源（集群可分配资源：</span>
              <span>{((currentItem.cpus || 0) + (availableResource.cpu || 0)).toFixed(1)} CPU / </span>
              <span>{MformG((currentItem.memory || 0) + (availableResource.memory || 0))} GiB 内存 / </span>
              <span>{MformG((currentItem.disk || 0) + (availableResource.disk || 0))} GiB 磁盘</span>
              <span>）</span>
            </p>
            <div className={`${styles.flexCenter} ${styles.paddingL}`}>
              <div className={styles.marginR}>CPU</div>
              <div className={`${styles.flex1} ${styles.marginR30}`}>
                <Slider
                  step={0.1}
                  min={currentItem.cpusUsed}
                  max={(currentItem.cpus || 0) + (availableResource.cpu || 0)}
                  onChange={(value) => this.onSlideChange(value, 'cpus')}
                  value={typeof getFieldValue('cpus') == 'number' ? getFieldValue('cpus') : 0}
                />
              </div>
              <FormItem style={{ marginBottom: 0 }}>
                {getFieldDecorator('cpus', {
                  initialValue: currentItem.cpus ? currentItem.cpus : 0,
                  // rules: [
                  //   {
                  //     validator(rule, value, callback) {
                  //       let max = (currentItem.cpus || 0) + (availableResource.cpu || 0);
                  //       console.log('max-->', max)
                  //       if (value > max || value < 0) {
                  //         callback(`不能小于0且不能超过最大值`)
                  //       }else {
                  //         callback()
                  //       }
                  //     }
                  //   }
                  // ]
                })(<InputNumber max={(currentItem.cpus || 0) + (availableResource.cpu || 0)} />)}
              </FormItem>
              <div className={styles.width60}>Core</div>
            </div>

            <div className={`${styles.flexCenter} ${styles.paddingL}`}>
              <div className={styles.marginR}>内存</div>
              <div className={`${styles.flex1} ${styles.marginR30}`}>
                <Slider
                  step={0.1}
                  min={MformG(currentItem.memoryUsed)}
                  max={MformG((currentItem.memory || 0) + (availableResource.memory || 0))}
                  onChange={(value) => this.onSlideChange(value, 'memory')}
                  value={typeof getFieldValue('memory') == 'number' ? getFieldValue('memory') : 0}
                />
              </div>
              <FormItem style={{ marginBottom: 0 }}>
                {getFieldDecorator('memory', {
                  initialValue: currentItem.memory ? MformG(currentItem.memory) : 0,
                })(<InputNumber max={MformG((currentItem.memory || 0) + (availableResource.memory || 0))} />)}
              </FormItem>
              <div className={styles.width60}>GiB</div>
            </div>
            <div className={`${styles.flexCenter} ${styles.paddingL}`}>
              <div className={styles.marginR}>磁盘</div>
              <div className={`${styles.flex1} ${styles.marginR30}`}>
                <Slider
                  step={0.1}
                  min={MformG(currentItem.diskUsed)}
                  max={MformG((currentItem.disk || 0) + (availableResource.disk || 0))}
                  onChange={(value) => this.onSlideChange(value, 'disk')}
                  value={typeof getFieldValue('disk') == 'number' ? getFieldValue('disk') : 0}
                />
              </div>
              <FormItem style={{ marginBottom: 0 }}>
                {getFieldDecorator('disk', {
                  initialValue: currentItem.disk ? MformG(currentItem.disk) : 0,
                })(<InputNumber max={MformG((currentItem.disk || 0) + (availableResource.disk || 0))} />)}
              </FormItem>
              <div className={styles.width60}>GiB</div>
            </div>
          </div>
        }


      </Form>
    )
  }
  render() {
    const { visible, onCancel, currentItem, confirmLoading } = this.props;
    return (
      <Modal
        visible={visible}
        title={currentItem.id ? "修改环境" : "新增环境"}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={confirmLoading}
        width={600}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}
export default Form.create()(connect(({ cluster }) => ({
  clusterList: cluster.clusterList,
}))(EnvEditModal));

