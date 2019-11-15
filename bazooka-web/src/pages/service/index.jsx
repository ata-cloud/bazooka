import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Input, Button, Modal, Form, Select, Icon, Spin, message } from 'antd';
import { connect } from 'dva';
import { ServiceItem } from '@/pages/components/'
import { COLOR_SHOW } from '@/common/constant'
import { service } from '@/services/service';
import styles from '../index.less';
import router from 'umi/router';
import { isAdmin } from '@/utils/utils';
const { Search } = Input;
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
class Service extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showAdd: false,
      searchObj: {},
      appCreateLoading: false
    };
  }
  componentDidMount() {
    this.onFetchList();
    // this.onFetchProjectList();
    this.onFetchListAdmin();
  }
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (prevState.searchObj !== searchObj) {
      this.onFetchList();
    }
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'service/clearData',
      payload: {
        appList: []
      }
    })
  }
  //------------------------事件处理-------------------------------------
  onFetchList = () => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'service/appList',
      payload: searchObj
    })
  }
  onFetchListAdmin = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/projectListAdmin',
      payload: {}
    })
  }
  onFetchProjectList = () => {
    const { projectList, dispatch } = this.props;
    if (!projectList.length) {
      dispatch({
        type: 'project/projectList'
      })
    }
  }
  onSearch = (e) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        keyword: e
      }
    })
  }
  onAdd = () => {
    router.push('/service/add')
    // this.setState({
    //   showAdd: true
    // })
  }
  onCancel = () => {
    this.setState({
      showAdd: false
    })
  }
  //新建服务
  onSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.onFetchAppCreate(values)
      }
    });
  };
  onFetchAppCreate = async (params) => {
    this.setState({
      appCreateLoading: true
    })
    let res = await service.appCreate(params);
    this.setState({
      appCreateLoading: false
    })
    if (res && res.code == '1') {
      this.setState({
        showAdd: false
      })
      message.success('添加成功');
      this.onFetchList();
    }
  }
  onRouteTo = (path, data) => {
    this.props.history.push({
      pathname: path,
      query: {
        appId: data.id,
        projectId: data.projectId
      }
    })
  }
  onTop = async (item) => {
    if (item.orderId > 0) {
      //取消置顶
      let res = await service.topDelete({ appId: item.id });
      if (res && res.code === '1') {
        message.success('取消成功');
        this.onFetchList();
      }
    } else {
      //置顶
      let res = await service.topAdd({ appId: item.id });
      if (res && res.code === '1') {
        message.success('置顶成功');
        this.onFetchList();
      }
    }
  }
  //------------------------页面渲染-------------------------------------
  renderTitle() {
    const { projectListAdmin } = this.props;
    return (
      <div>
        <p className={styles.marginB}>一个服务对应一个完整的生命周期，拥有代码管理、编译、打包、容器镜像、测试、发布、部署、运维、监控等完整的功能</p>
        <Row type="flex" justify="space-between" align="middle">
          <Col>
            {
              (isAdmin() || projectListAdmin.length) ?
                <Button type="primary" onClick={this.onAdd}>+ 新建服务</Button> : null
            }

          </Col>
          <Col>
            <Search placeholder="搜索服务" onSearch={this.onSearch} />
          </Col>
        </Row>
      </div>
    )
  }
  renderModal() {
    const { showAdd, appCreateLoading } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { userAll, projectListAdmin } = this.props;
    return (
      <Modal
        visible={showAdd}
        title="新建服务"
        onCancel={this.onCancel}
        onOk={this.onSubmit}
        confirmLoading={appCreateLoading}
      >
        <Form {...formItemLayout} onSubmit={this.onSubmit}>
          <FormItem label="服务名">
            {getFieldDecorator('appName', {
              rules: [
                { required: true, message: "服务名为20位以内中文数字字母中横线的组合", pattern: /^[a-zA-z0-9-\u4E00-\u9FA5]{1,20}$/ }
              ]
            })(<Input placeholder="请输入服务名" />)}
          </FormItem>
          <FormItem label="服务CODE">
            {getFieldDecorator('appCode', {
              rules: [
                { required: true, message: "CODE为20位以内数字小写字母中横线的组合", pattern: /^[a-z0-9-]{1,20}$/ }
              ]
            })(<Input placeholder="请输入服务CODE，创建后不能修改" />)}
          </FormItem>
          <FormItem label="服务描述">
            {getFieldDecorator('description', {
              rules: [
                {
                  message: "最多输入100个字符",
                  validator(rule, value, callback) {
                    if (value.length > 100) {
                      callback('最多输入100个字符')
                    } else {
                      callback()//必须写
                    }
                  }
                }
              ]
            })(<TextArea placeholder="请输入描述" rows={4} />)}
          </FormItem>
          <FormItem label="所属项目">
            {getFieldDecorator('projectId', {
              rules: [
                { required: true, message: "请选择所属项目" }
              ]
            })(
              <Select placeholder="请选择所属项目" showSearch optionFilterProp="children">
                {
                  projectListAdmin && projectListAdmin.map((item) => (
                    <Option value={item.id} key={item.id}>{item.projectName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
          <FormItem label="服务负责人">
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
        </Form>

      </Modal>
    )
  }
  renderList() {
    const { list, projectListAdmin } = this.props;
    return (
      <Fragment>
        <Row gutter={24}>
          {
            list && list.length ? list.map((item, i) => (
              <Col key={i} md={8} sm={12}>
                <ServiceItem item={{ ...item, index: i }} toTop={() => this.onTop(item)} onItemClick={() => this.onRouteTo('/service/detail', item)} />
              </Col>
            )) :
              <Col span={8}>
                {
                  (isAdmin() || projectListAdmin.length) ?
                    <Button type="dashed" className={styles.listItem} onClick={this.onAdd}>
                      <Icon type="plus" />
                      <span>新建服务</span>
                    </Button> : null
                }
              </Col>
          }
        </Row>
      </Fragment>
    )
  }
  render() {
    const { showAdd } = this.state;
    const { loading } = this.props;
    return (
      <PageHeaderWrapper content={this.renderTitle()}>
        <Spin spinning={loading}>
          {this.renderList()}
        </Spin>
        {showAdd && this.renderModal()}
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ service, loading, auth, project }) => ({
  list: service.appList,
  userAll: auth.userAll,
  projectList: project.projectList,
  projectListAdmin: project.projectListAdmin,
  loading: loading.effects["service/appList"]
}))(Service));

