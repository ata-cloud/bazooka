import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Table, Button, Popconfirm, Modal, Input, Form, Select, Spin, message } from 'antd';
import { connect } from 'dva';
import { CARD_TITLE_BG } from "@/common/constant";
import styles from '@/pages/index.less';
import { project } from '@/services/project';
import router from 'umi/router';
import { isAdmin } from '@/utils/utils';

const { confirm } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
class ProjectConfig extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showDeleteModal: false,
      showUserAdd: false,
      userAddLoading: false,
      deleteProLoading: false
    };
  }
  componentDidMount() {
    const { devUserList } = this.props;
    if (Object.getOwnPropertyNames(devUserList).length === 0) {
      this.onFetchDevUserList();
    }
  }
  onFetchDevUserList = (params) => {
    const { projectId, dispatch } = this.props;
    dispatch({
      type: 'project/devUserList',
      payload: { projectId, ...params }
    })
  }
  //添加参与人
  onAddOk = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          userAddLoading: true
        })
        let userIds = [];
        values.userIds && values.userIds.map((vo) => {
          userIds.push({
            userId: vo.key,
            userName: vo.label
          })
        })
        this.onAddDecUser({ ...values, userIds })
      }
    });
  }
  //添加参与人
  onAddDecUser = async (params) => {
    const { projectId } = this.props;
    let res = await project.addDevUser({ projectId: projectId, ...params });
    this.setState({
      userAddLoading: false,
      showUserAdd: false
    })
    this.onFetchDevUserList();
    if (res && res.code == '1') {
      message.success('添加成功');
    }
  }
  //删除参与人
  onDeleteUser = async (record) => {
    const { projectId } = this.props;
    let res = await project.deleteDevUser({ projectId, userId: record.userId });
    if (res && res.code == '1') {
      message.success('删除成功');
      this.onFetchDevUserList();
    }
  }
  //删除项目
  onDeletePro = () => {
    this.setState({
      showDeleteModal: true
    })
  }
  //确认删除项目
  onDeleteProOk = (e) => {
    const { proInfo } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (proInfo.projectName !== values.name) {
          message.warning('请输入正确的项目名')
          return
        }
        this.onDeleteProject(values)
      }
    });
  }
  onDeleteProject = async (params) => {
    this.setState({
      deleteProLoading: true
    })
    const { projectId } = this.props;
    let res = await project.deleteProject({ projectId });
    this.setState({
      deleteProLoading: false
    })
    if (res && res.code == '1') {
      message.success('删除成功');
      router.goBack();
    }
  }
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...sort,
      pageNo: parseInt(current),
      pageSize: parseInt(pageSize),
    };
    this.onFetchDevUserList(params);
  };
  onCancel = () => {
    this.setState({
      showDeleteModal: false
    })
  }
  onShowUserAdd = () => {
    this.setState({
      showUserAdd: true
    })
  }
  onUserAddCancel = () => {
    this.setState({
      showUserAdd: false
    })
  }
  // --------------------渲染 --------------------------------------------------
  renderDeletePro() {
    const { showDeleteModal, deleteProLoading } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { proInfo } = this.props;
    return (
      <Modal
        visible={showDeleteModal}
        title="删除项目"
        okText="确认删除"
        okType="danger"
        onOk={this.onDeleteProOk}
        onCancel={this.onCancel}
        confirmLoading={deleteProLoading}
      >
        <p>请输入项目名“<span className={styles.textError}>{proInfo.projectName}</span>”确认删除项目</p>
        <p>删除项目将会删除项目下属的所有服务，相关的服务容器会被关停，代码仓库和镜像会被保留。 删除操作不可逆，请谨慎操作</p>
        <Form className={styles.antAdvancedSearchForm} onSubmit={this.onDeleteProOk}>
          <FormItem>
            {getFieldDecorator('name', {
              rules: [
                { required: true, message: "请输入项目名" }
              ]
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    )
  }
  renderUserAdd() {
    const { showUserAdd, userAddLoading } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { userAll } = this.props;
    return (
      <Modal
        title="添加参与人"
        visible={showUserAdd}
        onOk={this.onAddOk}
        onCancel={this.onUserAddCancel}
        confirmLoading={userAddLoading}
        maskClosable={false}
      >
        <Form className={styles.antAdvancedSearchForm} onSubmit={this.onAddOk}>
          <FormItem>
            {getFieldDecorator('userIds', {
              rules: [
                { required: true, message: "请选择参与人" }
              ]
            })(
              <Select mode="multiple" allowClear showSearch optionFilterProp="children" placeholder="请选择参与人" labelInValue>
                {
                  userAll && userAll.rows && userAll.rows.map((item) => (
                    <Option value={item.userId} key={item.userId}>{item.realName}</Option>
                  ))
                }
              </Select>
            )}
            {/* {getFieldDecorator('userIds', {
              rules: [
                { required: true, message: "请选择参与人" }
              ]
            })(
              <Select
                mode="multiple"
                placeholder="请选择参与人"
                notFoundContent={userLoading ? <Spin size="small" /> : null}
                filterOption={false}
                onSearch={this.onFetchUser}
                style={{ width: '100%' }}
              >
                {userList && userList.rows && userList.rows.map(item => (
                  <Option key={item.userId} value={item.userId}>{item.realName}</Option>
                ))}
              </Select>
            )} */}
          </FormItem>
        </Form>
      </Modal>
    )
  }
  renderTable() {
    // const { list } = this.state;
    const { devUserList, loading, proInfo } = this.props;
    let isMaster = isAdmin() || proInfo.adminUserRole == "USER_PROJECT_MASTER";
    let oprea = isMaster ? {
      title: '操作',
      dataIndex: 'opera',
      render: (text, record) => (
        <div>
          <Popconfirm
            title="确定删除吗?"
            onConfirm={() => this.onDeleteUser(record)}
            okText="确定"
            cancelText="取消"
          >
            <a>删除</a>
          </Popconfirm>
        </div>
      ),
    } : {};
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
      },
      {
        title: '全名',
        dataIndex: 'realName',
      },
      {
        title: '邮箱',
        dataIndex: 'email'
      },
      {
        ...oprea
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={devUserList.rows || []}
        onChange={this.onTableChange}
        loading={loading}
        pagination={{
          // showQuickJumper: true,
          // pageSizeOptions: ['10', '20', '30', '50'],
          total: devUserList.totalCount || 0,
          showTotal: (total, range) =>
            `共${devUserList.totalCount || 0}条，当前${devUserList.pageNum ? devUserList.pageNum : 1}/${
            devUserList.totalPage ? devUserList.totalPage : 1
            }页`,
          // showSizeChanger: true,
          current: devUserList.pageNum ? devUserList.pageNum : 1,
          pageSize: devUserList.pageSize ? devUserList.pageSize : 10,
        }}
        rowKey={record => {
          return record.userId;
        }}
      >
      </Table>
    )
  }
  render() {
    const { showDeleteModal, showUserAdd } = this.state;
    const { proInfo } = this.props;
    return (
      <Fragment>
        <Card title="项目参与人列表" extra={
          <Fragment>
            {
              (isAdmin() || proInfo.adminUserRole == "USER_PROJECT_MASTER") ?
                <Button type="primary" onClick={this.onShowUserAdd}>添加参与人</Button>
                : null
            }
          </Fragment>

        }>
          {this.renderTable()}
        </Card>
        {
          isAdmin() &&
          <Card title="删除项目" className={styles.marginT} hoverable>
            <p>删除项目将会删除项目下属的所有服务，相关的服务容器会被关停，代码仓库和镜像会被保留。 删除操作不可逆，请谨慎操作</p>
            <Button type="danger" onClick={this.onDeletePro}>删除项目</Button>
          </Card>
        }
        {showDeleteModal && this.renderDeletePro()}
        {showUserAdd && this.renderUserAdd()}
      </Fragment>
    );
  }
}
export default Form.create()(connect(({ project, auth, loading }) => ({
  devUserList: project.devUserList,
  userAll: auth.userAll,
  proInfo: project.proInfo,
  loading: loading.effects["project/devUserList"],
}))(ProjectConfig));
