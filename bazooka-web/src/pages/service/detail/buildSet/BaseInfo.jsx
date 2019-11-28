import React, { Fragment } from 'react';
import { Card, Form, Input, Row, Col } from 'antd';
import { connect } from 'dva';
const FormItem = Form.Item;
const { TextArea } = Input;
import styles from '@/pages/index.less';
class BaseInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  render() {
    const { getFieldDecorator } = this.props.formParent;
    const { data, appBaseInfo, appRunStatus, deployMode, currentItem } = this.props;
    return (
      <Fragment>
        <div className={`${styles.marginT} ${styles.marginB}`}>
          <h3 className={styles.textC}>基本信息</h3>
          <p className={styles.textC}>发布配置名、描述和发布方式等</p>
        </div>
        <Row gutter={48}>
          <Col span={12}>
            <FormItem label="名称" extra="给发布配置起一个名字，以便在使用时分别使用" colon={false}>
              {getFieldDecorator('configName', {
                initialValue: data.configName,
                rules: [
                  { required: true, message: "请输入30位以内字母中文标点符号的组合", pattern: /^[a-zA-Z-_，,.。？?：:;；《》\u4E00-\u9FA5]{1,30}$/ }
                ]
              })(<Input placeholder="请输入名称" disabled={currentItem.type === 'edit' ? true : false} />)}
            </FormItem>
            <FormItem label="描述" extra="发布配置的描述，可以让其他人了解你的发布配置的使用方式" colon={false}>
              {getFieldDecorator('configDescription', {
                initialValue: data.configDescription,
                rules: [
                  {
                    message: "最大长度100",
                    validator: async (rule, value, callback) => {
                      if (value && value.length > 100) {
                        callback('最大长度100')
                      } else {
                        callback()//必须写
                      }
                    }
                  }
                ]
              })(<TextArea placeholder="请输入描述" rows={4} />)}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem label="所属服务" help="此发布配置所属的项目/服务，不可修改" colon={false}>
              {getFieldDecorator('appId', {
                initialValue: appBaseInfo.appName,
              })(<Input placeholder="请输入所属服务" disabled />)}
            </FormItem>
            <FormItem label="所属环境" help="此发布配置使用的环境，不可修改" colon={false}>
              {getFieldDecorator('envId', {
                initialValue: appRunStatus.envName,
              })(<Input placeholder="请输入所属环境" disabled />)}
            </FormItem>
            <FormItem label="发布配置类型" help="此发布配置的类型，新建发布配置时设置，此处不可修改" colon={false}>
              {getFieldDecorator('deployMode', {
                initialValue: deployMode.indexOf("BUILD") > -1 ? '构建发布' : deployMode === 'DOCKER_IMAGE' ? '镜像发布' : undefined,
              })(<Input placeholder="请输入发布配置类型" disabled />)}
            </FormItem>
          </Col>
        </Row>

      </Fragment>
    );
  }
}
export default connect(({ service }) => ({
  appBaseInfo: service.appBaseInfo,
  appRunStatus: service.appRunStatus,
}))(BaseInfo);