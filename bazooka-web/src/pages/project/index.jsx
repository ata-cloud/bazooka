import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Input, Button, Modal, Form, Select, Icon, List, Dropdown, Menu, Tag, Checkbox, Radio, InputNumber, Spin, message, Divider } from 'antd';
import { connect } from 'dva';
import Link from 'umi/link';
import styles from '../index.less';
import { COLOR_SHOW } from '@/common/constant';
import { project } from '@/services/project';
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
const formItemLayout1 = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 11 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 13 },
  },
};
class Project extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showAdd: false,
      searchObj: {},
      submitLoading: false,
      envSelect: [],
      portLB: {}
    };
  }
  componentDidMount() {
    this.onFetchList();
    this.onFetchItem();
  }
  //------------------------事件处理-------------------------------------
  onFetchList = (params = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/projectList',
      payload: params
    })
  }
  onFetchItem = () => {
    const { envList, dispatch } = this.props;
    if (!envList.length) {
      dispatch({
        type: 'env/envList',
        payload: {}
      })
    }
  }

  onSearch = (e) => {
    this.onFetchList({
      keyword: e
    })
  }
  onAdd = () => {
    this.setState({
      showAdd: true
    })
  }
  onCancel = () => {
    this.setState({
      showAdd: false
    })
  }
  //新建服务
  onSubmit = (e) => {
    const { searchObj } = this.state;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let envList = [], devUserIds = [];
        values.envList.map((vo) => {
          envList.push({ envId: vo })
        });
        values.devUserIds && values.devUserIds.map((vo) => {
          devUserIds.push({ userId: vo.key, userName: vo.label })
        });
        this.setState({
          submitLoading: true
        });
        this.onFetchAddPro({ ...values, envList, devUserIds });
      }
    });
  };
  onFetchAddPro = async (params) => {
    let res = await project.createProject(params);
    this.setState({
      submitLoading: false
    })
    this.onFetchList();
    if (res && res.code == '01041013') {
      this.onCancel();
    }
    if (res && res.code == '1') {
      message.success('添加成功');
      this.onCancel();
    }
  }
  onRouteTo = (path, data) => {
    this.props.history.push({
      pathname: path,
      query: {
        projectId: data.projectId
      }
    })
  }
  //置顶
  onTop = async (item) => {
    if (item.orderId > 0) {
      //取消置顶
      let res = await project.deleteTopProject({ projectId: item.projectId });
      if (res && res.code == '1') {
        message.success('取消成功');
        this.onFetchList();
      }
    } else {
      //置顶
      let res = await project.topProject({ projectId: item.projectId });
      if (res && res.code == '1') {
        message.success('置顶成功');
        this.onFetchList();
      }
    }
  }
  //根据环境获取端口
  onFetchLBport = async (envId) => {
    const { portLB } = this.state;
    let res = await project.queryDistributePort({ envId });
    this.setState({
      portLB: {
        ...portLB,
        [envId]: res.data
      }
    })
  }
  onEnvChange = (value) => {
    const { portLB, envSelect } = this.state;
    if (value.length && value.length > envSelect.length) {
      this.onFetchLBport(value[value.length - 1])
    }
    this.setState({
      envSelect: value
    })
  }
  //------------------------页面渲染-------------------------------------
  renderTitle() {
    return (
      // <Row type="flex" justify="space-between" align="middle">
      //   <Col>一个项目管理一系列的服务，关联一个项目负责人和多个参与人，同时关联多个<Link to="/environment">环境</Link>用于服务的部署</Col>
      //   <Col className={styles.flexCenter}>
      //     <Search placeholder="搜索项目" onSearch={this.onSearch} />
      //     <Button type="primary" className={styles.marginL} onClick={this.onAdd}>+ 新建项目</Button>
      //   </Col>
      // </Row>
      <div>
        <p className={styles.marginB}>一个项目管理一系列的服务，关联一个项目负责人和多个参与人，同时关联多个<Link to="/environment">环境</Link>用于服务的部署</p>
        <Row type="flex" justify="space-between" align="middle">
          <Col>
            {
              isAdmin() &&
              <Button type="primary" onClick={this.onAdd}>+ 新建项目</Button>
            }
          </Col>
          <Col>
            <Search placeholder="搜索项目" onSearch={this.onSearch} />
          </Col>
        </Row>
      </div>
    )
  }
  renderModal() {
    const { showAdd, submitLoading, portLB } = this.state;
    const { userAll, envList } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <Modal
        visible={showAdd}
        width={800}
        title="新建项目"
        onCancel={this.onCancel}
        onOk={this.onSubmit}
        confirmLoading={submitLoading}>

        <Form {...formItemLayout} onSubmit={this.onSubmit}>
          <FormItem label="项目名">
            {getFieldDecorator('projectName', {
              rules: [
                { required: true, message: "请输入项目名", message: "请输入20位以内中文数字字母中横线的组合", pattern: /^[A-Za-z0-9-\u4E00-\u9FA5]{1,20}$/ }
              ]
            })(<Input placeholder="请输入项目名" />)}
          </FormItem>
          <FormItem label="项目CODE">
            {getFieldDecorator('projectCode', {
              rules: [
                { required: true, message: "请输入项目CODE" }
              ]
            })(<Input placeholder="请输入项目CODE，创建后不能修改" />)}
          </FormItem>
          <FormItem label="描述">
            {getFieldDecorator('description', {
              rules: [
                {
                  validator(rule, value, callback) {
                    if (value.length > 500) {
                      callback('最多500个字符')
                    } else {
                      callback()
                    }
                  }
                }
              ]
            })(<TextArea placeholder="请输入项目描述" rows={4} />)}
          </FormItem>
          <FormItem label="负责人">
            {getFieldDecorator('masterUserId', {
              rules: [
                { required: true, message: "请选择负责人" }
              ]
            })(
              <Select placeholder="请选择负责人" showSearch optionFilterProp="children">
                {
                  userAll && userAll.rows && userAll.rows.map((item) => (
                    <Option value={item.userId} key={item.userId}>{item.realName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>

          <FormItem label="参与人">
            {getFieldDecorator('devUserIds', {
            })(
              <Select placeholder="请选择参与人" showSearch mode="multiple" allowClear optionFilterProp="children" labelInValue>
                {
                  userAll && userAll.rows && userAll.rows.map((item) => (
                    <Option value={item.userId} key={item.userId}>{item.realName}</Option>
                  ))
                }
              </Select>,
            )}
          </FormItem>
          <FormItem label="环境资源">
            {getFieldDecorator('envList', {
              rules: [
                { required: true, message: "至少选择一个环境资源" }
              ]
            })(
              <Checkbox.Group style={{ width: '100%', lineHeight: 'unset' }}>
                <Row type="flex" align="middle">
                  {
                    envList.map((item, i) => (
                      <Col span={11} key={item.id}>
                        <Checkbox value={item.id}>{item.name}</Checkbox>
                      </Col>
                    ))
                  }
                </Row>
              </Checkbox.Group>
            )}
          </FormItem>
        </Form>
      </Modal>
    )
  }
  renderList() {
    const { list } = this.props;
    return (
      <Fragment>
        <Row gutter={24}>
          {
            list.length ? list.map((item, i) => (
              <Col key={i} md={8} sm={12}>
                <Card className={`${styles.marginB} ${styles.listItem}`} hoverable style={{ backgroundColor: COLOR_SHOW[item.projectId % 5] }}>
                  <Row onClick={() => this.onRouteTo('/project/detail', item)}>
                    <div className={styles.flexCenter}>
                      <strong className={styles.textFont16}>{item.projectName}</strong>
                      {/* <div className={styles.itemFirst} style={{ backgroundColor: COLOR_SHOW[item.projectId % 5] }}>{item.projectName.charAt(0).toUpperCase()}</div>
                      <strong className={styles.textFont16}>{item.projectName}</strong>
                      {
                        item.orderId > 0 &&
                        <div className={styles.marginL10}>
                          <Tag color="blue">置顶</Tag>
                        </div>
                      } */}
                    </div>
                    <div className={styles.itemDesc}>
                      <span className={styles.itemDescSpan}>{item.description}</span>
                    </div>
                    <div className={`${styles.flex} ${styles.itemDetailPro}`}>
                      <div className={`${styles.flex1} ${styles.itemDetailProItem}`}>
                        <p className={styles.textFont16}>服务</p>
                        <p>{item.appCount}</p>
                      </div>
                      <div className={`${styles.flex1} ${styles.itemDetailProItem}`}>
                        <p className={styles.textFont16} >用户</p>
                        <p>{item.userCount}</p>
                      </div>
                      <div className={`${styles.flex1} ${styles.itemDetailProItem}`}>
                        <p className={styles.textFont16}>环境</p>
                        <p>{item.envCount}</p>
                      </div>
                    </div>
                    {/* <Row type="flex" className={styles.itemDetailPro}>
                      <Col className={`${styles.textC}`} span={8} className={styles.itemDetailProItem}>
                        <div >
                          <p className={styles.textFont16}>服务</p>
                          <p>{item.appCount}</p>
                        </div>
                      </Col>
                      <Col className={styles.textC} span={8} className={styles.itemDetailProItem}>
                        <div>
                          <p className={styles.textFont16} >用户</p>
                          <p>{item.userCount}</p>
                        </div>
                      </Col>
                      <Col className={styles.textC} span={8} className={styles.itemDetailProItem}>
                        <div>
                          <p className={styles.textFont16}>环境</p>
                          <p>{item.envCount}</p>
                        </div>
                      </Col>
                    </Row> */}
                  </Row>
                  {/* <div className={styles.moreOprea}>
                    <Dropdown overlay={this.renderMoreOpera(item, i)} placement="bottomCenter">
                      <Icon type="ellipsis" style={{ fontSize: 25 }} />
                    </Dropdown>
                  </div> */}
                </Card>
              </Col>
            )) :
              <Col span={8}>
                {
                  isAdmin() &&
                  <Button type="dashed" className={styles.listItem} onClick={this.onAdd}>
                    <Icon type="plus" />
                    <span>新建项目</span>
                  </Button>
                }
              </Col>
          }
        </Row>
      </Fragment>
    )
  }
  renderMoreOpera(item, i) {
    return (
      <Menu>
        <Menu.Item onClick={() => { this.onTop(item) }}>
          <span>{item.orderId > 0 ? '取消置顶' : '置顶'}</span>
        </Menu.Item>
      </Menu>
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
export default Form.create()(connect(({ project, loading, auth, env }) => ({
  list: project.projectList,
  userAll: auth.userAll,
  envList: env.envList,
  loading: loading.effects["project/projectList"]
}))(Project));

