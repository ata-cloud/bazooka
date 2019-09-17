import React, { Fragment } from 'react';
import { Card, Form, Input, Select, Row, Col } from 'antd';
import { connect } from 'dva';
import styles from '@/pages/index.less';
const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
class Compile extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  render() {
    const { getFieldDecorator, getFieldValue } = this.props.formParent;
    const { data, appBaseInfo } = this.props;
    return (
      <Fragment>
        <div className={`${styles.marginT} ${styles.marginB}`}>
          <h3 className={styles.textC}>编译构建</h3>
          <p className={styles.textC}>代码编译和镜像相关的设置</p>
        </div>
        <Row gutter={48}>
          <Col span={12}>
            <FormItem label="代码仓库" help="代码编译打包时将从此Gitlab代码仓库拉取代码，代码仓库地址和服务绑定，不可修改" colon={false}>
              <Input placeholder="请输入代码仓库" disabled value={appBaseInfo.gitlabUrl} />
            </FormItem>
            <FormItem label="发布可选分支" help="使用此发布配置时，可以选择用于代码编译打包的gitlab分支，如果选择“允许特定代码分支”或者“禁止特定代码分支”，可以在最前或者最后使用*代指任意字符" colon={false}>
              {getFieldDecorator('branch', {
                initialValue: data.branch ? data.branch : '1',
                rules: [
                  { required: true, message: "请选择可选分支" }
                ]
              })(
                <Select>
                  <Option value="1">全部代码分支</Option>
                  <Option value="2">允许特定代码分支</Option>
                  <Option value="3">禁止特定代码分支</Option>
                </Select>
              )}
              {
                getFieldValue('branch') === '2' &&
                <FormItem>
                  {getFieldDecorator('gitBranch', {
                    initialValue: data.gitBranchAllow,
                    rules: [
                      {
                        required: true, pattern: /^\*?[a-z]+\*?$/, message: '示例 *master*(*为通配符，可写可不写,分支名为小写字母)'
                      }
                    ]
                  })(<Input placeholder="请输入允许特定代码分支" />)}
                </FormItem>
              }
              {
                getFieldValue('branch') === '3' &&
                <FormItem>
                  {getFieldDecorator('gitBranch', {
                    initialValue: data.gitBranchDeny,
                    rules: [
                      {
                        required: true, pattern: /^\*?[a-z]+\*?$/, message: '示例 *master*(*为通配符，可写可不写,分支名为小写字母)'
                      }
                    ]
                  })(<Input placeholder="请输入禁止特定代码分支" />)}
                </FormItem>
              }
            </FormItem>
            <FormItem label="代码编译命令" help="用于编译代码的命令，根据编程语言和框架不同使用不同的编译命令，多条命令用英文分号分隔" colon={false}>
              {getFieldDecorator('compileCommand', {
                initialValue: data.compileCommand,
                // rules: [
                //   { required: true, message: "请输入代码编译命令" }
                // ]
              })(<TextArea placeholder="请输入代码编译命令" rows={4} />)}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem label="镜像名" help="构建的镜像将使用此镜像名，镜像名由环境指定的镜像库和服务名组成，不可修改" colon={false}>
              {getFieldDecorator('mirName', {
                initialValue: appBaseInfo.dockerImageName,
              })(<Input placeholder="请输入镜像名" disabled />)}
            </FormItem>
            <FormItem label="Dockerfile相对路径" help="构建镜像时使用的Dockerfile文件在代码仓库中的相对路径地址，默认为docker/Dockerfile" colon={false}>
              {getFieldDecorator('dockerfilePath', {
                initialValue: data.dockerfilePath || 'docker/Dockerfile',
                rules: [
                  { required: true, message: "请输入Dockerfile相对路径" }
                ]
              })(<Input placeholder="请输入Dockerfile相对路径" />)}
            </FormItem>
          </Col>
        </Row>

      </Fragment>
    );
  }
}
export default connect(({ service }) => ({
  appBaseInfo: service.appBaseInfo,
}))(Compile);
